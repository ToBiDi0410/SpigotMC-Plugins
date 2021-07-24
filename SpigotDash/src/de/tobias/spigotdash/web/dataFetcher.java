package de.tobias.spigotdash.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.input.ReversedLinesFileReader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;

import de.tobias.spigotdash.main;
import de.tobias.spigotdash.listener.JoinTime;
import de.tobias.spigotdash.utils.databaseManager;
import de.tobias.spigotdash.utils.errorCatcher;
import de.tobias.spigotdash.utils.pluginConsole;
import de.tobias.spigotdash.utils.pluginManager;

public class dataFetcher {

	public static File serverDir = new File(main.pl.getDataFolder().getAbsoluteFile().getParentFile().getParent());
	public static File serverPropFile = new File(serverDir, "server.properties");
	public static File bukkitPropFile = new File(serverDir, "Bukkit.yml");
	
	public static Runtime runtime = Runtime.getRuntime();
	public static MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

	public static long last_tick_time = 0;
	public static float tps = 0;
	public static float tps_avg = 0;
	public static float tps_avg_gen = 0;
	public static float tps_passed = 0;

	
	// ** CONTROLS **
	public static HashMap<String, String> getServerProps() {
		try {
			List<String> lines = Files.readAllLines(serverPropFile.toPath());
			HashMap<String, String> values = new HashMap<>();

			for (String s : lines) {
				if(!s.contains("#") && s.contains("=")) {
					String key = s.split("=")[0];
					values.put(key, s.replace(key + "=", ""));
				}
			}
			
			return values;
		} catch (Exception ex) {
			pluginConsole.sendMessage("&cFailed to Get Server Properties: ");
			errorCatcher.catchException(ex, false);
			return null;
		}
	}
	
	public static boolean saveServerProps(HashMap<String, String> newvals) {
		try {
			serverPropFile.delete();
			serverPropFile.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(serverPropFile);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			for (Entry<String, String> entry : newvals.entrySet()) {
				String line = entry.getKey() + "=" + entry.getValue();
				bw.write(line);
				bw.newLine();
			}
			
			bw.close();
			fos.close();
			
			return true;
		} catch (Exception ex) {
			pluginConsole.sendMessage("&cFailed to Save Server Properties: ");
			errorCatcher.catchException(ex, false);
			return false;
		}
	}
	
	public static boolean modifyServerPropertie(String key, Object val) {
		try {
			HashMap<String, String> newValues = getServerProps();
			
			if(newValues.containsKey(key)) {
				newValues.replace(key, val.toString());
			} else {
				newValues.put(key, val.toString());
			}
						
			saveServerProps(newValues);
			
			return true;
		} catch (Exception ex) {
			pluginConsole.sendMessage("&cFailed to Modify Server Properties: ");
			errorCatcher.catchException(ex, false);
			return false;
		}
	}
	
	public static String getServerPropertie(String key) {
		return getServerProps().get(key);
	}
	
	public static boolean modifyBukkitPropertie(String key, Object val) {
		try {
			YamlConfiguration yamlProps = YamlConfiguration.loadConfiguration(bukkitPropFile);		
			yamlProps.set(key, val);
			yamlProps.save(bukkitPropFile);
			return true;
		} catch (IOException ex) {
			pluginConsole.sendMessage("&cFailed to Modify Server Properties: ");
			errorCatcher.catchException(ex, false);
			return false;
		}
	}
	
	public static Object getBukkitPropertie(String key) {
		YamlConfiguration yamlProps = YamlConfiguration.loadConfiguration(bukkitPropFile);
		return yamlProps.get(key);
	}
	
	// ** PLUGINS **
	public static ArrayList<HashMap<String, Object>> getPluginsForWeb() {
		ArrayList<HashMap<String, Object>> plugins = new ArrayList<HashMap<String, Object>>();
		
		for(Plugin pl : pluginManager.getAllPluginsWithDisabled()) {
			HashMap<String, Object> plugin_info = new HashMap<>();
			plugin_info.put("enabled", pl.isEnabled());
			plugin_info.put("name", pl.getName());
			plugin_info.put("description", pl.getDescription().getDescription());
			plugin_info.put("version", pl.getDescription().getVersion());
			plugin_info.put("authors", pl.getDescription().getAuthors());
			plugin_info.put("website", pl.getDescription().getWebsite());	
			plugin_info.put("apiversion", pl.getDescription().getAPIVersion());
			plugin_info.put("known", false);
			plugins.add(plugin_info);
		}
		return plugins;
	}
	
	
	public static ArrayList<String> getPluginFileNames() {
		ArrayList<String> files = new ArrayList<String>();

		for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
			files.add(new java.io.File(pl.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getName());
		}

		return files;
	}
	
	// ** FILES **
	public static ArrayList<HashMap<String, Object>> getFilesInPath(String path) {
		File dir = new File(main.pl.getDataFolder().getParentFile().getParent(), path);
		ArrayList<HashMap<String, Object>> files = new ArrayList<>();
		 
		if(dir.exists()) {
			if(!dir.isFile()) {
				File[] files_obj = dir.listFiles();
				for(File f : files_obj) {
					files.add(fileToWebHashMap(f));
				}
				return files;
			} else {
				return null;
			}
		} else {
			return null;
		}
		
	}
	
	public static HashMap<String, Object> fileToWebHashMap(File file) {
		HashMap<String, Object> details = new HashMap<>();
		
		details.put("DIR", file.isDirectory());
		details.put("EXISTS", file.exists());
		details.put("NAME", file.getName());
		details.put("LAST_CHANGED", file.lastModified());
		
		return details;
	}
	
	public static File getFileWithPath(String path) {
		return new File(main.pl.getDataFolder().getParentFile().getParent(), path);
	}
	
	// ** TPS MEASUREMENT

	public static float getTPS() {
		return tps_avg;
	}

	public static Runnable getTPSRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				long diff = System.currentTimeMillis() - last_tick_time;
				float seconds = (diff / (1000.00f));

				tps = 20 / seconds;
				tps_avg_gen += (float) tps;
				tps_passed += 1;

				if (tps_passed >= 5) {
					tps_avg = (float) tps_avg_gen / (float) tps_passed;
					tps_avg_gen = 0;
					tps_passed = 0;
				}
				// Bukkit.getConsoleSender().sendMessage("TPS: " + tps);
				// Bukkit.getConsoleSender().sendMessage("AVG TPS: " + tps_avg);

				last_tick_time = System.currentTimeMillis();
			}

		};
	}

	// ** PLAYER **
	public static ArrayList<HashMap<String, Object>> getPlayersForWeb() {
		ArrayList<HashMap<String, Object>> players = new ArrayList<HashMap<String, Object>>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			HashMap<String, Object> playerinfo = new HashMap<String, Object>();
			playerinfo.put("UUID", p.getUniqueId());
			playerinfo.put("Name", p.getName());
			playerinfo.put("Displayname", p.getDisplayName());
			playerinfo.put("Location", locationToHashMap(p.getLocation()));
			playerinfo.put("Health", p.getHealth());
			playerinfo.put("Health_Max", p.getHealthScale());
			playerinfo.put("Food", p.getFoodLevel());
			playerinfo.put("Jointime", JoinTime.joinTimes.get(p.getUniqueId().toString()));
			players.add(playerinfo);
		}
		return players;
	}

	public static HashMap<String, Object> locationToHashMap(Location l) {
		HashMap<String, Object> loc = new HashMap<String, Object>();
		loc.put("X", l.getBlockX());
		loc.put("Y", l.getBlockY());
		loc.put("Z", l.getBlockZ());
		loc.put("PITCH", l.getPitch());
		loc.put("YAW", l.getYaw());
		loc.put("WORLD", l.getWorld().getName());
		return loc;
	}

	// ** LOG **
	public static List<String> getLog(Integer linecount) {
		File logfile = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/logs/", "latest.log");
		try {
			List<String> lines = new ArrayList<String>();
			int counter = 0;
			try (ReversedLinesFileReader reader = new ReversedLinesFileReader(logfile, Charset.forName("utf-8"));) {
				while (counter < linecount) {
					String line = reader.readLine();
					if(line == null) break;
					lines.add(line);
					counter++;
				}
			}
			lines = Lists.reverse(lines);	
			return lines;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	// ** WARNINGS **
	public static boolean pluginsDisabled() {
		for (Plugin pl : Bukkit.getPluginManager().getPlugins()) {
			if (!pl.isEnabled()) {
				return true;
			}
		}
		return false;
	}

	public static boolean unusedJARFiles() {
		File pluginsFolder = main.pl.getDataFolder().getParentFile();
		File[] jarFiles = pluginsFolder.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".jar");
		    }
		});
		return (jarFiles.length != Bukkit.getPluginManager().getPlugins().length);
	}
	
	// ** PERFORMANCE GENERAL **
	public static Object getPerformanceDataForWeb() {
		ResultSet data = databaseManager.query("SELECT * FROM `PERFORMANCE` WHERE `DATETIME` >= Datetime('now', '-10 minutes') ORDER BY date(`DATETIME`) ASC");
		if (data != null) {
			try {
				ArrayList<HashMap<String, Object>> data_maps = new ArrayList<HashMap<String, Object>>();
				ArrayList<String> columns = getColumsFromResultSet(data);
				while (data.next()) {
					data_maps.add(resultSetToHashMap(data, columns));
				}
				return data_maps;
			} catch (Exception ex) {
				ex.printStackTrace();
				return "ERR";
			}
		} else {
			return "";
		}
	}

	// ** WORLD FUNCTIONS **
	public static Integer getPlayerCount() {
		return Bukkit.getOnlinePlayers().size();
	}
	
	public static Integer getPluginCount() {
		return Bukkit.getPluginManager().getPlugins().length;
	}
	
	public static long getOntime() {
		long uptime = System.currentTimeMillis() - main.latestStart;
		return uptime;
	}

	public static Integer getTotalEntities() {
		int ent = 0;
		for (World w : Bukkit.getWorlds()) {
			ent += w.getEntities().size();
		}
		return ent;
	}

	public static Integer getTotalChunks() {
		int ch = 0;
		for (World w : Bukkit.getWorlds()) {
			ch += w.getLoadedChunks().length;
		}
		return ch;
	}

	public static Integer getWorldCount() {
		return Bukkit.getWorlds().size();
	}

	// ** CPU LOAD FUNCTIONS **

	public static double getProcessCPULoad() {
		try {
			ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });

			if (list.isEmpty())
				return Double.NaN;

			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();

			if (value == -1.0)
				return Double.NaN;
			return ((int) (value * 1000) / 10.0);
		} catch (Exception ex) {
			return 0;
		}

	}

	public static double getSystemCPULoad() {
		try {
			ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			AttributeList list = mbs.getAttributes(name, new String[] { "SystemCpuLoad" });

			if (list.isEmpty())
				return Double.NaN;

			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();

			if (value == -1.0)
				return Double.NaN;
			return ((int) (value * 1000) / 10.0);
		} catch (Exception ex) {
			return 0;
		}

	}

	// ** MEMORY FUNCTIONS ** //

	public static long getFreeMemory() {
		long usedMemory = runtime.freeMemory();
		return bytesToMB(usedMemory);
	}

	public static long getAllocatedMemory() {
		long alloctedMemory = runtime.totalMemory();
		return bytesToMB(alloctedMemory);
	}

	public static long getMaxMemory() {
		long maxMemory = runtime.maxMemory();
		return bytesToMB(maxMemory);
	}

	public static long getUsedMemory() {
		long allocated = getAllocatedMemory();
		long free = getFreeMemory();
		long used = allocated - free;
		return used;
	}

	// ** GENERAL HELPERS **

	public static long bytesToMB(long bytes) {
		return bytes / 1048576;
	}
	
	public static HashMap<String, Object> resultSetToHashMap(ResultSet rs, ArrayList<String> columns) {
		HashMap<String, Object> hs = new HashMap<>();
		try {
			for (String s : columns) {
				hs.put(s, rs.getObject(s));
			}
		} catch (Exception ex) {
			return hs;
		}

		return hs;
	}

	public static ArrayList<String> getColumsFromResultSet(ResultSet rs) {
		ArrayList<String> columns = new ArrayList<String>();
		try {
			ResultSetMetaData rsdm = rs.getMetaData();
			int columnCount = rsdm.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String name = rsdm.getColumnName(i);
				columns.add(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columns;
	}
	
	public static int coutNumberOfOccurences(char Char, String str) {
		int count = 0;
		 
		for (int i = 0; i < str.length(); i++) {
		    if (str.charAt(i) == Char) {
		        count++;
		    }
		}
		return count;
	}
	
	public static String replaceLast(String string, String toReplace, String replacement) {
	    int pos = string.lastIndexOf(toReplace);
	    if (pos > -1) {
	        return string.substring(0, pos)
	             + replacement
	             + string.substring(pos + toReplace.length());
	    } else {
	        return string;
	    }
	}

	public static UUID uuidFromUUIDWithoutDashes(String dashless) {
		return UUID.fromString(insertDashUUID(dashless));
	}
	
	public static String insertDashUUID(String uuid) {
		//FROM: https://bukkit.org/threads/java-adding-dashes-back-to-minecrafts-uuids.272746/
		StringBuffer sb = new StringBuffer(uuid);
		sb.insert(8, "-");
		 
		sb = new StringBuffer(sb.toString());
		sb.insert(13, "-");
		 
		sb = new StringBuffer(sb.toString());
		sb.insert(18, "-");
		 
		sb = new StringBuffer(sb.toString());
		sb.insert(23, "-");
		 
		return sb.toString();
		}
}

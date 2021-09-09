package de.tobias.spigotdash;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import de.tobias.spigotdash.listener.AltJoin;
import de.tobias.spigotdash.listener.JoinTime;
import de.tobias.spigotdash.utils.configuration;
import de.tobias.spigotdash.utils.jsonDatabase;
import de.tobias.spigotdash.utils.pluginConsole;
import de.tobias.spigotdash.utils.taskManager;
import de.tobias.spigotdash.utils.translations;
import de.tobias.spigotdash.web.WebServer;

public class main extends JavaPlugin {

	public static WebServer webserver;
	public static jsonDatabase cacheFile;
	public static Plugin pl;
	public static Metrics metrics;
	public static long latestStart = 0;
	
	public void onEnable() {
		try {
			pl = this;
			pluginConsole.sendMessage("&7----------- [  " + pluginConsole.CONSOLE_PREFIX + "&7] -----------");
			pluginConsole.sendMessage("&7Author(s): &b" + this.getDescription().getAuthors().toString());
			pluginConsole.sendMessage("&7Version: &6" + this.getDescription().getVersion() + " &7(API: &6" + this.getDescription().getAPIVersion() + "&7)");
			pluginConsole.sendMessage("&cThank you for using this Plugin <3");
			pluginConsole.sendMessage("&7----------- [  " + pluginConsole.CONSOLE_PREFIX + "&7] -----------");

			pluginConsole.sendMessage("Starting Metrics...");
			metrics = new Metrics(this, 11869);
			pluginConsole.sendMessage("&aMetrics started!");
			
			//DATA FOLDER CREATING
			if (!this.getDataFolder().exists()) getDataFolder().mkdir();
			
			//CONFIGURATION SETUP
			configuration.init();
			
			//TRANSLATION LOADING
			translations.load();

			//DATABASE SETUP
			pluginConsole.sendMessage("Loading Cache File...");
			File cache = new File(main.pl.getDataFolder(), "cache.json");
			if(!cache.exists()) { cache.getParentFile().mkdirs(); cache.createNewFile(); FileUtils.write(cache, "{PERFORMANCE_DATA: []}", StandardCharsets.UTF_8);}
			cacheFile = new jsonDatabase(cache);
			cacheFile.read();
			pluginConsole.sendMessage("&aCache File loaded!");
			
			//WEBSERVER SETUP
			webserver = new WebServer((Integer) configuration.CFG.get("PORT"));
			webserver.setup();
			
			//TASKS SETUP
			taskManager.startTasks();

			//ONTIME TRACKER
			Bukkit.getPluginManager().registerEvents(new JoinTime(), main.pl);
			JoinTime.enableSet();
			
			//ALT DETECTOR
			Bukkit.getPluginManager().registerEvents(new AltJoin(), main.pl);
			
			latestStart = System.currentTimeMillis();
			
			pluginConsole.sendMessage("&5Everything (seems to be) done!");
			
			/*final NgrokClient ngrokClient = new NgrokClient.Builder().build();
			final Tunnel httpTunnel = ngrokClient.connect(new CreateTunnel.Builder().withAddr(webserver.port).withProto(Proto.HTTP).build());
			pluginConsole.sendMessage("&c[BETA] NGROK Url: " + httpTunnel.getPublicUrl());*/

		} catch (Exception ex) {
			pluginConsole.sendMessage("&7----------- [  " + pluginConsole.CONSOLE_PREFIX + "&7] -----------");
			pluginConsole.sendMessage("&cINIT FAILURE! This error is currently unrecoverable!");
			ex.printStackTrace();
			pluginConsole.sendMessage("&7----------- [  " + pluginConsole.CONSOLE_PREFIX + "&7] -----------");

			try {
				taskManager.stopTasks();
			} catch(Exception eex) {}
			
			try {
				webserver.destroy();
			} catch(Exception eex) {}

		}

	}

	public void onDisable() {
		cacheFile.save();
		taskManager.stopTasks();
		webserver.destroy();
		configuration.save();
	}
}

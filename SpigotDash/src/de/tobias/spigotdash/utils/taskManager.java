package de.tobias.spigotdash.utils;

import org.bukkit.Bukkit;

import de.tobias.spigotdash.main;
import de.tobias.spigotdash.web.dataFetcher;

public class taskManager {

	public static int DATA_taskID = 0;
	public static int TPS_taskID = 0;
	public static int UPDATE_taskID = 0;

	public static long lastUpdate = 0;
	
	public static void startTasks() {
		DATA_taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(main.pl, new Runnable() {
			@Override
			public void run() {
				if(lastUpdate + 1000*15 <= System.currentTimeMillis()) {
					//Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "Data submitted into Database!");
					databaseManager.exec("INSERT INTO `PERFORMANCE` (`DATETIME`, `CPU_LOAD_SYSTEM`, `CPU_LOAD_PROCESS`, `MEMORY_USED`, `MEMORY_FREE`, `MEMORY_MAX`, `MEMORY_ALLOCATED`, `TPS`, `WORLD_CHUNKS`, `WORLD_ENTITIES`, `WORLD_PLAYERS`, `WORLD_COUNT`) VALUES ( DATETIME('now'), '" + dataFetcher.getSystemCPULoad() + "', '" + dataFetcher.getProcessCPULoad() + "', '" + dataFetcher.getUsedMemory() + "', '" + dataFetcher.getFreeMemory() + "', '" + dataFetcher.getMaxMemory() + "', '" + dataFetcher.getAllocatedMemory() + "', '" + dataFetcher.getTPS() + "', '" + dataFetcher.getTotalChunks() + "', '" + dataFetcher.getTotalEntities() + "', '" + dataFetcher.getPlayerCount() + "', '" + dataFetcher.getWorldCount() + "')");
					databaseManager.exec("DELETE FROM `PERFORMANCE` WHERE `DATETIME` <= datetime('now', '-1 hour')");
					
					// ** NOTIFICATIONS **
					notificationManager.manageNotifications();
					if(dataFetcher.getTPS() < 17.0f) {
						notificationManager.addNotification("LOW_TPS_WARN", "WARNING", "SpigotDash", "Low TPS", "You server is currently running at <b>low TPS</b>! Check the Performance tab for further information!", 1);
					}
					
					if(dataFetcher.pluginsDisabled()) {
						notificationManager.addNotification("PLUGINS_DISABLED_WARN", "WARNING", "SpigotDash", "Plugins disabled", "One or more Plugins are currently <b>disabled or unloaded</b><br>Please check the Log for Errors if this is not intended!", 1);
					}
					
					if(dataFetcher.unusedJARFiles()) {
						notificationManager.addNotification("PLUGINS_JARUNLOADED_WARN", "ERROR", "SpigotDash", "Unloaded JAR Files", "One or more JAR Files in the Plugins folder are currently <b>unloaded or invalid</b>!", -1);
					}
					
					if(updater.update_available == true) {
						notificationManager.addNotification("UPDATE_AVAILABLE", "INFO", "SpigotDash", "Update available", "An new Update for this Plugin was found!<br>Please visit <a href='https://www.spigotmc.org/resources/spigotdash-webinterface-performance-monitor.93710/'><b>SpigotMC</b></a> for more Information", 10);
					}
					
					lastUpdate = System.currentTimeMillis();
				}
				
			}
	    
	    }, 20L, 10L);
		
	    TPS_taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main.pl, dataFetcher.getTPSRunnable(), 20L, 20L);
	    UPDATE_taskID = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(main.pl, updater.getUpdateRunnable(), 20L, 20L * 60 * 30);
	}
	
	public static void stopTasks() {
		stopTask(DATA_taskID);
		stopTask(TPS_taskID);
		stopTask(UPDATE_taskID);
	}
	
	public static void stopTask(int id) {
		if(id != 0) {
			Bukkit.getScheduler().cancelTask(id);
		}
	}
}

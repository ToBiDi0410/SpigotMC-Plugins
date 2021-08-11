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
					
					//Efafaef
					
					//** NOTIFICATIONS **
					notificationManager.manageNotifications();
					if(dataFetcher.getTPS() < 17.0f) {
						notificationManager.addNotification("LOW_TPS_WARN", "WARNING", "SpigotDash", translations.replaceTranslationsInString("%T%NOTIFICATION_LOWTPS_TITLE%T%"), translations.replaceTranslationsInString("%T%NOTIFICATION_LOWTPS_CONTENT%T%"), 1);
					}
					
					if(dataFetcher.pluginsDisabled()) {
						notificationManager.addNotification("PLUGINS_DISABLED_WARN", "WARNING", "SpigotDash", translations.replaceTranslationsInString("%T%NOTIFICATION_DISABLEDPLUGINS_TITLE%T%"), translations.replaceTranslationsInString("%T%NOTIFICATION_DISABLEDPLUGINS_CONTENT%T%"), -1);
					}
					
					if(dataFetcher.unusedJARFiles()) {
						notificationManager.addNotification("PLUGINS_JARUNLOADED_WARN", "DANGER", "SpigotDash", translations.replaceTranslationsInString("%T%NOTIFICATION_UNLOADEDJARS_TITLE%T%"), translations.replaceTranslationsInString("%T%NOTIFICATION_UNLOADEDJARS_CONTENT%T%"), -1);
					}
					
					if(updater.update_available == true) {
						notificationManager.addNotification("UPDATE_AVAILABLE", "INFO", "SpigotDash", translations.replaceTranslationsInString("%T%NOTIFICATION_UPDATE_TITLE%T%"), translations.replaceTranslationsInString("%T%NOTIFICATION_UPDATE_CONTENT%T%"), -1);
					}
					
					lastUpdate = System.currentTimeMillis();
				}
				
			}
	    
	    }, 20L, 10L);
		
	    TPS_taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main.pl, dataFetcher.getTPSRunnable(), 20L, 20L);
	    
	    Integer updateTime = Integer.parseInt(configuration.CFG.get("UPDATE_REFRESH_TIME").toString());
	    pluginConsole.sendMessage("&7Set Autoupdater time to: &6" + updateTime + " &7Minutes");
	    UPDATE_taskID = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(main.pl, updater.getUpdateRunnable(), 20L, 20L * 60 * updateTime);
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

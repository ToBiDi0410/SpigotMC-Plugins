package de.tobias.spigotdash;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobias.spigotdash.listener.JoinTime;
import de.tobias.spigotdash.utils.configuration;
import de.tobias.spigotdash.utils.databaseManager;
import de.tobias.spigotdash.utils.pluginConsole;
import de.tobias.spigotdash.utils.taskManager;
import de.tobias.spigotdash.web.WebServer;

public class main extends JavaPlugin {

	public static WebServer webserver;
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

			//DATABASE SETUP
			databaseManager.connect();
			databaseManager.setupDB();
			
			//WEBSERVER SETUP
			webserver = new WebServer((Integer) configuration.CFG.get("PORT"));
			webserver.setup();
			
			//TASKS SETUP
			taskManager.startTasks();

			//ONTIME TRACKER
			Bukkit.getPluginManager().registerEvents(new JoinTime(), main.pl);
			JoinTime.enableSet();
			
			latestStart = System.currentTimeMillis();

			pluginConsole.sendMessage("&5Everything (seems to be) done!");
		} catch (Exception ex) {
			pluginConsole.sendMessage("&7----------- [  " + pluginConsole.CONSOLE_PREFIX + "&7] -----------");
			pluginConsole.sendMessage("&cINIT FAILURE! This error is currently unrecoverable!");
			ex.printStackTrace();
			pluginConsole.sendMessage("&7----------- [  " + pluginConsole.CONSOLE_PREFIX + "&7] -----------");
			
			try {
				databaseManager.close();
			} catch(Exception eex) {}

			try {
				taskManager.stopTasks();
			} catch(Exception eex) {}
			
			try {
				webserver.destroy();
			} catch(Exception eex) {}

		}

	}

	public void onDisable() {
		databaseManager.close();
		taskManager.stopTasks();
		webserver.destroy();
		configuration.save();
	}
}

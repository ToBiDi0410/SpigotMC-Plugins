package de.tobias.spigotdash;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.tobias.spigotdash.listener.JoinTime;
import de.tobias.spigotdash.utils.configuration;
import de.tobias.spigotdash.utils.databaseManager;
import de.tobias.spigotdash.utils.taskManager;
import de.tobias.spigotdash.utils.updater;
import de.tobias.spigotdash.web.AuthHandler;
import de.tobias.spigotdash.web.WebServer;

public class main extends JavaPlugin {

	public static WebServer webserver;
	public static Plugin pl;
	public static String CONSOLE_PREFIX = "§7[§bSpigotDash§7] §7";

	public void onEnable() {
		try {
			pl = this;
			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§7----------- [ §6SpigotDash§7 ] -----------");
			Bukkit.getConsoleSender()
					.sendMessage(CONSOLE_PREFIX + "§7Author(s): §b" + this.getDescription().getAuthors().toString());
			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§7Version: §6" + this.getDescription().getVersion()
					+ " §7(API: §6" + this.getDescription().getAPIVersion() + "§7)");
			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§cThank you for using this Plugin <3");
			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§7----------- [ §6SpigotDash§7 ] -----------");

			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "Starting Metrics...");
			@SuppressWarnings("unused")
			Metrics metrics = new Metrics(this, 11869);
			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§aMetrics started!");
			
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

			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§5Everything (seems to be) done!");
		} catch (Exception ex) {
			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "\n\n§7----------- [ §6SpigotDash§7 ] -----------");
			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§cINIT FAILURE! This error is currently unrecoverable!");
			ex.printStackTrace();
			Bukkit.getConsoleSender().sendMessage(CONSOLE_PREFIX + "§7----------- [ §6SpigotDash§7 ] -----------\n\n");
			
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

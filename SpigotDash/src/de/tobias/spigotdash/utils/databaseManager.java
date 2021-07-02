package de.tobias.spigotdash.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.Bukkit;

import de.tobias.spigotdash.main;

public class databaseManager {

	public static Connection conn = null;
	public static String DBFilePath = main.pl.getDataFolder().getAbsolutePath() + "/data.db";

	public static boolean connect() {
		Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "Connecting to SQLite Database...");
		try {
			String url = "jdbc:sqlite:" + DBFilePath;
			conn = DriverManager.getConnection(url);
			Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§aSuccessfully connected to Database!");
			return true;
		} catch (Exception ex) {
			Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§c[ERROR] Could not connect to Database:");
			ex.printStackTrace();
			return false;
		}
	}

	public static boolean close() {
		Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "Closing SQLite Database...");
		try {
			if (conn != null) {
				if (conn.isClosed() == false) {
					conn.close();
					conn = null;
				}
			}
			return true;
		} catch (Exception ex) {
			Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§c[ERROR] Could not disconnect from Database:");
			ex.printStackTrace();
			return false;
		}
	}
	
	public static boolean exec(String sql) {
		if(conn != null) {
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(sql);
				return true;
			} catch(Exception ex) {
				Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§c[ERROR] Failed to execute SQL Statement:");
				ex.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}
	
	public static ResultSet query(String sql) {
		if(conn != null) {
			try {
				Statement stmt = conn.createStatement();
				return stmt.executeQuery(sql);
			} catch(Exception ex) {
				Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§c[ERROR] Failed to execute SQL Query:");
				ex.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static boolean setupDB() {
		Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "Setting up SQLite Database...");
		exec("DROP TABLE IF EXISTS `PERFORMANCE`");
		boolean suc = (
		exec("CREATE TABLE IF NOT EXISTS `PERFORMANCE` ( `DATETIME` DATETIME, `CPU_LOAD_SYSTEM` INT NOT NULL , `CPU_LOAD_PROCESS` INT NOT NULL , `MEMORY_USED` INT NOT NULL , `MEMORY_FREE` INT NOT NULL , `MEMORY_MAX` INT NOT NULL , `MEMORY_ALLOCATED` INT NOT NULL , `TPS` INT NOT NULL , `WORLD_CHUNKS` INT NOT NULL , `WORLD_ENTITIES` INT NOT NULL, `WORLD_PLAYERS` INT NOT NULL, `WORLD_COUNT` INT NOT NULL )") &&
				true);
		if(suc) {
			Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§aSQLite Database setup!");
		} else {
			Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§cSQLite Database setup failed!");
		}
		return suc;
	}

}

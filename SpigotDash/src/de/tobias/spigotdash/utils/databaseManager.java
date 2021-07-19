package de.tobias.spigotdash.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import de.tobias.spigotdash.main;

public class databaseManager {

	public static Connection conn = null;
	public static String DBFilePath = main.pl.getDataFolder().getAbsolutePath() + "/data.db";

	public static boolean connect() {
		pluginConsole.sendMessage("Connecting to SQLite Database...");
		try {
			String url = "jdbc:sqlite:" + DBFilePath;
			conn = DriverManager.getConnection(url);
			pluginConsole.sendMessage("&aSuccessfully connected to Database!");
			return true;
		} catch (Exception ex) {
			pluginConsole.sendMessage("&c[ERROR] Could not connect to Database:");
			errorCatcher.catchException(ex, false);
			return false;
		}
	}

	public static boolean close() {
		pluginConsole.sendMessage("Closing SQLite Database...");
		try {
			if (conn != null) {
				if (conn.isClosed() == false) {
					conn.close();
					conn = null;
				}
			}
			return true;
		} catch (Exception ex) {
			pluginConsole.sendMessage("&c[ERROR] Could not disconnect from Database:");
			errorCatcher.catchException(ex, false);
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
				pluginConsole.sendMessage("&c[ERROR] Failed to execute SQL Statement:");
				errorCatcher.catchException(ex, false);
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
				pluginConsole.sendMessage("&c[ERROR] Failed to execute SQL Query:");
				errorCatcher.catchException(ex, false);
				return null;
			}
		} else {
			return null;
		}
	}
	
	public static boolean setupDB() {
		pluginConsole.sendMessage("Setting up SQLite Database...");
		exec("DROP TABLE IF EXISTS `PERFORMANCE`");
		boolean suc = (
		exec("CREATE TABLE IF NOT EXISTS `PERFORMANCE` ( `DATETIME` DATETIME, `CPU_LOAD_SYSTEM` INT NOT NULL , `CPU_LOAD_PROCESS` INT NOT NULL , `MEMORY_USED` INT NOT NULL , `MEMORY_FREE` INT NOT NULL , `MEMORY_MAX` INT NOT NULL , `MEMORY_ALLOCATED` INT NOT NULL , `TPS` INT NOT NULL , `WORLD_CHUNKS` INT NOT NULL , `WORLD_ENTITIES` INT NOT NULL, `WORLD_PLAYERS` INT NOT NULL, `WORLD_COUNT` INT NOT NULL )") &&
				true);
		if(suc) {
			pluginConsole.sendMessage("&aSQLite Database setup!");
		} else {
			pluginConsole.sendMessage("&cSQLite Database setup failed!");
		}
		return suc;
	}

}

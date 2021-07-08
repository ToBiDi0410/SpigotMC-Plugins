package de.tobias.spigotdash.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.ExceptionUtils;

import de.tobias.spigotdash.Metrics;
import de.tobias.spigotdash.main;

public class errorCatcher {

	
	public static boolean catchException(Exception ex, boolean halt) {
		pluginConsole.sendMessage("&6Reporting this error is not required! It will be transmitted to bStats and therefore to the Developer!");
		pluginConsole.sendMessage("&6If you want to report this error anyways, include the Information below!");
		pluginConsole.sendMessageWithoutPrefix("&c[---------- EXCEPTION ----------]");
		pluginConsole.sendMessageWithoutPrefix("&6Technical Details:");
		
		StackTraceElement rootcause = getRootCause(ex);
		String file = rootcause.getFileName();
		String message = ex.getStackTrace()[0].getClassName() + ": " + ex.getMessage();
		Integer line = rootcause.getLineNumber();
		pluginConsole.sendMessageWithoutPrefix("&cFiles/Classes: " + file);
		pluginConsole.sendMessageWithoutPrefix("&cLine: " + line);
		pluginConsole.sendMessageWithoutPrefix("&cMessage: " + message);
		pluginConsole.sendMessageWithoutPrefix("&cHalt: " + halt);
		pluginConsole.sendMessageWithoutPrefix("&cStacktrace:");
		ex.printStackTrace();
		pluginConsole.sendMessageWithoutPrefix("&c[---------- EXCEPTION ----------]\n");
		if(halt == true) {
			pluginConsole.sendMessage("&4The error above will disable this Plugin! Disabling..");
			Bukkit.getPluginManager().disablePlugin(main.pl);
		}
		
		transmitError(file, line, message);
		
		return true;
	}
	
	public static void transmitError(String file, Integer line, String message) {
		main.metrics.addCustomChart(new Metrics.DrilldownPie("errors", () -> {
	        Map<String, Map<String, Integer>> map = new HashMap<>();
	        Map<String, Integer> entry = new HashMap<>();
	        entry.put(file + ":" + line, 1);
	        map.put(message, entry);
	        return map;
	    }));
	}
	
	public static StackTraceElement getRootCause(Exception ex) {
		int i = 0;
		while(i < ex.getStackTrace().length) {
			StackTraceElement st = ex.getStackTrace()[i];
			if(st.getClassName().contains("de.tobias.spigotdash")) {
				return st;
			}
			i++;
		}
		return null;
	}
}

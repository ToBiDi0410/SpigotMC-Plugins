package de.tobias.spigotdash.utils;

import org.bukkit.Bukkit;

import com.google.common.io.Files;

import de.tobias.spigotdash.main;

public class errorCatcher {

	
	public static boolean catchException(Exception ex, boolean halt) {
		Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§6Reporting this error is not required! It will be transmitted to bStats and therefore to the Developer!");
		Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§6If you want to report this error anyways, include the Information below!");
		Bukkit.getConsoleSender().sendMessage("§c[---------- EXCEPTION ----------]");
		Bukkit.getConsoleSender().sendMessage("§6Technical Details:");
		String files = "";
		int i = 0;
		while(i < ex.getStackTrace().length) {
			StackTraceElement st = ex.getStackTrace()[i];
			files += "-".repeat(i) + st.getFileName() + "\n";
			i++;
		}
		Bukkit.getConsoleSender().sendMessage("§cFiles/Classes: \n" + files);
		Bukkit.getConsoleSender().sendMessage("§cLine: " + ex.getStackTrace()[0].getLineNumber());
		Bukkit.getConsoleSender().sendMessage("§cMessage: " + ex.getStackTrace()[0].getClassName() + ": " + ex.getMessage());
		Bukkit.getConsoleSender().sendMessage("§cHalt: " + halt);
		Bukkit.getConsoleSender().sendMessage("§cStacktrace:");
		ex.printStackTrace();
		Bukkit.getConsoleSender().sendMessage("§c[---------- EXCEPTION ----------]\n");
		
		if(halt == true) {
			Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "§4The error above will disable this Plugin! Disabling..");
			Bukkit.getPluginManager().disablePlugin(main.pl);
		}
		
		return true;
	}
}

package de.tobias.wirelessred;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {
	
	public static Plugin pl;
	
	public void onEnable() {
		pl = this;
		Bukkit.getConsoleSender().sendMessage("Loading...");
	}
	
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("Unloading...");
		
	}
}
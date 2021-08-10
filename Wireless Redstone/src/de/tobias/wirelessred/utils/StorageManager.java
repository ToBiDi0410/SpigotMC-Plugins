package de.tobias.wirelessred.utils;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.material.Diode;

import de.tobias.wirelessred.main;

public class StorageManager {

	public static File storageFile = new File(main.pl.getDataFolder(), "storage.yml");
	public static YamlConfiguration yamlCfg = null;
	
	public static void load() {
		yamlCfg = YamlConfiguration.loadConfiguration(storageFile);
		
	}
	
	public static String getConfigKeyByLocation(Location loc) { 
		String key = "connections." + loc.getBlockX() + loc.getBlockY() + loc.getBlockZ();
		return key;
	}
	
	public static void getWirelessRedstoneObject(Location loc) {
		HashMap<String, Object> data = new HashMap<>();
		
		String key = "connections." + loc.getBlockX() + loc.getBlockY() + loc.getBlockZ();
		
		if() {
			Location start = loc;

			data.put("Start", loc);
			data.put("Dest", yamlCfg.getLocation(key + ".destination"));
		}
	}
	
	public static boolean isWirelessRedstoneSender(Location loc) {
		return yamlCfg.contains(getConfigKeyByLocation(loc));
	}
	
	public static void setRedstoneValue(Location loc, boolean state) {
		if(isWirelessRedstoneSender(loc)) {
			String key = getConfigKeyByLocation(loc);
			
			yamlCfg.set(key + ".value", state);
			Location dest = yamlCfg.getLocation(key + ".destination");
			if(dest.isWorldLoaded()) {
				Diode d = (Diode) dest.getBlock();
				d.setData((byte) (state ? 1 : 0));
			}
		}
	}
}

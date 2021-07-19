package de.tobias.spigotdash.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobias.spigotdash.main;

public class configuration {
	
	public static String current_ver = "0.4";
	
	public static File cfg_file = new File(main.pl.getDataFolder(), "config.yml");
	public static YamlConfiguration yaml_cfg = null;
	
	public static HashMap<String, Object> CFG = new HashMap<String, Object>();
	
	public static boolean init() {
		boolean created = false;
		CFG.put("PORT", 9678);
		CFG.put("FILE_VERSION", current_ver);
		CFG.put("WEB_PASSWORD", "PleaseChangeThis");
		CFG.put("UPDATE_REFRESH_TIME", 30);
		CFG.put("PLAYER_RECORD", 0);
		CFG.put("darkMode", false);
		
		pluginConsole.sendMessage("Initializing Config File...");
		if(!cfg_file.exists()) {
			try {
				cfg_file.createNewFile();
				created = true;
				pluginConsole.sendMessage("&6Created new config File!");
			} catch (IOException e) {
				pluginConsole.sendMessage("&c[ERROR] Failed to create Config File: ");
				errorCatcher.catchException(e, false);
				return false;
			}
		}
		
		yaml_cfg = YamlConfiguration.loadConfiguration(cfg_file);
		
		//LOAD ONLY NEEDED KEYS
		for(String s : CFG.keySet()) {
			if(yaml_cfg.contains(s)) {
				CFG.replace(s, yaml_cfg.get(s));
			} else {
				if(!created) {
					pluginConsole.sendMessage("&6WARNING: Your Config File is missing some values: &b" + s);
				} else {
					yaml_cfg.set(s, CFG.get(s));
				}
			}
		}
		
		//WARN ON WRONG_VERSIONS
		if(!yaml_cfg.getString("FILE_VERSION").equalsIgnoreCase(current_ver)) {
			if(!tryUpgrade(yaml_cfg)) {
				pluginConsole.sendMessage("&6WARNING: Your Config File Version is not the newest (" + current_ver + ")");
				pluginConsole.sendMessage("&cTo fix this, you should delete the current Config and Restart the Server to generate a new one!");	
			}
		}
		
		if(created) {
			save();
		}
		
		pluginConsole.sendMessage("&aConfiguration loaded from File!");
		
		return true;
		
	}
	
	public static boolean tryUpgrade(YamlConfiguration yaml_cfg) {
		boolean migrated = false;
		if(yaml_cfg.getString("FILE_VERSION").equalsIgnoreCase("0.2")) {
			yaml_cfg.set("UPDATE_REFRESH_TIME", 30);
			yaml_cfg.set("FILE_VERSION", "0.3");
			save();
			migrated = true;
		}
		
		if(yaml_cfg.getString("FILE_VERSION").equalsIgnoreCase("0.3")) {
			yaml_cfg.set("PLAYER_RECORD", 0);
			yaml_cfg.set("darkMode", false);
			yaml_cfg.set("FILE_VERSION", "0.4");
			save();
			migrated = true;
		}
		
		if(migrated) {
			pluginConsole.sendMessage("&2Migrated Configuration File to new Version, ignore the Warnings above!");
		}
		return migrated;
	}
	
	public static boolean save() {
		try {
			yaml_cfg.save(cfg_file);
			return true;
		} catch (IOException e) {
			pluginConsole.sendMessage("&c[ERROR] Cannot save configuration: ");
			errorCatcher.catchException(e, false);
			return false;
		}
	}
}

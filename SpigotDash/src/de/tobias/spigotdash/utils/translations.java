package de.tobias.spigotdash.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.YamlConfiguration;

import de.tobias.spigotdash.main;

public class translations {

	public static Map<String, Object> currentTranslations = new HashMap<String, Object>();
	public static String translationBrackets = "%T%";
	public static File translationFile = new File(main.pl.getDataFolder(), "translations.yml");
	public static YamlConfiguration yaml_cfg = null;
	
	public static String replaceTranslationsInString(String input) {
		for(Entry<String, Object> translation : currentTranslations.entrySet()) {
			input = input.replaceAll(translationBrackets + translation.getKey().toUpperCase() + translationBrackets, translation.getValue().toString());
		}
		return input;
	}
	
	public static boolean load() {
		pluginConsole.sendMessage("Loading Translations...");
		loadDefaultTranslations();
		yaml_cfg = YamlConfiguration.loadConfiguration(translationFile);
		
		for(String key : yaml_cfg.getKeys(true)) {
			if(currentTranslations.containsKey(key)) {
				currentTranslations.replace(key, yaml_cfg.getString(key));
			} else {
				currentTranslations.put(key, yaml_cfg.getString(key));
			}
		}
		
		yaml_cfg.addDefaults(currentTranslations);
		yaml_cfg.options().copyDefaults(true);
		try {
			yaml_cfg.save(translationFile);
		} catch (IOException e) {
			pluginConsole.sendMessage("&cCould not save Translations: ");
			e.printStackTrace();
		}
		pluginConsole.sendMessage("&aTranslations from File imported!");
		return true;
	}
	
	public static void loadDefaultTranslations() {
		//LOGIN
		currentTranslations.put("Login", "Login");
		currentTranslations.put("Login_Required", "You need to be logged in, in order to continue!");
		currentTranslations.put("Login_Required_Short", "Login required");
		currentTranslations.put("Login_Required_Popup", "You need to be logged in to use this Page!<br>Redirecting you to the Login page...");
		currentTranslations.put("Password", "Password");
		currentTranslations.put("Reconnecting", "Reconnecting...");
		currentTranslations.put("Server_Offline", "Oh no, the Server seems to be offline! We will try reconnecting for you...");
		currentTranslations.put("Page_Load_Failed", "Failed to load the requested Page! Is the Server online?");
		
		//SIDEBAR
		currentTranslations.put("Overview", "Overview");
		currentTranslations.put("Performance", "Performance");
		currentTranslations.put("Graphs", "Graphs");
		currentTranslations.put("Worlds", "Worlds");
		currentTranslations.put("Management", "Management");
		currentTranslations.put("Console", "Console");
		currentTranslations.put("Controls", "Controls");
		currentTranslations.put("Others", "Others");
		currentTranslations.put("Players", "Players");
		currentTranslations.put("Files", "Files");
		currentTranslations.put("Back", "Back");
		
		//OVERVIEW
		currentTranslations.put("TPS", "TPS");
		currentTranslations.put("Plugins", "Plugins");
		currentTranslations.put("Ontime", "Ontime");
		currentTranslations.put("Notifications", "Notifications");
		currentTranslations.put("PLAYER_RECORD", "Player Record");
		
		//GRAPHS
		currentTranslations.put("CPU_Usage", "CPU Usage (%)");
		currentTranslations.put("CPU_load_host", "Host CPU Load");
		currentTranslations.put("CPU_load_server", "Load caused by Server");
		
		currentTranslations.put("RAM_Usage", "RAM Usage (mB)");
		currentTranslations.put("Allocated", "Allocated");
		currentTranslations.put("Used", "Used");
		
		currentTranslations.put("TPS_History", "TPS History");
		currentTranslations.put("TPS_Long", "TPS (Ticks per Second)");

		currentTranslations.put("Engine_Stats", "Engine Stats");
		currentTranslations.put("Chunks", "Chunks");
		currentTranslations.put("Entities", "Entities");
		currentTranslations.put("Players", "Players");
		
		//WORLDS
		currentTranslations.put("Coming_Soon", "Coming soon...");
		
		//CONSOLE
		currentTranslations.put("Execute", "Execute");
		currentTranslations.put("Command", "Command");
		
		//CONTROLS
		currentTranslations.put("General", "General");
		currentTranslations.put("Stop", "Stop");
		currentTranslations.put("Reload", "Reload");
		currentTranslations.put("Remove", "Remove");
		
		currentTranslations.put("Whitelist", "Whitelist");
		currentTranslations.put("Edit_Whitelist", "Edit Whitelist");
		currentTranslations.put("Enable_Whitelist", "Enable Whitelist");
		currentTranslations.put("Disable_Whitelist", "Disable Whitelist");
		
		currentTranslations.put("Game", "Game");
		currentTranslations.put("Enable_End", "Enable End");
		currentTranslations.put("Disable_End", "Disable End");
		currentTranslations.put("Enable_Nether", "Enable Nether");
		currentTranslations.put("Disable_Nether", "Disable Nether");
		
		//PLUGINS
		currentTranslations.put("Install_Plugins", "Install Plugins");
		currentTranslations.put("Installed", "Installed");
		currentTranslations.put("Install", "Install");
		currentTranslations.put("Supports", "Supports");
		currentTranslations.put("From", "From");
		currentTranslations.put("Enabled", "Enabled");
		currentTranslations.put("Disabled", "Disabled");
		currentTranslations.put("Enable", "Enable");
		currentTranslations.put("Disable", "Disable");
		
		//PLAYERS
		currentTranslations.put("No_Players_Online", "There are currently no Player on the Server");
		currentTranslations.put("Player_Left_Server", "The Player left the Server");
		
		currentTranslations.put("Send_Ingame_Message", "Send Ingame Message");
		currentTranslations.put("Color_Codes_Supported", "Color Codes are support (e.g '&6')");
		currentTranslations.put("Sent", "Sent");
		currentTranslations.put("Send_Message", "Send");
		currentTranslations.put("Ingame_Message_Sent", "The Message was sent to the Player!");		
		currentTranslations.put("Kick_player", "Kick Player");
		currentTranslations.put("Kicked", "Kicked");
		currentTranslations.put("Player_Kicked", "The Player was kicked from the Server!");
		
		currentTranslations.put("Displayname", "Displayname");
		currentTranslations.put("Position", "Position");
		currentTranslations.put("Health", "Health");
		currentTranslations.put("Food", "Food");
		currentTranslations.put("In", "in");
		currentTranslations.put("Action_Message", "Message");
		currentTranslations.put("Action_Kick", "Kick");
		
		//FILES
		currentTranslations.put("Files", "Files");
		currentTranslations.put("Icon", "Icon");
		currentTranslations.put("Name", "Name");
		currentTranslations.put("Last_Change", "Last Change");
		currentTranslations.put("Error_Occured", "An error occured");
		currentTranslations.put("Error_Unsupported_File_Format", "File format not supported");
		currentTranslations.put("Previous_Folder", "Previous Folder");
		
		//NOTIFICATIONS
		currentTranslations.put("NOTIFICATION_LOWTPS_TITLE", "Low TPS");
		currentTranslations.put("NOTIFICATION_LOWTPS_CONTENT", "Your Server is running at low TPS.<br>Check the Performance Tabs for further Inforomation");
		
		currentTranslations.put("NOTIFICATION_DISABLEDPLUGINS_TITLE", "Disabled Plugins");
		currentTranslations.put("NOTIFICATION_DISABLEDPLUGINS_CONTENT", "One or more Plugins are currently <b>disabled or unloaded</b><br>Please check the Log for Errors if this is not intended!");
	
		currentTranslations.put("NOTIFICATION_UNLOADEDJARS_TITLE", "Unloaded JAR Files");
		currentTranslations.put("NOTIFICATION_UNLOADEDJARS_CONTENT", "One or more JAR Files in the Plugins folder are currently <b>unloaded or invalid</b>!");
		
		currentTranslations.put("NOTIFICATION_UPDATE_TITLE", "Update available");
		currentTranslations.put("NOTIFICATION_UPDATE_CONTENT", "An new Update for this Plugin was found!<br>Please visit <a href='https://www.spigotmc.org/resources/spigotdash-webinterface-performance-monitor.93710/'><b>SpigotMC</b></a> for more Information");
	
		currentTranslations.put("NOTIFICATION_RELOADNEED_TITLE", "Reload required");
		currentTranslations.put("NOTIFICATION_RELOADNEED_CONTENT", "A reload or restart is required to finish Plugin installation!<br><button class=\"button is-danger\" onclick='reloadServer();'>Reload</button>");
	}
}

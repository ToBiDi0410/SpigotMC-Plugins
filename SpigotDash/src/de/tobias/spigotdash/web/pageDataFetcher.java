package de.tobias.spigotdash.web;

import java.util.HashMap;

import org.bukkit.Bukkit;

import de.tobias.spigotdash.utils.configuration;
import de.tobias.spigotdash.utils.notificationManager;

public class pageDataFetcher {
	
	public static Object GET_PAGE_OVERVIEW() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("ontime", timeIntoString(dataFetcher.getOntime()));
		data.put("tps", Math.round(dataFetcher.getTPS() * 100.0) / 100.0);
		data.put("player_count", dataFetcher.getPlayerCount());
		data.put("plugin_count", Bukkit.getPluginManager().getPlugins().length);
		data.put("player_record", configuration.yaml_cfg.getInt("PLAYER_RECORD"));
		data.put("notifications", notificationManager.notifications);
		
		return data;
	}
	
	public static Object GET_PAGE_GRAPHS() {
		return dataFetcher.getPerformanceDataForWeb();
	}
	
	public static Object GET_PAGE_PLAYERS() {
		return dataFetcher.getPlayersForWeb();
	}
	
	// HELPERS
	
	public static String timeIntoString(long millis) {
		String s = "";
		
		int seconds = 0;
		int minutes = 0;
		int hours = 0;
		int days = 0;
		
		while(millis >= 1000) {
			seconds++;
			millis -= 1000;
		}
		
		while(seconds >= 60) {
			minutes++;
			seconds -= 60;
		}
		
		while(minutes >= 60) {
			hours++;
			minutes -= 60;
		}
		
		while(hours >= 24) {
			days++;
			hours -= 24;
		}
		
		if(days > 0) {
			s+= days + "d ";
		}
		
		if(hours > 0) {
			s+= hours + "h ";
		}
		
		if(minutes > 0) {
			s+= minutes + "m ";
		}
		
		s+= seconds + "s";
		
		return s;
	}
}

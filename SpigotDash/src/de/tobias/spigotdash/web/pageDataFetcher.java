package de.tobias.spigotdash.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
		data.put("notifications", notificationManager.notifications.entrySet().stream().filter(x -> !(boolean)x.getValue().get("closed")).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue())) );
		
		return data;
	}
	
	public static Object GET_PAGE_GRAPHS() {
		return dataFetcher.getPerformanceDataForWeb();
	}
	
	public static Object GET_PAGE_PLAYERS() {
		return dataFetcher.getPlayersForWeb();
	}
	
	public static Object GET_PAGE_PLUGINS() {
		return dataFetcher.getPluginsForWeb();
	}
	
	public static Object GET_PAGE_CONTROLS() {
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		boolean nether = Boolean.parseBoolean(dataFetcher.getServerPropertie("allow-nether"));
		boolean whitelist = Boolean.parseBoolean(dataFetcher.getServerPropertie("white-list"));
		boolean end = (boolean) dataFetcher.getBukkitPropertie("settings.allow-end");
		data.put("nether", nether);
		data.put("end", end);
		data.put("whitelist", whitelist);
		data.put("whitelistEntrys", offlinePlayerListToWeb(Bukkit.getWhitelistedPlayers()));
		
		return data;

	}
	
	// HELPERS
	public static Object offlinePlayerListToWeb(Set<OfflinePlayer> set) {
		ArrayList<Object> newArray = new ArrayList<>();
		
		for(OfflinePlayer p : set) {
			newArray.add(offlinePlayerToWeb(p));
		}
		
		return newArray;
	}
	
	public static Object offlinePlayerToWeb(OfflinePlayer p) {
		HashMap<String, Object> data = new HashMap<String, Object>();
		
		data.put("uuid", p.getUniqueId().toString());
		data.put("name", p.getName());
		data.put("banned", p.isBanned());
		
		return data;
	}
	
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

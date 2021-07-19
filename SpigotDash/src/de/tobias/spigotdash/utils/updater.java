package de.tobias.spigotdash.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.tobias.spigotdash.main;

public class updater {

	public static String current_version = main.pl.getDescription().getVersion();
	public static boolean update_available = false;

	public static void checkForUpdates() {
		try {
			pluginConsole.sendMessage("&7Checking for Updates...");
			URL url = new URL("https://api.spiget.org/v2/resources/93710/versions?fields=name");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(false);
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);

			int status = con.getResponseCode();
			if (status == 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
				in.close();
				con.disconnect();
				
				JsonParser parser = new JsonParser();
				JsonElement jsonTree = parser.parse(content.toString());
				JsonArray array = jsonTree.getAsJsonArray();
				JsonObject newest = array.get(array.size() - 1).getAsJsonObject();
				
				String newest_version = newest.get("name").getAsString();
				if(!current_version.equalsIgnoreCase(newest_version)) {
					update_available = true;
					pluginConsole.sendMessage("&7New Update &aavailable&7! Please take a look at &6SpigotMC&7!");
				} else {
					update_available = false;
					pluginConsole.sendMessage("&aYou are running the newest Version!");
				}
			}

		} catch (Exception ex) {
			pluginConsole.sendMessage("&cCheck for Updates failed! You won't recieve notifications!");
		}
	}
	
	public static Runnable getUpdateRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				if(update_available != true) {
					checkForUpdates();
				}
			}

		};
	}

}

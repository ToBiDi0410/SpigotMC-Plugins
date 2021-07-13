package de.tobias.spigotdash.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.tobias.spigotdash.main;

public class pluginInstaller {
	
	public static String API_URL = "https://api.spiget.org/v2/";

	public static String installPlugin(String id) {
		pluginConsole.sendMessage("&7Installing new Plugin '" + id + "'...");
		try {
			JsonObject obj = getJSONObjectFromPluginID(id);
			if(obj == null) return "INSTALL_FAILED_NULL";
			URL download = new URL(API_URL + "resources/" + id + "/download");

			File dest = new File(main.pl.getDataFolder().getParentFile(), obj.get("name").getAsString().split(" ")[0] + getFileTypeFromPluginJSON(obj));
			pluginConsole.sendMessage("Downloading File from '" + download.toString() + "' to '" + dest.getPath() + "'...");
			ReadableByteChannel rbc = Channels.newChannel(download.openStream());
			FileOutputStream fos = new FileOutputStream(dest);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
			if(!dest.getName().contains(".jar")) {
				pluginConsole.sendMessage("&cThe Plugin downloaded is not a JAR File! Please continue manually!");
				return "INSTALL_FAILED_NOT_JAR";
			} else {
				pluginConsole.sendMessage("&aPlugin installed successfully. Please reload the Server to activate the Plugin");
				return "INSTALLED";
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return "INSTALL_FAILED_ERR_THROWN";
		}
		
	}
	
	public static String getSpigotDownloadUrlFromPluginJSON(JsonObject main) {
		return "https://spigotmc.org/" + main.get("file").getAsJsonObject().get("url").getAsString();
	}
	
	public static String getFileTypeFromPluginJSON(JsonObject main) {
		return main.get("file").getAsJsonObject().get("type").getAsString();
	}

	public static JsonObject getJSONObjectFromPluginID(String id) {
		try {
			URL url = new URL(API_URL + "resources/" + id);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(false);
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);

			int status = con.getResponseCode();
			if (status != 200) return null;

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
			JsonObject obj = jsonTree.getAsJsonObject();
			return obj;
		} catch (Exception ex) {
			return null;
		}
	}

}

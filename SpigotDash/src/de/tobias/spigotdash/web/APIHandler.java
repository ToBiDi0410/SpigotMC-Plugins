package de.tobias.spigotdash.web;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import de.tobias.spigotdash.main;
import de.tobias.spigotdash.utils.notificationManager;

public class APIHandler {

	public static void handle(HttpExchange he, JsonObject json) {
		if (json.has("method")) {
			String method = json.get("method").getAsString();

			if (method.equalsIgnoreCase("GET_NOTIFICATIONS")) {
				MainRequestHandler.sendJSONResponse(he, 200, notificationManager.notifications);
				return;
			}

			if (method.equalsIgnoreCase("GET_LOG")) {
				MainRequestHandler.sendJSONResponse(he, 200, dataFetcher.getLog(200));
				return;
			}

			if (method.equalsIgnoreCase("GET_PLAYERS")) {
				MainRequestHandler.sendJSONResponse(he, 200, dataFetcher.getPlayersForWeb());
				return;
			}

			if (method.equalsIgnoreCase("GET_PERFORMANCE_DATA")) {
				MainRequestHandler.sendJSONResponse(he, 200, dataFetcher.getPerformanceDataForWeb());
				return;
			}

			if (method.equalsIgnoreCase("EXEC_COMMAND")) {
				if (json.has("command")) {
					try {
						Bukkit.getConsoleSender().sendMessage(
								main.CONSOLE_PREFIX + "Executing: �6/" + json.get("command").getAsString());
						Bukkit.getScheduler().callSyncMethod(main.pl, () -> Bukkit
								.dispatchCommand(Bukkit.getConsoleSender(), json.get("command").getAsString())).get();
						MainRequestHandler.sendJSONResponse(he, 200, "EXECUTED");
						return;
					} catch (Exception ex) {
						MainRequestHandler.sendJSONResponse(he, 500, "ERR_EXEC_FAILED");
						return;
					}
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_COMMAND");
					return;
				}
			}

			if (method.equalsIgnoreCase("GET_FILES_IN_PATH")) {
				if (json.has("path")) {
					String path = json.get("path").getAsString();
					MainRequestHandler.sendJSONResponse(he, 200, dataFetcher.getFilesInPath(path));
					return;
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_PATH");
					return;
				}
			}

			if (method.equalsIgnoreCase("GET_FILE_WITH_PATH")) {
				if (json.has("path")) {
					String path = json.get("path").getAsString();
					File f = dataFetcher.getFileWithPath(path);
					if (f.exists()) {
						if (f.isFile()) {
							MainRequestHandler.sendFileResponse(he, f, 200);
							return;
						} else {
							MainRequestHandler.sendJSONResponse(he, 400, "ERR_FILE_IS_DIR");
							return;
						}
					} else {
						MainRequestHandler.sendJSONResponse(he, 404, "ERR_FILE_NOT_FOUND");
						return;
					}
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_PATH");
					return;
				}
			}

			if (method.equalsIgnoreCase("GET_PLUGINS")) {
				;
				MainRequestHandler.sendJSONResponse(he, 200, dataFetcher.getPluginsForWeb());
				return;
			}

			if (method.equalsIgnoreCase("TOGGLE_PLUGIN")) {
				if (json.has("plugin")) {
					Plugin pl = Bukkit.getPluginManager().getPlugin(json.get("plugin").getAsString());
					if(pl != null) {
						if (pl.isEnabled()) {
							Bukkit.getScheduler().runTask(main.pl, new Runnable() {
								public void run() {
									Bukkit.getPluginManager().disablePlugin(pl);
									MainRequestHandler.sendJSONResponse(he, 200, "SUCCESS");
								}
							});
						} else {
							Bukkit.getScheduler().runTask(main.pl, new Runnable() {
								public void run() {
									Bukkit.getPluginManager().enablePlugin(pl);
									MainRequestHandler.sendJSONResponse(he, 200, "SUCCESS");
								}
							});
						}
						return;
					}
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_NOT_FOUND");
					return;
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_PLUGIN");
					return;
				}
			}

			if (method.equalsIgnoreCase("REMOVED_NOTIFICATION")) {
				if (json.has("uuid")) {
					notificationManager.removeNotification(json.get("uuid").getAsString());
					MainRequestHandler.sendJSONResponse(he, 200, "REMOVED");
					return;
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_NOTIFICATION_UUID");
					return;
				}
			}

			MainRequestHandler.sendJSONResponse(he, 500, "ERR_NOT_HANDLED");

		} else {
			MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_METHOD");
		}
	}
}

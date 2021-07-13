package de.tobias.spigotdash.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import de.tobias.spigotdash.main;
import de.tobias.spigotdash.utils.notificationManager;
import de.tobias.spigotdash.utils.pluginConsole;
import de.tobias.spigotdash.utils.pluginInstaller;

public class APIHandler {
	
	public static File deleteTemp = new File(main.pl.getDataFolder().getParentFile(), ".deleted");

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
						pluginConsole.sendMessage("Executing: &6/" + json.get("command").getAsString());
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
									return;
								}
							});
						} else {
							Bukkit.getScheduler().runTask(main.pl, new Runnable() {
								public void run() {
									Bukkit.getPluginManager().enablePlugin(pl);
									MainRequestHandler.sendJSONResponse(he, 200, "SUCCESS");
									return;
								}
							});
						}
						return;
					} else {
						MainRequestHandler.sendJSONResponse(he, 400, "ERR_NOT_FOUND");
						return;
					}
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_PLUGIN");
					return;
				}
			}
			
			if (method.equalsIgnoreCase("DELETE_PLUGIN")) {
				if (json.has("plugin")) {
					Plugin pl = Bukkit.getPluginManager().getPlugin(json.get("plugin").getAsString());
					if (pl != null) {
						File plfile = new java.io.File(pl.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
						File folder = pl.getDataFolder();
						Bukkit.getScheduler().runTask(main.pl, new Runnable() {
							public void run() {
								if (pl.isEnabled()) Bukkit.getPluginManager().disablePlugin(pl);
								
								if(!plfile.delete() || (folder.exists() && !folder.delete())) {
									MainRequestHandler.sendJSONResponse(he, 500, "ERR_DEL_FAILED");
									return;
								}
								
								MainRequestHandler.sendJSONResponse(he, 200, "SUCCESS");
								return;
							}
						});
						return;
					} else {
						MainRequestHandler.sendJSONResponse(he, 400, "ERR_NOT_FOUND");
						return;
					}
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_PLUGIN");
					return;
				}
			}
			
			if(method.equalsIgnoreCase("RELOAD_SERVER")) {
				Bukkit.getScheduler().runTask(main.pl, new Runnable() {
					public void run() {
						Bukkit.getServer().reload();
					}
				});
				return;
			}
			
			if(method.equalsIgnoreCase("GET_PLUGIN_FILES")) {
				MainRequestHandler.sendJSONResponse(he, 200, dataFetcher.getPluginFileNames());
				return;
			}
			
			if (method.equalsIgnoreCase("INSTALL_PLUGIN")) {
				if (json.has("id")) {
					String install_state = pluginInstaller.installPlugin(json.get("id").getAsString());
					int code = install_state.equalsIgnoreCase("INSTALLED") ? 200 : 500;
					if(code == 200) notificationManager.setNeedReload(true);
					MainRequestHandler.sendJSONResponse(he, code, install_state);
					return;
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_NOTIFICATION_UUID");
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

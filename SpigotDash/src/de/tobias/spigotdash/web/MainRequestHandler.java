package de.tobias.spigotdash.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.tobias.spigotdash.utils.errorCatcher;
import de.tobias.spigotdash.utils.pluginConsole;

public class MainRequestHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange he) throws IOException {
		try {
			if (addCorsHeaders(he) && handleWithSections(he)) {
				String path = he.getRequestURI().getPath();
				if (path.equalsIgnoreCase("/")) {
					path = "/index.html";
				}

				String classpath = "/www" + path;
				URL res = getClass().getResource(classpath);

				if (res == null) {
					classpath = "/www/404.html";
				}
				res = getClass().getResource(classpath);

				File f = new File(res.toExternalForm());
				he.sendResponseHeaders(200, f.length());
				OutputStream outputStream = he.getResponseBody();
				getClass().getResourceAsStream(classpath).transferTo(outputStream);
				outputStream.close();
				;
			}
		} catch (Exception ex) {
			errorCatcher.catchException(ex, false);
		}
	}
	
	public boolean handleWithSections(HttpExchange he) {
		String path = he.getRequestURI().getPath();
		String request_body = castInputStreamToString(he.getRequestBody());
		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(request_body);
		
		if(request_body == null || jsonTree == null || !jsonTree.isJsonObject()) return true;
		
		JsonObject json = jsonTree.getAsJsonObject();
		if(path.equalsIgnoreCase("/api")) {
			if(!AuthHandler.isAuthed(he)) {
				sendJSONResponse(he, 401, "ERR_REQUIRE_AUTH");
				return false;
			}
			APIHandler.handle(he, json);
			return false;
		}
		
		if(path.equalsIgnoreCase("/auth")) {
			AuthHandler.handle(he, json);
			return false;
		}
		
		return true;
	}

	public boolean addCorsHeaders(HttpExchange he) {
		try {
			he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			if (he.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
				he.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS, POST");
				he.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
				he.sendResponseHeaders(204, -1);
				return false;
			}
			return true;
		} catch (Exception ex) {
			pluginConsole.sendMessage("&c[ERROR] Failed to add/mange CORS Headers to/in Response:");
			errorCatcher.catchException(ex, false);
			he.close();
			return false;
		}
	}
	
	public static String castInputStreamToString(InputStream ios) {
		try {
			StringBuilder sb = new StringBuilder();
			int i;
			while ((i = ios.read()) != -1) {
				sb.append((char) i);
			}
			return sb.toString();
		} catch (Exception ex) {
			pluginConsole.sendMessage("&c[ERROR] Failed to read InputStream into String:");
			errorCatcher.catchException(ex, false);
			return null;
		}
	}
	
	public static void sendJSONResponse(HttpExchange he, Integer code, Object data) {
		try {
			String response_string = new GsonBuilder().create().toJson(data);
			byte[] message_bytes = response_string.getBytes();
			he.getResponseHeaders().add("Content-Type", "application/json");
			he.sendResponseHeaders(code, message_bytes.length);
			OutputStream outputStream = he.getResponseBody();
			outputStream.write(message_bytes);
			outputStream.close();
		} catch (Exception ex) {
			pluginConsole.sendMessage("§c[ERROR] Failed to send JSON Response:");
			errorCatcher.catchException(ex, false);
			he.close();
		}
	}
	
	public static void sendFileResponse(HttpExchange he, File f, Integer code) {
		try {
			he.sendResponseHeaders(200, f.length());
			OutputStream outputStream = he.getResponseBody();
			(new FileInputStream(f)).transferTo(outputStream);
			outputStream.close();
		} catch (Exception ex) {
			he.close();
			errorCatcher.catchException(ex, false);
		}
	}

}

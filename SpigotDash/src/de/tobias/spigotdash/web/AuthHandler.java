package de.tobias.spigotdash.web;

import java.net.HttpCookie;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bukkit.Bukkit;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import de.tobias.spigotdash.utils.configuration;
import de.tobias.spigotdash.utils.errorCatcher;

public class AuthHandler {

	public static Map<String, Object> sessionData = new HashMap<String, Object>();
	
	public static void handle(HttpExchange he, JsonObject json) {
		try {
			if (!isAuthed(he)) {
				if (json.has("username") && json.has("password")) {
					if (isValid(json.get("username").getAsString(), json.get("password").getAsString())) {
						he.getResponseHeaders().add("Set-Cookie", generateNewCookie().toString());
						MainRequestHandler.sendJSONResponse(he, 200, "COOKIE_ADDED");
						return;
					} else {
						MainRequestHandler.sendJSONResponse(he, 400, "ERR_WRONG_NAME_OR_PASSWORD");
						return;
					}
				} else {
					MainRequestHandler.sendJSONResponse(he, 400, "ERR_MISSING_NAME_OR_PASSWORD");
					return;
				}
			} else {
				MainRequestHandler.sendJSONResponse(he, 200, "WARN_ALREADY_AUTHED");
				return;
			}
		} catch (Exception ex) {
			errorCatcher.catchException(ex, false);
		}
	}
	
	
	public static String hashPassword(String password) {
		return "NOT_IMPLEMENTED";
	}
	
	public static boolean addAccount(String username, String password) {
		return true;
	}
	
	public static boolean isValid(String username, String password) {
		username = username.toLowerCase();
		if(username.equalsIgnoreCase("admin") && password.equals((String) configuration.CFG.get("WEB_PASSWORD"))) {
			return true;
		}
		return false;
	}
	
	public static HttpCookie generateNewCookie() {
		String newID = UUID.randomUUID().toString();
		HttpCookie sessionCookie = new HttpCookie("sessionId", newID);
		sessionData.put(newID, "TEST");
		return sessionCookie;
	}
	
	public static boolean isAuthed(HttpExchange he) {
		HttpCookie sessionCookie = getSessionCookie(he);
		if(sessionCookie != null) {
			if(sessionData.containsKey(sessionCookie.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	public static HttpCookie getSessionCookie(HttpExchange he) {
		HttpCookie cookie = null;
		String sessionCookie = he.getRequestHeaders().getFirst("Cookie");
		if(sessionCookie != null) {
			 cookie = HttpCookie.parse(sessionCookie).get(0);
		}
		return cookie;
	}
}

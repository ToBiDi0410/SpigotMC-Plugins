package de.tobias.spigotdash.web;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.bukkit.Bukkit;

import com.sun.net.httpserver.HttpServer;

import de.tobias.spigotdash.main;
import de.tobias.spigotdash.utils.errorCatcher;

public class WebServer {

	Integer port;
	HttpServer server;

	public WebServer(Integer port) {
		this.port = port;
	}

	public boolean setup() {
		try {
			Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "Starting Webserver under Port " + this.port + "...");
			this.server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", new MainRequestHandler());
			server.setExecutor(null);
			server.start();
			Bukkit.getConsoleSender().sendMessage(main.CONSOLE_PREFIX + "�aWebserver started!");
			return true;
		} catch (IOException e) {
			errorCatcher.catchException(e, false);
			return false;
		}

	}
	
	public void destroy() {
		server.stop(1);
	}
}

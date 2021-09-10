package de.tobias.spigotdash.web;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import de.tobias.spigotdash.utils.errorCatcher;
import de.tobias.spigotdash.utils.pluginConsole;

public class NgrokManager {

	public Integer port;
	public NgrokClient ngrokClient;
	public Tunnel httpTunnel;
	
	public NgrokManager(Integer port) {
		this.port = port;
		this.ngrokClient = new NgrokClient.Builder().build();
	}
	
	public boolean connect() {
		pluginConsole.sendMessage("&7Connecting to NGrok for external Access...");
		pluginConsole.sendMessage("&6[NOTE] You don´t need this if you are able to forward ports!");
		try {
			ngrokClient.LOGGER.setFilter(emptyLogger());
			ngrokClient.getNgrokProcess().LOGGER.setFilter(emptyLogger());
			httpTunnel = ngrokClient.connect(new CreateTunnel.Builder().withAddr(port).withProto(Proto.HTTP).build());
			pluginConsole.sendMessage("&aConnected to NGrok Servers!");
			pluginConsole.sendMessage("&6URL: " + httpTunnel.getPublicUrl());
		} catch(Exception ex) {
			pluginConsole.sendMessage("&cConnection to NGrok Servers failed!");
			errorCatcher.catchException(ex, false);
		}

		return true;
	}
	
	public Filter emptyLogger() {
		return (new Filter() { //DISABLE NGROK LOGGER
			@Override
			public boolean isLoggable(LogRecord record) {
				return false;
			}
		});
	}
	
	public void reopen() {
		if(httpTunnel != null) {
			ngrokClient.getNgrokProcess().stop();
			httpTunnel = ngrokClient.connect(new CreateTunnel.Builder().withAddr(port).withProto(Proto.HTTP).build());
			pluginConsole.sendMessage("&6New NGrok URL (force reconnect): " + httpTunnel.getPublicUrl());
		}
	}
	
	public void destroy() {
		if(httpTunnel != null) {
			ngrokClient.kill();
		}
	}
}

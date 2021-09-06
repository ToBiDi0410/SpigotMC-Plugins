package de.tobias.spigotdash.web;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.FileUtils;

import de.tobias.spigotdash.main;
import de.tobias.spigotdash.utils.configuration;

public class webBundler {
	
	public static String bundleDefaultCSS() {
		try {
			HashMap<String, String> cssFiles = new HashMap<>();
			addResourceToMap("global.css", cssFiles);
			addResourceToMap("smartMenu.css", cssFiles);
			addResourceToMap("minecraftColors.css", cssFiles);

			
			addResourceToMap("other-license/bulma.min.css", cssFiles);
			addResourceToMap("other-license/bulma-extensions.min.css", cssFiles);
			addResourceToMap("other-license/hightlightjs.railcasts.min.css", cssFiles);
			addResourceToMap("other-license/materialIcons.css", cssFiles);
			addResourceToMap("other-license/vanillatoasts.css", cssFiles);
			
			if(configuration.dark) {
				addResourceToMap("dark.css", cssFiles);
				addResourceToMap("other-license/sweet_dark.css", cssFiles);
				addResourceToMap("other-license/bulmaswatch.min.css", cssFiles);
			}
			return webBundler.bundleCSSContents(cssFiles);
		} catch(Exception ex) {
			ex.printStackTrace();
			return "/*ERROR: " + ex.getMessage() + "*/";
		}
	}
	
	public static String bundleDefaultJS() {
		try {
			HashMap<String, String> jsFiles = new HashMap<>();
			addResourceToMap("notificationManager.js", jsFiles);
			addResourceToMap("taskManager.js", jsFiles);
			addResourceToMap("componentGenerator.js", jsFiles);
			addResourceToMap("smartMenu.js", jsFiles);
			addResourceToMap("minecraftColors.js", jsFiles);
			addResourceToMap("global.js", jsFiles);
			
			addResourceToMap("other-license/apexcharts.js", jsFiles);
			addResourceToMap("other-license/bulma-extensions.min.js", jsFiles);
			addResourceToMap("other-license/highlight.min.js", jsFiles);
			addResourceToMap("other-license/jsencrypt.min.js", jsFiles);
			addResourceToMap("other-license/sweetalert2.min.js", jsFiles);
			addResourceToMap("other-license/vanillatoasts.js", jsFiles);

			return webBundler.bundleJSContents(jsFiles);
		} catch(Exception ex) {
			ex.printStackTrace();
			return "/*ERROR: " + ex.getMessage() + "*/";
		}
	}
	
	public static String bundleCSSFiles(ArrayList<File> files) {
		HashMap<String, String> contents = new HashMap<>();
		for(File f : files) {
			if(f.exists() && f.canRead()) {
				try {
					contents.put(f.getName(), FileUtils.fileRead(f));
				} catch (IOException e) {
					contents.put(f.getName(), "/*FAILED TO READ THIS FILE*/");
				}
			}
		}
		
		return bundleCSSContents(contents);
	}
	
	public static String bundleCSSContents(HashMap<String, String> contents) {
		String s = "/*This file contains multiple CSS Files!*/\n";
		for(Entry<String, String> ent : contents.entrySet()) {
			s+= ""
			+ "\n\n/*----------\n"
			+ "FROM: " + ent.getKey()
			+ "\n----------*/\n"
			+ ent.getValue();
		}

		return s;
	}
	
	//JS
	
	public static String bundleJSContents(HashMap<String, String> contents) {
		String s = "/*This file contains multiple JS Files!*/\n";
		for(Entry<String, String> ent : contents.entrySet()) {
			s+= ""
			+ "\n\n/*----------\n"
			+ "FROM: " + ent.getKey()
			+ "\n----------*/\n"
			+ ent.getValue();
		}

		return s;
	}
	
	public static void addResourceToMap(String rsname, HashMap<String, String> files) throws IOException {
		URL rs = main.pl.getClass().getResource("/www/global/" + rsname);
		files.put(rs.getFile(), IOUtils.toString(rs, StandardCharsets.UTF_8));
	}
 
}

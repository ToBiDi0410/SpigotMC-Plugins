package de.tobias.spigotdash.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileDeleteStrategy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import com.sun.jdi.event.Event;

import de.tobias.spigotdash.main;

public class pluginManager {
	
	public static ArrayList<Plugin> disabledPlugins = new ArrayList<>();

	public static boolean removePlugin(Plugin pl) {
		pluginConsole.sendMessage("&7Removing Plugin '&6" + pl + "&7'...");
		File f = new File(pl.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		if(disablePlugin(pl)) {
		    try {
				FileDeleteStrategy.FORCE.delete(f);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean disablePlugin(Plugin plugin) {
		// FROM: https://github.com/r-clancy/PlugMan/

		String name = plugin.getName();
		
		pluginConsole.sendMessage("&7Disabling Plugin '&6" + plugin + "&7'...");
		
		PluginManager pluginManager = Bukkit.getPluginManager();
		SimpleCommandMap commandMap = null;
		List<Plugin> plugins = null;
		Map<String, Plugin> names = null;
		Map<String, Command> commands = null;
		Map<Event, SortedSet<RegisteredListener>> listeners = null;
		boolean reloadlisteners = true;

		if (pluginManager != null) {

			pluginManager.disablePlugin(plugin);
			disabledPlugins.add(plugin);
			
			try {

				Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
				pluginsField.setAccessible(true);
				plugins = (List<Plugin>) pluginsField.get(pluginManager);

				Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
				lookupNamesField.setAccessible(true);
				names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

				try {
					Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
					listenersField.setAccessible(true);
					listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
				} catch (Exception e) {
					reloadlisteners = false;
				}

				Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
				commandMapField.setAccessible(true);
				commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

				Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
				knownCommandsField.setAccessible(true);
				commands = (Map<String, Command>) knownCommandsField.get(commandMap);

			} catch (Exception ex) {
				pluginConsole.sendMessage("&cFailed to disable Plugin: ");
				errorCatcher.catchException(ex, false);
				return false;
			}

			pluginManager.disablePlugin(plugin);

			if (plugins != null && plugins.contains(plugin))
				plugins.remove(plugin);

			if (names != null && names.containsKey(name))
				names.remove(name);

			if (listeners != null && reloadlisteners) {
				for (SortedSet<RegisteredListener> set : listeners.values()) {
					for (Iterator<RegisteredListener> it = set.iterator(); it.hasNext();) {
						RegisteredListener value = it.next();
						if (value.getPlugin() == plugin) {
							it.remove();
						}
					}
				}
			}

			if (commandMap != null) {
				for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, Command> entry = it.next();
					if (entry.getValue() instanceof PluginCommand) {
						PluginCommand c = (PluginCommand) entry.getValue();
						if (c.getPlugin() == plugin) {
							c.unregister(commandMap);
							it.remove();
						}
					}
				}
			}

			// Attempt to close the classloader to unlock any handles on the plugin's jar
			// file.
			ClassLoader cl = plugin.getClass().getClassLoader();

			if (cl instanceof URLClassLoader) {

				try {

					Field pluginField = cl.getClass().getDeclaredField("plugin");
					pluginField.setAccessible(true);
					pluginField.set(cl, null);

					Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
					pluginInitField.setAccessible(true);
					pluginInitField.set(cl, null);

				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException ex) {
				}

				try {

					((URLClassLoader) cl).close();
				} catch (IOException ex) {

				}

			}

			// Will not work on processes started with the -XX:+DisableExplicitGC flag, but
			// lets try it anyway.
			// This tries to get around the issue where Windows refuses to unlock jar files
			// that were previously loaded into the JVM.
			System.gc();
			
			return true;
		}
		return false;
	}
	
	public static boolean load(String name) {
		// FROM: https://github.com/r-clancy/PlugMan/

		pluginConsole.sendMessage("&7Trying load of Plugin '&6" + name + "&7'...");
		
        Plugin target = null;
        File pluginDir = new File("plugins");

        if (!pluginDir.isDirectory()) {
        	pluginConsole.sendMessage("&cThe Plugin directory is not a Directory, please fix this soon!");
            return false;
        }

        File pluginFile = new File(pluginDir, name + ".jar");

        if (!pluginFile.isFile()) {
            for (File f : pluginDir.listFiles()) {
                if (f.getName().endsWith(".jar")) {
                    try {
                        PluginDescriptionFile desc = main.pl.getPluginLoader().getPluginDescription(f);
                        if (desc.getName().equalsIgnoreCase(name)) {
                            pluginFile = f;
                            break;
                        }
                    } catch (Exception ex) {
                    	pluginConsole.sendMessage("&cCannot find Plugin to enable: ");
                    	errorCatcher.catchException(ex, false);
                    	return false;
                    }
                }
            }
        }

        try {
            target = Bukkit.getPluginManager().loadPlugin(pluginFile);
        } catch (Exception ex) {
        	pluginConsole.sendMessage("&cCannot enable Plugin: ");
        	errorCatcher.catchException(ex, false);
        	return false;
        }

        target.onLoad();
        Bukkit.getPluginManager().enablePlugin(target);

        Iterator<Plugin> it = disabledPlugins.iterator();
		while(it.hasNext()) {
			Plugin pl = it.next();
			if(pl != null && pl.getName().equalsIgnoreCase(name)) {
				it.remove();
			}
		}
        
        return true;

    }
	
	public static ArrayList<Plugin> getAllPluginsWithDisabled() {
		ArrayList<Plugin> returnVal = new ArrayList<>(Arrays.asList(Bukkit.getPluginManager().getPlugins()));
		Iterator<Plugin> it = disabledPlugins.iterator();
		while(it.hasNext()) {
			Plugin pl = it.next();
			if(pl != null) {
				returnVal.add(pl);
			}
		}
		return returnVal;
	}
	
	public static Plugin getPlugin(String name) {
		for(Plugin pl : getAllPluginsWithDisabled()) {
			if(pl != null && pl.getDescription() != null && pl.getDescription().getName().equalsIgnoreCase(name)) {
				return pl;
			}
		}
		
		return null;
	}
}

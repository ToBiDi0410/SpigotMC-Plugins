package de.tobias.spigotdash.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class notificationManager {
	
	public static HashMap<String, HashMap<String, Object>> notifications = new HashMap<String, HashMap<String, Object>>();
	
	public static void addNotification(String MESSAGE_ID, String level, String initiator, String title, String message, int stayMinutes) {
		HashMap<String, Object> data = new HashMap<>();
		
		data.put("title", title);
		data.put("level", level);
		data.put("initiator", initiator);
		data.put("message", message);
		data.put("shown", false);
		data.put("created", System.currentTimeMillis());
		data.put("uuid", MESSAGE_ID);
		if(stayMinutes == -1) {
			data.put("removedAfter", (long)-1);
		} else {
			data.put("removedAfter", System.currentTimeMillis() + (stayMinutes * 1000 * 60));
		}
		
		if(notifications.containsKey(MESSAGE_ID)) {
			data = notifications.get(MESSAGE_ID);
			data.replace("removedAfter", System.currentTimeMillis() + (stayMinutes * 1000 * 60));
		}
		notifications.put(MESSAGE_ID, data);

	}
	
	public static void removeNotification(String MESSAGE_ID) {
		if(notifications.containsKey(MESSAGE_ID) ) {
			notifications.remove(MESSAGE_ID);
		}
	}
	
	public static void manageNotifications() {
		List<String> toRemove = new ArrayList<String>();
		for(Map.Entry<String, HashMap<String, Object>> entry : notifications.entrySet()) {
			String key = entry.getKey();
			HashMap<String, Object> value = entry.getValue();
			if(!(key == null || value == null)) {
				if((long)value.get("removedAfter") <= System.currentTimeMillis() && (long)value.get("removedAfter") != -1) {
					toRemove.add(key);
				}
			}

		}
		for(String s : toRemove) {
			notifications.remove(s);
		}
	}
}

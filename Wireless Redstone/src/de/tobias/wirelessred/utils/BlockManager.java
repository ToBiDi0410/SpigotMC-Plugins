package de.tobias.wirelessred.utils;

import org.bukkit.Location;
import org.bukkit.block.data.Powerable;
import org.bukkit.material.Redstone;

public class BlockManager {

	public static void setDiode(Location loc, boolean state) {
		if(!loc.isWorldLoaded() || loc.getBlock() == null) return;
		
		Powerable d = (Powerable) loc.getBlock();
		d.setPowered(state);
	}
}

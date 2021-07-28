package me.elgamer.hidenseek.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.elgamer.hidenseek.sql.MapData;
import me.elgamer.hidenseek.utilities.Map;

public class MapCommand {

	public static void command(Player p, String[] args) {

		//Add help
		if (args[1].equalsIgnoreCase("add")) {

			if (!p.hasPermission("Minigames.hs.map.add")) {
				p.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
			}

			if (args.length < 5) {
				p.sendMessage(ChatColor.RED +"/hs add map [Location] [Creator] [Time in seconds to hide]");
				return;
			}

			//Add functions
			if (args[4].matches("[0-9]+")) {

				int time = Integer.parseInt(args[4]);

				if (MapData.addMap(args[2], args[3], p.getWorld(), p.getLocation(), time)) {
					p.sendMessage(ChatColor.GREEN +"The map has been added to the DB");
					return;
				} else {
					p.sendMessage(ChatColor.RED +"The map was unfortunetly not added to the DB. Please contact someone who can fix this.");
					return;
				}
			} else {
				p.sendMessage(ChatColor.RED +"The time in seconds to hide should be a numeric value");
				return;
			}

		} else if (args[1].equalsIgnoreCase("delete")) {

			if (!p.hasPermission("Minigames.hs.map.delete")) {
				p.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
				return;
			}

			if (args.length < 2) {
				p.sendMessage(ChatColor.RED +"/hs delete map [Location]");
				return;
			}

			if (MapData.mapExists(args[2])) {
				int mapID = MapData.getMap(args[2]);

				//Delete FUNCTIONS
				if (MapData.deleteMap(mapID)) {
					p.sendMessage(ChatColor.RED +"An error has occured. The DB command was not successful. Please contact someone who can fix this.");
					return;
				} else {
					p.sendMessage(ChatColor.GREEN +"1 map have been removed from the DB");
					return;
				}
			} else {
				p.sendMessage(ChatColor.RED +"This map does not exist");
				return;
			}
			
		} else if (args[1].equalsIgnoreCase("list")) {
			
			if (!p.hasPermission("Minigames.hs.map.list")) {
				p.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
				return;
			}
			
			//Get a list of mapIDs
			ArrayList<Integer> mapIDs = MapData.getMaps();

			//Get details of each map
			for (int i : mapIDs) {
				Map map = new Map(i);
				
				p.sendMessage(ChatColor.AQUA + "MapID: " + map.iMapID + "Name: " + map.szLocation + "Creator: " + map.szCreator + "Hide time: " + map.iWait);
				p.sendMessage(ChatColor.AQUA + "World: " + map.mapWorld.getName() + "Coordinates: " + map.spawn.getX() + ", " + map.spawn.getY() + ", " + map.spawn.getZ());
			}
			return;
		} else if (args[1].equalsIgnoreCase("help")) {
			help(p);
			return;
		} else {
			// /hsmap help
			help(p);
			return;
		}
	}


	public static void help(Player p) {
		p.sendMessage(ChatColor.DARK_AQUA +"---------------");
		p.sendMessage(ChatColor.DARK_AQUA +"/hs map help:");
		p.sendMessage(ChatColor.AQUA +"/hs map add");
		p.sendMessage(ChatColor.AQUA +"/hs map delete");
		p.sendMessage(ChatColor.AQUA +"/hs map list");
	}
}
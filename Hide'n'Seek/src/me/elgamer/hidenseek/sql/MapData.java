package me.elgamer.hidenseek.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.minigames.Main;

public class MapData {
	
	public static ArrayList<Integer> getMaps() {
		
		Main instance = Main.getInstance();
		HideNSeek plugin = HideNSeek.getInstance();

		ArrayList<Integer> maps = new ArrayList<Integer>();
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + plugin.maps);			
			ResultSet results = statement.executeQuery();
						
			while (results.next()) {
				maps.add(results.getInt("MapID"));
			}
			
			return maps;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Location getSpawn(int mapID) {
		
		Main instance = Main.getInstance();
		HideNSeek plugin = HideNSeek.getInstance();
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + plugin.maps + " WHERE MapID=" + mapID);			
			ResultSet results = statement.executeQuery();
						
			if (results.next()) {
				
				return (new Location( Bukkit.getWorld(plugin.getConfig().getString("world")),
						results.getDouble("X"),
						results.getDouble("Y"),
						results.getDouble("Z")));			
				
			} else {
				return null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}

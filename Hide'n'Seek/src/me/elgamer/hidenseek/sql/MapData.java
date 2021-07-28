package me.elgamer.hidenseek.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.hidenseek.utilities.Map;
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

				return (new Location( Bukkit.getWorld(results.getString("World")),
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

	private static int getNewID() {

		Main instance = Main.getInstance();
		HideNSeek plugin = HideNSeek.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + plugin.maps);
			ResultSet results = statement.executeQuery();

			if (results.last()) {
				int last = results.getInt("ID");
				return (last+1);
			} else {
				return 1;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
	}

	//Insert new map
	public static boolean addMap(String location, String creator, World world, Location l, int hideTime) {

		Main instance = Main.getInstance();
		HideNSeek plugin = HideNSeek.getInstance();

		try {

			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + plugin.maps + " (ID,Location,Creator,World,X,Y,Z,HideTime) (?,?,?,?,?,?,?,?)");	

			statement.setInt(1, getNewID());
			statement.setString(2, location);
			statement.setString(3, creator);
			statement.setString(4, world.getName());
			statement.setDouble(5, l.getX());
			statement.setDouble(6, l.getY());
			statement.setDouble(7, l.getZ());
			statement.setInt(8, hideTime);

			statement.executeUpdate();

			return true;
		}
		catch(SQLException se) {
			se.printStackTrace();
			return false;
		}
	}

	//Delete  map
	public static boolean deleteMap(int mapID) {

		Main instance = Main.getInstance();
		HideNSeek plugin = HideNSeek.getInstance();

		try {

			PreparedStatement statement = instance.getConnection().prepareStatement
					("DELETE FROM " + plugin.maps + " WHERE ID=" + mapID);	
			statement.executeUpdate();
			return true;
		}
		catch(SQLException se) {
			se.printStackTrace();
			return false;
		}
	}

	public static boolean mapExists(String location) {

		Main instance = Main.getInstance();
		HideNSeek plugin = HideNSeek.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + plugin.maps + " WHERE Location=?");		
			statement.setString(1, location);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static int getMap(String location) {

		Main instance = Main.getInstance();
		HideNSeek plugin = HideNSeek.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + plugin.maps + " WHERE Location=?");		
			statement.setString(1, location);
			ResultSet results = statement.executeQuery();
			results.next();
			
			return (results.getInt("ID"));

		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static Map getMap(int id) {

		Main instance = Main.getInstance();
		HideNSeek plugin = HideNSeek.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + plugin.maps + " WHERE ID=" + id);		
			ResultSet results = statement.executeQuery();
			results.next();
			
			return (new Map(
					id,
					results.getString("Location"),
					results.getString("Creator"),
					Bukkit.getWorld(results.getString("World")),
					new Location(
							Bukkit.getWorld(results.getString("World")),
							results.getDouble("X"),
							results.getDouble("Y"),
							results.getDouble("Z")),
					results.getInt("HideTime")));

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}

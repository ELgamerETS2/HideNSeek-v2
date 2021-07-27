package me.elgamer.hidenseek;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.elgamer.hidenseek.sql.Tables;
import me.elgamer.hidenseek.utilities.Lobby;

public class HideNSeek extends JavaPlugin {
	
	static HideNSeek instance;
	static FileConfiguration config;
	
	public String maps,stats,game;
	
	public static Lobby lobby;
	
	@Override
	public void onEnable() {

		//Config Setup
		instance = this;
		config = instance.getConfig();
		saveDefaultConfig();
	
		//MySQL table names
		maps = config.getString("table.maps");
		stats = config.getString("table.stats");
		game = config.getString("game");
		
		//Create MySQL tables
		Tables.createTables();
		
		//Setup listeners
		//new PlayerInteract(this);
		//new InventoryClicked(this);
		
		lobby = new Lobby(config);
		
	}
	
	public static HideNSeek getInstance() {
		return instance;
	}
}

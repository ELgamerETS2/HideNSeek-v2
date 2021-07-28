package me.elgamer.hidenseek.utilities;

import org.bukkit.Location;
import org.bukkit.World;

import me.elgamer.hidenseek.sql.MapData;

public class Map {
	public int iMapID;
	public String szLocation;
	public String szCreator;
	public World mapWorld;
	
	public Location spawn;
	public long iWait;
	
	//Contructors
	public Map(int iMap) {
		
		Map map = MapData.getMap(iMap);
		
		this.iMapID = map.iMapID;
		this.szLocation = map.szLocation;
		this.szCreator = map.szCreator;
		this.mapWorld = map.mapWorld;
		this.spawn = map.spawn;
		this.iWait = map.iWait;
	}
	
	public Map(int ID, String location, String creator, World world, Location l, int hideTime) {
		
		this.iMapID = ID;
		this.szLocation = location;
		this.szCreator = creator;
		this.mapWorld = world;
		this.spawn = l;
		this.iWait = hideTime * 1000;
		
	}
}

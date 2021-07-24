package me.elgamer.hidenseek.utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import Minigames.minigamesMain;

public class Map {
	public int iMapID;
	public String szLocation;
	public String szCreator;
	public World mapWorld;
	private String szMapWorld;
	
	public double[] spawnCoordinates;
	public long iWait;
	
	//Contructors
	public HideAndSeekMap(int iMap)
	{
		this.iMapID = iMap;
		this.spawnCoordinates = new double[3];
		this.szCreator = "";
		this.szLocation = "";
	}
	
	public HideAndSeekMap()
	{
		this.iMapID = 0;
		this.spawnCoordinates = new double[3];
		this.szCreator = "";
		this.szLocation = "";
		this.iWait = 0;
	}
	
	public HideAndSeekMap(String szLocation, String szCreator, String szMapWorld, Location location, String szWait)
	{
		this.szLocation = szLocation;
		this.szCreator = szCreator;
		this.szMapWorld = szMapWorld;
		this.spawnCoordinates = new double[3];
		this.spawnCoordinates[0] = location.getBlockX();
		this.spawnCoordinates[1] = location.getBlockY();
		this.spawnCoordinates[2] = location.getBlockZ();
		this.iWait = Integer.parseInt(szWait);
	}
	
	public HideAndSeekMap(String szLocation)
	{
		this.szLocation = szLocation;
	}
	
	//SQL setters
	public void setMapFromMapID()
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		try
		{
			//Collects all fields for the specified EID
			sql = "SELECT * FROM "+minigamesMain.getInstance().HideAndSeekMaps +" WHERE MapID = \""+this.iMapID +"\"";
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no user is found, program will notify thing
			if (bSuccess == false)
			{
				System.out.println("[Minigames] [HideAndSeekMap] [DB] Setting preferences from MapID: No map found with MapID "+this.iMapID);
			}
			//Checks that there is only 1 record returned
			else if (resultSet.next() == false)
			{
				//Runs if there is no second record
				//Colects results again
				resultSet = SQL.executeQuery(sql);
				resultSet.next();
				
				//Stores results into the object
				this.szLocation = resultSet.getString("Location");
				this.szCreator = resultSet.getString("Creator");
				this.mapWorld = minigamesMain.getInstance().getServer().getWorld(resultSet.getString("MapWorld"));
				this.spawnCoordinates[0] = resultSet.getDouble("StartX");
				this.spawnCoordinates[1] = resultSet.getDouble("StartY");
				this.spawnCoordinates[2] = resultSet.getDouble("StartZ");
				this.iWait = (long) (resultSet.getInt("Wait"));
			}
			else
			{
				System.out.println("Setting preferences from MapID: More than one map found");
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	//Collects list of hide and seek mapIDs
	public static int[] hideAndSeekMapIDs()
	{		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int i;
		
		//Counts total amount of hdie and seek maps first
		int iCount = count();
		
		//Initiates the int array for storing MapIDs with length of the amount of maps just found
		int[] iMapIDs = new int[iCount];
		
		//If no maps were found, skip collecting the list
		if (iCount == 0)
		{
			return iMapIDs;
		}
		
		try
		{
			//Collects all maps
			sql = "SELECT * FROM "+minigamesMain.getInstance().HideAndSeekMaps;
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Select * From HideAndSeekMaps]: "+sql);
			
			//Executes the query
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			
			//Moves the curser to the next line
			resultSet.next();
						
			//Checks that there is only 1 record returned
			for (i = 0 ; i < iCount ; i++)
			{
				iMapIDs[i] = resultSet.getInt("MapID");
				resultSet.next();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return iMapIDs;
	}
	
	//Counts the amount of hide and seek maps
	public static int count()
	{
		boolean bSuccess = false;
		
		String sql;
		
		Statement SQL = null;
		ResultSet resultSet = null;
		
		int iCount = 0;
		
		try
		{
			//Collects all maps
			sql = "SELECT * FROM "+minigamesMain.getInstance().HideAndSeekMaps;
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Count of HideAndSeekMaps]: "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			resultSet = SQL.executeQuery(sql);
			//Moves the curser to the next line
			bSuccess = resultSet.next();
			
			//If no map is found, program will notify thing
			if (bSuccess == false)
			{
				System.out.println("No maps found");
			}
			else
			{
				iCount++;
			}
			
			//Checks that there is only 1 record returned
			while (resultSet.next() != false)
			{
				iCount++;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return iCount;
	}
	
	//Insert new map
	public boolean addMap()
	{
		boolean bSuccess = false;
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		try
		{
			//Compiles the command to add the new user
			sql = "INSERT INTO `"+minigamesMain.getInstance().HideAndSeekMaps
					+"` (`Location`, `Creator`, `MapWorld`, `StartX`, `StartY`, `StartZ`, `Wait`)"
					+ " VALUES("
					+ "\""+szLocation+"\", "
					+ "\""+szCreator+"\", "
					+ "\""+szMapWorld+"\", "
					+ spawnCoordinates[0] +", "
					+ spawnCoordinates[1] +", "
					+ spawnCoordinates[2] +", "
					+ (int) iWait + ");";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Add to HideAndSeekMaps]: "+sql);
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			iCount = SQL.executeUpdate(sql);
			
			//Checks whether only 1 record was updated
			if (iCount == 1)
			{
				//If so, bSuccess is set to true
				bSuccess = true;
			}
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bSuccess;
	}
	
	//Delete  map
	public int deleteMap()
	{
		int iCount = -1;
		String sql;
		
		Statement SQL = null;
		
		try
		{
			//Compiles the command to add the new user
			sql = "DELETE FROM `"+minigamesMain.getInstance().HideAndSeekMaps +"` Where Location = \""+szLocation+"\"";
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Deleting a HideAndSeekMaps]: "+sql);
			
			SQL = minigamesMain.getInstance().getConnection().createStatement();
			
			//Executes the update and returns the amount of records updated
			iCount = SQL.executeUpdate(sql);
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return iCount;
	}
}

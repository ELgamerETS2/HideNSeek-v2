package me.elgamer.hidenseek.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapCommand implements CommandExecutor  {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Check is command sender is a p
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be run as a p!");
			return true;
		}
		
		Player p = (Player) sender;
		
		//Player doesn't have the correct permissions
		if (!sender.hasPermission("minigames.hidenseek.map")) {
			sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
			return true;
		}
		
		if (args.length == 0) {
			maphelp(sender);
			return true;
		}
				
		else if (args.length == 2) {
			
			//Add help
			if (args[0].equalsIgnoreCase("add"))
			{
				if (!sender.hasPermission("Minigames.hidemaps.addmaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				sender.sendMessage(ChatColor.RED +"/hsmap add [Location] [Creator] [Time in seconds to hide]");
				return true;
			}
			else if (args[0].equalsIgnoreCase("delete"))
			{
				if (!sender.hasPermission("Minigames.hidemaps.deletemaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				//Delete FUNCTIONS
				HideAndSeekMap oldMap = new HideAndSeekMap(args[1]);
				int iDeleted = oldMap.deleteMap();
				
				if (iDeleted == -1)
					sender.sendMessage(ChatColor.RED +"An error has occured. The DB command was not successful. Please contact someone who can fix this.");
				
				else if (iDeleted == 1)
					sender.sendMessage(ChatColor.GREEN +"1 map have been removed from the DB");
				
				else
					sender.sendMessage(ChatColor.GREEN +""+iDeleted +" maps have been removed from the DB");
				
				return true;
			}
			else
			{
				// /hsmap help
				maphelp(sender);
				return true;
			}
		}
		else if (args[0].equalsIgnoreCase("list"))
		{
			if (!sender.hasPermission("Minigames.hidemaps.list"))
			{
				sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
				return true;
			}
			//Get a list of mapIDs
			int[] mapIDs = HideAndSeekMap.hideAndSeekMapIDs();
			//Initiate Map array with length of the amount of MapIDs just found
			HideAndSeekMap[] Maps = new HideAndSeekMap[mapIDs.length];
			
			//Get details of each map
			for (int i = 0 ; i < mapIDs.length ; i++)
			{
				Maps[i] = new HideAndSeekMap(mapIDs[i]);
				Maps[i].setMapFromMapID();
				p.sendMessage(ChatColor.DARK_AQUA +"[Map Found]:");
				p.sendMessage(ChatColor.AQUA +"MapID: "+Maps[i].iMapID);
				p.sendMessage(ChatColor.AQUA +"Name: "+Maps[i].szLocation);
				p.sendMessage(ChatColor.AQUA +"Creator: "+Maps[i].szCreator);
				p.sendMessage(ChatColor.AQUA +"World: "+Maps[i].mapWorld.getName());
				p.sendMessage(ChatColor.AQUA +"Cordinates: "+Maps[i].spawnCoordinates[0] +", "+Maps[i].spawnCoordinates[1] +", "+Maps[i].spawnCoordinates[2]);
				p.sendMessage(ChatColor.AQUA +"Hide time: "+Maps[i].iWait);
			}
			return true;
		}
		else if (args.length == 4)
		{
			if (args[0].equalsIgnoreCase("add"))
			{
				if (!sender.hasPermission("Minigames.hidemaps.addmaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}				
				//Add functions
				if (args[3].matches("[0-9]+"))
				{
					HideAndSeekMap newMap = new HideAndSeekMap(args[1], args[2], p.getWorld().getName(), p.getLocation(), args[3]);
					if (newMap.addMap())
					{
						sender.sendMessage(ChatColor.GREEN +"The map has been added to the DB");
					}
					else
					{
						sender.sendMessage(ChatColor.RED +"The map was unfortunetly not added to the DB. Please contact someone who can fix this.");
					}
				}
				else
					sender.sendMessage(ChatColor.RED +"The time in seconds to hide should be a numeric value");
				return true;
			}
			else if (args[0].equalsIgnoreCase("delete"))
			{
				//Delete help
				if (!sender.hasPermission("Minigames.hidemaps.deletemaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				sender.sendMessage(ChatColor.RED +"/hsmap delete [Location]");
				return true;
			}
			else
			{
				maphelp(sender);
				return true;
			}
		}
		else
		{
			if (args[0].equalsIgnoreCase("add"))
			{
				//Add help
				if (!sender.hasPermission("Minigames.hidemaps.addmaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				sender.sendMessage(ChatColor.RED +"/hsmap add [Location] [Creator] [Time in seconds to hide]");
				return true;
			}
			else if (args[0].equalsIgnoreCase("delete"))
			{
				//Delete help
				if (!sender.hasPermission("Minigames.hidemaps.deletemaps"))
				{
					sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
					return true;
				}
				sender.sendMessage(ChatColor.RED +"/hsmap delete [Location]");
				return true;
			}
			
			maphelp(sender);
			return true;
		}		
	}

	private void maphelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.DARK_AQUA +"---------------");
		sender.sendMessage(ChatColor.DARK_AQUA +"/hsmap Help:");
		sender.sendMessage(ChatColor.AQUA +"/hsmap delete");
		sender.sendMessage(ChatColor.AQUA +"/hsmap add");
		sender.sendMessage(ChatColor.AQUA +"/hsmap list");
	}
}

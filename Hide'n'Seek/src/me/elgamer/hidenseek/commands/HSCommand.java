package me.elgamer.hidenseek.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HSCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Check is command sender is a pplayer
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be run as a player!");
			return true;
		}

		Player p = (Player) sender;

		//Player doesn't have the correct permissions
		if (!sender.hasPermission("minigames.hidenseek.map")) {
			sender.sendMessage(ChatColor.RED +"You do not have permission to use this command!");
			return true;
		}

		if (args.length == 0) {
			p.sendMessage(ChatColor.RED + "/hs map help");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("map")) {
			if (args.length < 2) {
				MapCommand.help(p);
				return true;
			} else {
				MapCommand.command(p,args);
				return true;
			}
		} else {
			help(p);
			return true;
		}
		

	}
	
	private void help(Player p) {
		p.sendMessage(ChatColor.DARK_AQUA +"---------------");
		p.sendMessage(ChatColor.DARK_AQUA +"/hs map help:");
	}

}

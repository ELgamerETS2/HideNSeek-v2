package me.elgamer.hidenseek.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.hidenseek.utilities.Lobby;
import me.elgamer.minigames.Main;
import me.elgamer.minigames.gui.Menu;
import me.elgamer.minigames.utilities.User;

public class InventoryClicked implements Listener {
	public InventoryClicked(HideNSeek plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if (e.getCurrentItem() == null) {
			return;
		}

		if (e.getCurrentItem().hasItemMeta() == false) {
			return;
		}

		String title = e.getView().getTitle();

		User u = Main.getUser((Player) e.getWhoClicked());
		
		//If in the Menu GUI
		if (title.equals(Menu.inventory_name))
		{
			e.setCancelled(true);
			if (e.getCurrentItem() == null) {
				return;
			}
			
			if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Hide'n'Seek")) {
				Lobby.joinLobby(u);
			}
			
		}
	}
}

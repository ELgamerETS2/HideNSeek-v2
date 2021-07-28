package me.elgamer.hidenseek.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.hidenseek.utilities.Lobby;

public class PlayerInteract implements Listener {
	public PlayerInteract(HideNSeek plugin) {
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void interactEvent(PlayerInteractEvent e) {
		
		Player player;
		
		player = e.getPlayer();
		
		if (player.getOpenInventory().getType() != InventoryType.CRAFTING && e.getPlayer().getOpenInventory().getType() != InventoryType.CREATIVE)
		{
		    return;
		}
		
		if (player.getInventory().getItemInMainHand().equals(HideNSeek.lobby.leave)) {
			e.setCancelled(true);
			
			for (me.elgamer.hidenseek.utilities.Player p : HideNSeek.lobby.players) {			
				player.getInventory().clear();
				Lobby.leaveLobby(p);
			}
		}
	}
}

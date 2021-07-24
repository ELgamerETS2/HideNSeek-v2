package me.elgamer.hidenseek.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.hidenseek.utilities.Player;

public class MoveEvent implements Listener {
	
	ArrayList<Player> seekers;
	
	public MoveEvent(HideNSeek plugin, ArrayList<Player> seekers) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.seekers = seekers;
	}
	
	@EventHandler
	public void Move(PlayerMoveEvent e) {
		//Goes through the list of finders
		for (Player p : seekers) {
			if (p.u.p.equals(e.getPlayer())) {
				e.setCancelled(true);
			}
		}
	}
	
	public void unRegister() {
		HandlerList.unregisterAll(this);
	}

}

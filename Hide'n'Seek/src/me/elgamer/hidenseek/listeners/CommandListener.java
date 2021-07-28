package me.elgamer.hidenseek.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.hidenseek.utilities.Lobby;
import me.elgamer.hidenseek.utilities.Player;

public class CommandListener implements Listener {

	Lobby lobby;
	ArrayList<Player> lobbyPlayers;
	ArrayList<Player> gamePlayers;

	public CommandListener(Lobby lobby, HideNSeek plugin) {
		this.lobbyPlayers = lobby.players;
		this.gamePlayers = lobby.game.players;
		this.lobby = lobby;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void CommandPreProcess(PlayerCommandPreprocessEvent e) {

		if (e.getMessage().startsWith("/leave")) {

			for (Player p : lobbyPlayers) {

				if (p.u.p.equals(e.getPlayer())) {
					Lobby.leaveLobby(p);
				}

			}

			for (Player p : gamePlayers) {

				if (p.u.p.equals(e.getPlayer())) {
					lobby.game.playerLeave(p);
				}

			}
		}		
	}

	public void unRegister() {
		HandlerList.unregisterAll(this);
	}

}

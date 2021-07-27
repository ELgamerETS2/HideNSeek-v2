package me.elgamer.hidenseek.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.hidenseek.utilities.Announce;
import me.elgamer.hidenseek.utilities.Game;
import me.elgamer.hidenseek.utilities.Player;

public class DamageDone implements Listener
{
	private Game game;
	
	public DamageDone(Game game, HideNSeek plugin)
	{
		this.game = game;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
		
	@EventHandler
	public void Damage(EntityDamageByEntityEvent event)
	{
		
		//Checks whether it is a PVP event
		if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getType() == EntityType.PLAYER)
		{
			
			//Convert damager to player
			ArrayList<Player> hiders = game.hiders;
			ArrayList<Player> seekers = game.seekers;
			org.bukkit.entity.Player seeker = (org.bukkit.entity.Player) event.getDamager();
			org.bukkit.entity.Player hider = (org.bukkit.entity.Player) event.getEntity();
			Player updatedSeeker;
			
			//Go through seekers list until it finds the seeker
			//	- If the seeker isn't found, nothing will happen
			for (Player p : seekers) {
				//Identifies the actual seeker who found someone
				if (p.u.p.equals(seeker)) {
					//Identifies the hider
					for (Player h : hiders) {
						if (h.u.p.equals(hider)) {
							//Increments the amount of people found for this finder by 1
							updatedSeeker = h;
							seekers.add(updatedSeeker);
							
							//Updates scoreboard score for finder
							game.Found.getScore(seeker.getName()).setScore(p.iFound);
						
							//Adds hider to seekers
							seekers.add(h);
							
							//Set score to 0
							game.Found.getScore(hider.getDisplayName()).setScore(0);
							hider.setWalkSpeed(game.seekerSpeed);
		        			
							//Announces to all players that a player has been found
							Announce.announce(game.players, ChatColor.DARK_PURPLE + hider.getDisplayName() + ChatColor.LIGHT_PURPLE + " was found by "+ChatColor.DARK_PURPLE + seeker.getDisplayName());
							Announce.announce(game.players, ChatColor.DARK_PURPLE + hider.getDisplayName() + ChatColor.LIGHT_PURPLE + " is now a seeker");
							
							//Global effect
							FireworkEffect effect = FireworkEffect.builder().trail(true).with(Type.BALL_LARGE).withFlicker().withColor(Color.FUCHSIA).build();
							
							Firework firework1 = (Firework) seeker.getWorld().spawnEntity(seeker.getLocation(), EntityType.FIREWORK);
							FireworkMeta meta1 = firework1.getFireworkMeta();							
							meta1.addEffect(effect);
							meta1.setPower(4);
							firework1.setFireworkMeta(meta1);
							
							Firework firework2 = (Firework) seeker.getWorld().spawnEntity(seeker.getLocation(), EntityType.FIREWORK);
							FireworkMeta meta2 = firework2.getFireworkMeta();
							meta2.addEffect(effect);
							meta2.setPower(5);
							firework2.setFireworkMeta(meta2);
							
							Firework firework3 = (Firework) seeker.getWorld().spawnEntity(seeker.getLocation(), EntityType.FIREWORK);
							FireworkMeta meta3 = firework2.getFireworkMeta();
							meta3.addEffect(effect);
							meta3.setPower(6);
							firework3.setFireworkMeta(meta3);
							
							hiders.remove(h);
							break;
						}
					}
					break;
				}
			}
			
			if (game.bTerminate == true) {
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] [DamageDone] Tried and prevented terminating game when game was not running");
			}
			else if (game.hiders.size() == 0) {
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] [DamageDone] Terminating game now there are no hiders left");
				this.game.terminate();
			}			
		}
	}
	
	@EventHandler
	public void Environment(EntityDeathEvent event) {
		
		if (event.getEntityType() == EntityType.PLAYER) {
			ArrayList<Player> hiders = game.hiders;
			ArrayList<Player> seekers = game.seekers;
			org.bukkit.entity.Player hider = (org.bukkit.entity.Player) event.getEntity();
			
			//Identifies the hider
			for (Player h : hiders) {
				if (h.u.p.equals(hider)) {				

					//Adds this to the finders
					seekers.add(h);
					
					//Set score to 0
					game.Found.getScore(hider.getName()).setScore(0);
					
					//Announces to all players that a player has been killed
					Announce.announce(game.players, ChatColor.DARK_PURPLE + hider.getDisplayName() +ChatColor.LIGHT_PURPLE +" died");
					Announce.announce(game.players, ChatColor.DARK_PURPLE + hider.getDisplayName() +ChatColor.LIGHT_PURPLE +" is now a seeker");
					
					hiders.remove(h);
					
					hider.teleport(game.spawn);
					break;
				}
			}
		}
	}
	
	public void unRegister() {
		HandlerList.unregisterAll(this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [Hide] DamageDone unregistered");
	}
} //End Class
//Created by Bluecarpet in London
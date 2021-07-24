package me.elgamer.hidenseek.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import Minigames.Announce;
import Minigames.minigamesMain;
import Minigames.Games.HideAndSeek.HideAndSeekFinder;
import Minigames.Games.HideAndSeek.HideAndSeekGame;
import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.hidenseek.utilities.Game;

public class DamageDone implements Listener
{
	private final HideNSeek plugin;
	private final Game game;
	
	public DamageDone(Game game, HideNSeek plugin)
	{
		this.plugin = plugin;
		this.game = game;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
		
	@EventHandler
	public void Damage(EntityDamageByEntityEvent event)
	{
		int i, j;
		
		//Checks whether it is a PVP event
		if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getType() == EntityType.PLAYER)
		{
			//Convert damager to player
			Player finder = (Player) event.getDamager();
			Player hider = (Player) event.getEntity();
			HideAndSeekFinder updatedFinder;
			
			//Go through seekers list until it finds the seeker
			//	- If the seeker isn't found, nothing will happen
			for (i = 0 ; i < HideAndSeekGame.finders.size() ; i++)
			{
				//Identifies the actual seeker who found someone
				if (HideAndSeekGame.finders.get(i).player.equals(finder))
				{
					//Identifies the hider
					for (j = 0 ; j < HideAndSeekGame.hiders.size() ; j++)
					{
						if (HideAndSeekGame.hiders.get(j).player.equals(hider))
						{
							//Increments the amount of people found for this finder by 1
							updatedFinder = new HideAndSeekFinder(finder, HideAndSeekGame.finders.get(i).iFound + 1);
							HideAndSeekGame.finders.set(i, updatedFinder);
							
							//Updates scoreboard score for finder
							HideAndSeekGame.Found.getScore(HideAndSeekGame.finders.get(i).player.getName()).setScore(HideAndSeekGame.finders.get(i).iFound);
							
							int iCurrentFinders = HideAndSeekGame.finders.size();
							
							//Creates a new finder object. Hider, people found, finders when found
							HideAndSeekFinder newFinder = new HideAndSeekFinder(hider, 0, iCurrentFinders);
							
							//Adds this to the finders
							HideAndSeekGame.finders.add(newFinder);
							
							//Set score to 0
							HideAndSeekGame.Found.getScore(hider.getDisplayName()).setScore(0);
							hider.setWalkSpeed((float) mainPlugin.getConfig().getDouble("SeekerSpeed"));
		        			
							//Announces to all players that a player has been found
							Announce.announce(this.HideAndSeekGame.getPlayers(), ChatColor.DARK_PURPLE +hider.getDisplayName() +ChatColor.LIGHT_PURPLE +" was found by "+ChatColor.DARK_PURPLE +finder.getDisplayName());
							Announce.announce(this.HideAndSeekGame.getPlayers(), ChatColor.DARK_PURPLE +hider.getDisplayName() +ChatColor.LIGHT_PURPLE +" is now a seeker");
							
							//Global effect
							FireworkEffect effect = FireworkEffect.builder().trail(true).with(Type.BALL_LARGE).withFlicker().withColor(Color.FUCHSIA).build();
							
							Firework firework1 = (Firework) finder.getWorld().spawnEntity(finder.getLocation(), EntityType.FIREWORK);
							FireworkMeta meta1 = firework1.getFireworkMeta();							
							meta1.addEffect(effect);
							meta1.setPower(4);
							firework1.setFireworkMeta(meta1);
							
							Firework firework2 = (Firework) finder.getWorld().spawnEntity(finder.getLocation(), EntityType.FIREWORK);
							FireworkMeta meta2 = firework2.getFireworkMeta();
							meta2.addEffect(effect);
							meta2.setPower(5);
							firework2.setFireworkMeta(meta2);
							
							Firework firework3 = (Firework) finder.getWorld().spawnEntity(finder.getLocation(), EntityType.FIREWORK);
							FireworkMeta meta3 = firework2.getFireworkMeta();
							meta3.addEffect(effect);
							meta3.setPower(6);
							firework3.setFireworkMeta(meta3);
							
							HideAndSeekGame.hiders.remove(j);
							break;
						}
					}
					break;
				}
			}
			
			if (HideAndSeekGame.bTerminate == true)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] [DamageDone] Tried and prevented terminating game when game was not running");
			}
			else if (HideAndSeekGame.hiders.size() == 0)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] [DamageDone] Terminating game now there are no hiders left");
				this.HideAndSeekGame.terminate();
			}			
		}
	}
	
	@EventHandler
	public void Environment(EntityDeathEvent event)
	{
		int j;
		
		if (event.getEntityType() == EntityType.PLAYER)
		{
			Player hider = (Player) event.getEntity();
			
			//Identifies the hider
			for (j = 0 ; j < HideAndSeekGame.hiders.size() ; j++)
			{
				if (HideAndSeekGame.hiders.get(j).player.equals(hider))
				{					
					int iCurrentFinders = HideAndSeekGame.finders.size();
					
					//Creates a new finder object. Hider, people found, finders when found
					HideAndSeekFinder newFinder = new HideAndSeekFinder(hider, 0, iCurrentFinders);
					
					//Adds this to the finders
					HideAndSeekGame.finders.add(newFinder);
					
					//Set score to 0
					HideAndSeekGame.Found.getScore(hider.getName()).setScore(0);
					
					//Announces to all players that a player has been killed
					Announce.announce(this.HideAndSeekGame.getPlayers(), ChatColor.DARK_PURPLE +hider.getDisplayName() +ChatColor.LIGHT_PURPLE +" died");
					Announce.announce(this.HideAndSeekGame.getPlayers(), ChatColor.DARK_PURPLE +hider.getDisplayName() +ChatColor.LIGHT_PURPLE +" is now a seeker");
					
					HideAndSeekGame.hiders.remove(j);
					
					hider.teleport(HideAndSeekGame.spawnPoint);
					break;
				}
			}
		}
	}
	
	public void unRegister()
	{
		HandlerList.unregisterAll(this);
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[Minigames] [Hide] DamageDone unregistered");
	}
} //End Class
//Created by Bluecarpet in London
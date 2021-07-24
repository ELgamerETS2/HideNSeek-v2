package me.elgamer.hidenseek.utilities;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.minigames.Main;
import me.elgamer.minigames.utilities.User;

public class Lobby {

	//Essentials
	public Location spawn;
	public World world;
	public ArrayList<Player> players;

	//Stores amount of finders wanted
	private int iFinders;

	public boolean gameIsRunning;
	private boolean gameIsStarting;
	int iTimer;
	boolean bTerminate;
	BukkitTask Task;
	
	private Game game;


	public Lobby(FileConfiguration config) {

		this.world = Bukkit.getWorld(config.getString("world"));

		//Set up location for hide lobby
		this.spawn = new Location(world,
				config.getDouble("spawn.x"),
				config.getDouble("spawn.y"),
				config.getDouble("spawn.z"),
				(float) config.getDouble("spawn.yaw"),
				(float) config.getDouble("spawn.pitch"));

		//Create a list of players
		players = new ArrayList<Player>();

		Bukkit.broadcastMessage(ChatColor.GREEN + "Created Hide'n'Seek lobby!");

		Bukkit.getScheduler().runTaskTimer(HideNSeek.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				if (players.size() >= config.getInt("minimum") && !gameIsRunning && !gameIsStarting)
				{
					gameStartCountdown();
				}
			}
		}, 0, 100L);
	}

	/*
	private void reset()
	{
		//Reset finders to 0
		this.iFinders = 0;

		//Create the map
		this.Map = new HideAndSeekMap();

		//Reset the booleans
		gameIsRunning = false;
		gameIsStarting = false;

		iTimer = 60;
	}
	 */

	//Handles player attmepting to join the hide and seek lobby
	public static void joinLobby(User u) {

		u.p.teleport(HideNSeek.lobby.spawn);

		//Adds player to the list
		HideNSeek.lobby.players.add(new Player(u));

		//Send welcome message
		u.p.sendMessage(ChatColor.LIGHT_PURPLE +"Welcome to the Hide'n'Seek lobby");

		/*
		//Announces to all players that a player has joined the loobby
		Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.DARK_PURPLE +player.getDisplayName() +ChatColor.LIGHT_PURPLE +" has joined the Hide and Seek lobby"));

		//Announces the amount of players now in the lobby
		if (players.size() == 1)
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" player is now in the lobby"));
		else
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" players are now in the lobby"));
		 */
	}

	//Deals with players leaving the lobby
	public static void leaveLobby(Player p) {
		
		HideNSeek.lobby.players.remove(p);
		p.u.p.teleport(Main.lobby.spawn);
		p.u.p.sendMessage(ChatColor.LIGHT_PURPLE +"You have left the Hide'n'Seek lobby");
		
		/*
			//Announces that they have left
			Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.DARK_PURPLE +player.getDisplayName() +ChatColor.LIGHT_PURPLE +" has left the Hide and Seek lobby"));

			//Announces the amount of players now in the lobby
			if (players.size() == 1)
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" player in lobby"));
			else
				Announce.announce((Player[]) players.toArray(new Player[players.size()]), (ChatColor.LIGHT_PURPLE +""+players.size() +" players in lobby"));

			*/
	}

	public void gameStartCountdown()
	{
		gameIsStarting = true;
		iTimer = 60;
		System.out.println("Game is starting, timer is 60");

		Task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				if (gameIsRunning)
				{
					gameIsStarting = false;
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}

				if (players.size() < Main.getInstance().getConfig().getInt("minimum"))
				{
					gameIsStarting = false;
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}

				if (iTimer == 0)
				{
					//Announce.announce(players, ChatColor.LIGHT_PURPLE +"Game starting now");
					iTimer = 60;
					startGame();
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}
				else if (iTimer % 10 == 0)
				{
					//Announce.announce(players, ChatColor.LIGHT_PURPLE +"Game starting in "+iTimer +" seconds");
				}
				else if (iTimer < 10)
				{
					//Announce.announce(players, ChatColor.LIGHT_PURPLE +"Game starting in "+iTimer);
				}

				iTimer--;
			}
		}, 0, 20L);
	}

	//Triggers the game to start
	public void startGame()
	{
		gameIsRunning = true;

		//Sets finders to 1
		iFinders = 1;

		//Creates a new game and returns the gameID
		//game = new Game((Player[]) players.toArray(new Player[players.size()]), iFinders, Map.iMapID, Main.getInstance(), this);

		//Resets player list
		players.clear();

		gameIsStarting = false;

		//Starts the game running
		//game.start();
	}

	/*
	//Deals with the game finishing
	protected void gameFinished(Player[] exitingPlayers, ArrayList<HideAndSeekFinder> finders, boolean gameRan)
	{
		//Adds players leaving game back into lobby players
		//Resets their scoreboard
		for (Player player : exitingPlayers)
		{
			//Adds player to lobby
			playerJoinLobby(player);
			//Resets their scoreboard
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}

		//Sets game as not running
		//-Shouldn't be needed but it there for security
		gameIsRunning = false;

		//Tests whether the game successfully started
		if (gameRan)
		{
			//Store game statistics
			Statistics Statistics = new Statistics(CorePlugin);
			Statistics.storeHideAndSeekPoints(finders, HideGame.game.getGameID());
		}

		//Reset hide and seek game
		HideGame.reset();
	}
	*/
}
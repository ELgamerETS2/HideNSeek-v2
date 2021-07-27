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

public class Lobby {

	//Essentials
	public Location spawn;
	public World world;
	public ArrayList<Player> players;

	public boolean gameIsRunning;
	private boolean gameIsStarting;
	int iTimer;
	boolean bTerminate;
	BukkitTask Task;

	public Game game;


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

	//Handles player attmepting to join the hide and seek lobby
	public static void joinLobby(Player p) {

		p.u.p.teleport(HideNSeek.lobby.spawn);
		ArrayList<Player> players = HideNSeek.lobby.players;

		//Adds player to the list
		players.add(p);

		//Send welcome message
		p.u.p.sendMessage(ChatColor.LIGHT_PURPLE +"Welcome to the Hide'n'Seek lobby");

		//Announces to all players that a player has joined the loobby
		Announce.announce(players, (ChatColor.DARK_PURPLE + p.u.p.getDisplayName() + ChatColor.LIGHT_PURPLE +" has joined the Hide and Seek lobby"));

		//Announces the amount of players now in the lobby
		if (players.size() == 1) {
			Announce.announce(players, (ChatColor.LIGHT_PURPLE +""+players.size() +" player is now in the lobby"));
		} else {
			Announce.announce(players, (ChatColor.LIGHT_PURPLE +""+players.size() +" players are now in the lobby"));
		}
	}

	//Deals with players leaving the lobby
	public static void leaveLobby(Player p) {

		ArrayList<Player> players = HideNSeek.lobby.players;
		players.remove(p);
		p.u.p.teleport(Main.lobby.spawn);
		p.u.p.sendMessage(ChatColor.LIGHT_PURPLE +"You have left the Hide'n'Seek lobby");

		//Announces that they have left
		Announce.announce(players, (ChatColor.DARK_PURPLE + p.u.p.getDisplayName() + ChatColor.LIGHT_PURPLE +" has left the Hide and Seek lobby"));

		//Announces the amount of players now in the lobby
		if (players.size() == 1) {
			Announce.announce(players, (ChatColor.LIGHT_PURPLE + "" + players.size() + " player in lobby"));
		} else {
			Announce.announce(players, (ChatColor.LIGHT_PURPLE + "" + players.size() + " players in lobby"));
		}
	}

	public void gameStartCountdown() {
		
		gameIsStarting = true;
		iTimer = 60;
		System.out.println("Game is starting, timer is 60");

		Task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (gameIsRunning) {
					gameIsStarting = false;
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}

				if (players.size() < Main.getInstance().getConfig().getInt("minimum")) {
					gameIsStarting = false;
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}

				if (iTimer == 0) {
					Announce.announce(players, ChatColor.LIGHT_PURPLE + "Game starting now");
					iTimer = 60;
					startGame();
					Bukkit.getScheduler().cancelTask(Task.getTaskId());
					return;
				}
				else if (iTimer % 10 == 0) {
					Announce.announce(players, ChatColor.LIGHT_PURPLE + "Game starting in " + iTimer + " seconds");
				}
				else if (iTimer < 10) {
					Announce.announce(players, ChatColor.LIGHT_PURPLE + "Game starting in " + iTimer);
				}

				iTimer--;
			}
		}, 0, 20L);
	}

	//Triggers the game to start
	public void startGame() {
		gameIsRunning = true;

		//Creates a new game and returns the gameID
		game = new Game(players);

		//Resets player list
		players.clear();

		gameIsStarting = false;

		//Starts the game running
		game.startGame();
	}

	//Deals with the game finishing
	protected void gameFinished(ArrayList<Player> exitingPlayers, ArrayList<Player> seekers, boolean gameRan) {
		//Adds players leaving game back into lobby players
		//Resets their scoreboard
		for (Player p : exitingPlayers)
		{
			//Adds player to lobby
			joinLobby(p);
			//Resets their scoreboard
			p.u.p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}

		//Sets game as not running
		//-Shouldn't be needed but it there for security
		gameIsRunning = false;

		//Tests whether the game successfully started
		if (gameRan)
		{
			//Store game statistics
			//Statistics Statistics = new Statistics(CorePlugin);
			//Statistics.storeHideAndSeekPoints(finders, HideGame.game.getGameID());
		}

		//Reset hide and seek game
		game = null;
	}
}
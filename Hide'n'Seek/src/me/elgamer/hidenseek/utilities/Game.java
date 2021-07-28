package me.elgamer.hidenseek.utilities;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import me.elgamer.hidenseek.HideNSeek;
import me.elgamer.hidenseek.listeners.DamageDone;
import me.elgamer.hidenseek.listeners.MoveEvent;
import me.elgamer.hidenseek.sql.MapData;
import me.elgamer.minigames.Main;
import me.elgamer.minigames.sql.GameLog;
import me.elgamer.minigames.sql.GameTable;

public class Game {
	public ArrayList<Player> players;
	public ArrayList<Player> seekers;
	public ArrayList<Player> hiders;
	public int seekerCount;
	public int iStartingHiders;
	public Map map;
	public Location spawn;

	public int gameID;
	public int mapID;

	private DamageDone DD;
	private MoveEvent SM;

	private boolean registered;

	public boolean bTerminate;
	private boolean bGamePlayStarted;

	//Scoreboard
	protected ScoreboardManager SBM;
	protected Scoreboard SB;
	protected Team TeamH;
	protected Team TeamS;
	public Objective Found;
	protected Objective Hiders;
	protected Score score;

	//Speed
	public float seekerSpeed;
	public float hiderSpeed;

	public Game(ArrayList<Player> players) { //Input to be from lobby for whatever preferences are decided

		this.players.addAll(players);

		mapID = 0;
		bTerminate = false;
		registered = false;

		bGamePlayStarted = false;

		//Get scoreboard
		SBM = Bukkit.getScoreboardManager();
		SB = SBM.getNewScoreboard();

		//Registers the teams
		TeamH = SB.registerNewTeam("Hiders");
		TeamS = SB.registerNewTeam("Seekers");

		//Registers the objectives
		Found = SB.registerNewObjective("Seekers", "dummy", "Seekers");
		Hiders = SB.registerNewObjective("Hiders", "dummy", "Hiders");

		//Set the teams
		TeamH.setDisplayName("Hiders");
		TeamS.setDisplayName("Seekers");

		TeamH.setAllowFriendlyFire(false);
		TeamS.setAllowFriendlyFire(false);

		Found.setDisplayName("Found");
		Found.setDisplaySlot(DisplaySlot.SIDEBAR);
		Found.setRenderType(RenderType.INTEGER);

		Hiders.setDisplayName("Hiders");
		Hiders.setDisplaySlot(DisplaySlot.SIDEBAR);
		Hiders.setRenderType(RenderType.INTEGER);
		
		seekerSpeed = (float) HideNSeek.getInstance().getConfig().getDouble("speed.seeker");
		hiderSpeed = (float) HideNSeek.getInstance().getConfig().getDouble("speed.hider");
	}

	public void startGame() {

		//Sets up the game - decides on a seeker
		createTeams();

		//Chooses map
		chooseMap();

		spawn = MapData.getSpawn(mapID);

		//Starts 10 second countdown into game
		Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			int time = 10; //or any other number you want to start countdown from
			@Override
			public void run() {
				if (this.time == 0) {
					return;
				}
				//Terminates the process if the game has been terminated
				if (bTerminate) {
					return;
				}

				if (time == 1) {
					Announce.announce(players, (ChatColor.DARK_PURPLE +""+time +" Second until start"));
				} else {
					Announce.announce(players, (ChatColor.DARK_PURPLE +""+time +" Seconds until start"));
				}
				this.time--;
			}
		}, 0L, 20L);

		if (bTerminate) {
			return;
		}

		//Teleport players and get them fitted
		teleportPlayers();
		allowFlight();

		//Run game after 10 seconds
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				if (bTerminate)
				{
					return;
				}

				disableFlight();
				teleportPlayers();

				//Actual gameplay
				bGamePlayStarted = true;
				game();
			}
		}, 200L);
	}

	private void createTeams()
	{
		int i;
		int iRandom = -1;

		Player seeker;

		//Find a seeker
		for (i = 0 ; i < seekerCount ; i++) {

			iRandom = ((int) (Math.random() * players.size()));
			seeker = players.get(iRandom);

			//Tests whether the seeker selected is already in the list
			if (seekers.contains(seeker)) {
				i--;
				continue;
			} else {
				seekers.add(seeker);
				TeamS.addEntry(seeker.u.p.getDisplayName());
				score = Found.getScore(seeker.u.p.getDisplayName());
				score.setScore(0);
				seeker.u.p.setScoreboard(SB);
			}

		}

		//Add the hiders
		for (Player p: players) {
			if (!(seekers.contains(p))) {
				hiders.add(p);
				TeamH.addEntry(p.u.p.getDisplayName());
				score = Found.getScore(p.u.p.getDisplayName());
				p.u.p.setScoreboard(SB);
			}
		}

		//Announce seekers
		for (Player p : seekers)
		{
			Announce.announce(players, (ChatColor.DARK_PURPLE + p.u.p.getDisplayName() +ChatColor.LIGHT_PURPLE +" is a seeker"));
		}
	}

	//-------------------------------------
	//-----------Chooses the map-----------
	//-------------------------------------
	public void chooseMap()
	{
		//Collects a list of Maps
		ArrayList<Integer> maps = MapData.getMaps();

		//If the MapID is 0 (Not already set), change the MapID to a random hide and seek map
		if (mapID == 0) {
			//Chooses a random index from the array
			int iTotalMaps = maps.size();
			System.out.println("[Minigames] [SQL Maps] Maps Found = " +iTotalMaps);
			int iRandom = ( (int) (Math.random() * iTotalMaps) );

			mapID = maps.get(iRandom);
			map = new Map(mapID);
		}

		//Announce choice of map
		Announce.announce(players, (ChatColor.LIGHT_PURPLE +"The map choosen is "+ChatColor.DARK_PURPLE + map.szLocation + ChatColor.LIGHT_PURPLE+" by "+ChatColor.DARK_PURPLE + map.szCreator));
	}

	//-------------------------------------
	//-----------Teleports players---------
	//-------------------------------------
	public void teleportPlayers() {
		for (Player p : players) {
			p.u.p.teleport(spawn);
		}
	}

	private void allowFlight() {
		for (Player p : seekers) {
			p.u.p.setAllowFlight(true);
			p.u.p.setFlying(true);
			p.u.p.setFlySpeed(0.3F);
		}

		for (Player p : hiders) {
			p.u.p.setAllowFlight(true);
			p.u.p.setFlying(true);
			p.u.p.setFlySpeed(0.3F);
		}
	}

	public void disableFlight() {
		for (Player p : seekers) {
			p.u.p.setFlySpeed(0.2F);
			p.u.p.setFlying(false);
			p.u.p.setAllowFlight(false);
		}

		for (Player p : hiders)
		{
			p.u.p.setFlySpeed(0.2F);
			p.u.p.setFlying(false);
			p.u.p.setAllowFlight(false);
		}
	}

	//-------------------------------------
	//-----------Actual gameplay-----------
	//-------------------------------------
	private void game() {

		HideNSeek instance = HideNSeek.getInstance();
		gameID = GameLog.addGame(GameTable.getID(instance.game), mapID);
		Announce.announce(players, (ChatColor.LIGHT_PURPLE + "The game has begun"));
		Announce.announce(players, (ChatColor.LIGHT_PURPLE + "There are " + map.iWait + " seconds for hiders to hide"));

		for (Player p : seekers) {
			p.u.p.sendMessage(ChatColor.DARK_PURPLE + "You are restricted for " + map.iWait + " seconds");
			p.u.p.setWalkSpeed(0);
			p.u.p.setFlySpeed(0);

			for (Player h : hiders) {
				p.u.p.hidePlayer(instance, h.u.p);
			}
		}

		//Plays last 3 seconds
		Bukkit.getScheduler().runTaskTimer(instance, new Runnable() {
			int time = 3; //or any other number you want to start countdown from

			@Override
			public void run() {
				if (this.time == 0) {
					return;
				}
				Announce.announce(players, (ChatColor.DARK_PURPLE + "" + time));
				Announce.playNote(players, Sound.BLOCK_GLASS_BREAK);             
				this.time--;
			}
		}, (map.iWait - 3) * 20L, 20L);

		//Releases seekers
		Bukkit.getScheduler().runTaskLater(instance, new Runnable()
		{
			@Override
			public void run()
			{
				Announce.playNote(players, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST);

				SM.unRegister();

				//Then allow hiders to be found
				for (Player p : seekers) {
					p.u.p.setWalkSpeed(seekerSpeed);
					p.u.p.setFlySpeed((float) 0.2);
					for (Player h : hiders) {
						h.u.p.showPlayer(instance, p.u.p);
					}
				}

				//Sets the speed of hiders
				for (Player p : hiders) {
					p.u.p.setWalkSpeed(hiderSpeed);
				}

				Announce.announce(players, (ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "The seekers have been release"));
			}
		}, map.iWait * 20L);

		if (bTerminate) {
			return;
		}

		registered = true;

		//Creates the hit listener
		DD = new DamageDone(this, instance);
	}

	//----------------------------------------
	//-------Deals with players leaving-------
	//----------------------------------------
	public void playerLeave(Player p) {

		//Checks whether they are in the hiders list and removes them
		hiders.remove(p);
		seekers.remove(p);
		players.remove(p);

		//Checks the size of each list to check if the game needs to end
		if (hiders.size() == 0) {
			finalHiderLeft();
		} else if (seekers.size() == 0) {
			finalFinderLeft();
		}
	}

	//Deals with final hider leaving
	private void finalHiderLeft() {
		Announce.announce(players, (ChatColor.GREEN +"The final hider left, the seekers have won!"));
		terminate();
	}

	//Deals with final seeker leaving
	private void finalFinderLeft() {
		Announce.announce(players, (ChatColor.GREEN +"The final seeker left, the hiders have won!"));
		terminate();
	}

	//Terminates the game
	public void terminate() {
		
		try {
			//Records game termination in the log
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game has ended");

			//Sets game as not running in the lobby
			//This is very important as if someone now leaves, it will prevent the game from attempting the remove them
			//...from the game which could then trigger another termination on top of the current one.
			HideNSeek.lobby.gameIsRunning = false;

			//Unregisters damage listener
			if (registered) {
				DD.unRegister();
				registered = false;
			}

			//Announce termination and set terminate to true
			this.bTerminate = true;
			Announce.announce(players, (ChatColor.GREEN +"The game has ended!"));

			//Records whether game play started in the log
			if (!bGamePlayStarted) {
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game play did not start");
			} else {
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game play did start");
			}
			
			//Checks whether game play started (i.e game added to DB)
			if (bGamePlayStarted) {
				//Record end of game in database
				GameLog.setGameEnd(gameID);
			}

			//Wait 6 seconds before sending players back to the lobby
			Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{            	
					//Notifies lobby of game ending
					HideNSeek.lobby.gameFinished(players, seekers, bGamePlayStarted);
				}
			}, 120L);
		}
		catch (Exception e)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] terminate() - Error teriminating game");
			e.printStackTrace();
		}
	}
}

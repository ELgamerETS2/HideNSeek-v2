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
import me.elgamer.hidenseek.sql.MapData;
import me.elgamer.minigames.Main;

public class Game {
	public final Game game;
	public ArrayList<Player> players;
	public ArrayList<Player> seekers;
	public ArrayList<Player> hiders;
	public int seekerCount;
	public int iStartingHiders;
	public Map map;
	protected Location spawn;
	
	public int mapID;
	
	private DamageDone DD;
	private SeekerMove SM;
	
	private boolean registered;
	
	protected boolean bTerminate;
	private boolean bGamePlayStarted;
	
	//Scoreboard
	protected ScoreboardManager SBM;
	protected Scoreboard SB;
	protected Team TeamH;
	protected Team TeamS;
	protected Objective Found;
	protected Objective Hiders;
	protected Score score;
	
	public Game(ArrayList<Player> players) { //Input to be from lobby for whatever preferences are decided
				
		this.players = players;

		map = new Map();
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
	}
	
	public void startGame() {
		
		//Sets up the game - decides on a seeker
		createTeams();
		
		//Chooses map
		chooseMap();
		
		spawn = MapData.getSpawn(mapID);
		
		//Starts 10 second countdown into game
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable()
        {
            int time = 10; //or any other number you want to start countdown from
            @Override
            public void run()
            {
                if (this.time == 0)
                {
                    return;
                }
                //Terminates the process if the game has been terminated
                if (bTerminate)
                {
                	return;
                }
                if (time == 1)
        			//Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +""+time +" Second until start"));
                else
        			//Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +""+time +" Seconds until start"));
                this.time--;
            }
        }, 0L, 20L);
        
        if (bTerminate)
        {
        	return;
        }
        
		//Teleport players and get them fitted
		teleportPlayers();
		allowFlight();
		
        if (bTerminate)
        {
        	return;
        }
        
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
                
            	game.setTimeStart();
            	game.storeGameInDatabase();
            	game.selectLastInsertID();
            	
        		//Actual gameplay
            	bGamePlayStarted = true;
        		game();
            }
        }, 200L);
	}
	
	private void createTeams()
	{
		int i, j;
		int iRandom = -1;
				
		boolean bUnique;
		Player seeker;
		Player hider;
		
		//Find a seeker
		for (i = 0 ; i < seekerCount ; i++) {
			bUnique = true;
			
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
		
		//Announce finders
		for (Player p : seekers)
		{
			//Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +newFinder.player.getDisplayName() +ChatColor.LIGHT_PURPLE +" is a seeker"));
		}
	}
	
	//-------------------------------------
	//-----------Chooses the map-----------
	//-------------------------------------
	public void chooseMap()
	{
		//Collects a list of Maps
		int[] maps = MapData.getMaps();
		
		//If the MapID is 0 (Not already set), change the MapID to a random hide and seek map
		if (mapID == 0) {
			//Chooses a random index from the array
			int iTotalMaps = maps.length;
			System.out.println("[Minigames] [SQL Maps] Maps Found = " +iTotalMaps);
			int iRandom = ( (int) (Math.random() * iTotalMaps) );
			
			mapID = maps[iRandom];
		}
		
		//Announce choice of map
		//Announce.announce(getPlayers(), (ChatColor.LIGHT_PURPLE +"The map choosen is "+ChatColor.DARK_PURPLE +Map.szLocation +ChatColor.LIGHT_PURPLE+" by "+ChatColor.DARK_PURPLE+Map.szCreator));
	}
	
	//-------------------------------------
	//-----------Teleports players---------
	//-------------------------------------
	public void teleportPlayers()
	{
		int i;
		for (Player p : players) {
			p.u.p.teleport(spawn);
		}
	}
	
	/*
	private void allowFlight()
	{
		for (HideAndSeekFinder finder : finders)
		{
			finder.player.setAllowFlight(true);
			finder.player.setFlying(true);
			finder.player.setFlySpeed(0.3F);
		}
		
		for (HideAndSeekHider hider : hiders)
		{
			hider.player.setAllowFlight(true);
			hider.player.setFlying(true);
			hider.player.setFlySpeed(0.3F);
		}
	}
	*/
	
	/*
	public void disableFlight()
	{
		for (HideAndSeekFinder finder : finders)
		{
			finder.player.setFlySpeed(0.2F);
			finder.player.setFlying(false);
			finder.player.setAllowFlight(false);
		}
		
		for (HideAndSeekHider hider : hiders)
		{
			hider.player.setFlySpeed(0.2F);
			hider.player.setFlying(false);
			hider.player.setAllowFlight(false);
		}
	}
	*/
	
	//-------------------------------------
	//-----------Actual gameplay-----------
	//-------------------------------------
	private void game()
	{
		int i, j;
		
		HideNSeek instance = HideNSeek.getInstance();
		//Announce.announce(getPlayers(), (ChatColor.LIGHT_PURPLE +"The game has begun"));
		//Announce.announce(getPlayers(), (ChatColor.LIGHT_PURPLE +"There are " +Map.iWait +" seconds for hiders to hide"));
		
		for (Player p : seekers) {
			p.u.p.sendMessage(ChatColor.DARK_PURPLE +"You are restricted for " + Map.iWait +" seconds");
			p.u.p.setWalkSpeed(0);
			p.u.p.setFlySpeed(0);
			
			for (Player h : hiders) {
				p.u.p.hidePlayer(instance, h.u.p);
			}
		}
        
		//Plays last 3 seconds
        Bukkit.getScheduler().runTaskTimer(instance, new Runnable()
        {
            int time = 3; //or any other number you want to start countdown from

            @Override
            public void run()
            {
                if (this.time == 0)
                {
                    return;
                }
			    Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +""+time));
				Announce.playNote(players, Sound.BLOCK_GLASS_BREAK);             
                this.time--;
            }
        }, (Map.iWait - 3) * 20L, 20L);
        
        //Releases seekers
        Bukkit.getScheduler().runTaskLater(instance, new Runnable()
        {
            @Override
            public void run()
            {
            	int i, j;
        		Announce.playNote(players, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST);
        		
    			SM.unRegister();
    			
        		//Then allow hiders to be found
        		for (i = 0 ; i < finders.size() ; i++)
        		{
        			finders.get(i).player.setWalkSpeed((float) plugin.getConfig().getDouble("SeekerSpeed"));
        			finders.get(i).player.setFlySpeed((float) 0.2);
        			for (j = 0 ; j < hiders.size() ; j++)
        			{
        				finders.get(i).player.showPlayer(plugin, hiders.get(j).player);
        			}
        		}
        		
        		//Sets the speed of hiders
        		for (i = 0 ; i < hiders.size() ; i++)
        		{
        			hiders.get(i).player.setWalkSpeed((float) plugin.getConfig().getDouble("HiderSpeed"));
        		}
        		
        		Announce.announce(getPlayers(), (ChatColor.DARK_PURPLE +"" +ChatColor.BOLD +"The seekers have been release"));
            }
        }, Map.iWait * 20L);
        
        if (bTerminate)
        {
        	return;
        }
        
		registered = true;
		
        //Creates the hit listener
        DD = new DamageDone(this, plugin);
   	}
	
	//----------------------------------------
	//-------Deals with players leaving-------
	//----------------------------------------
	public void playerLeave(Player player)
	{
		int i;
		boolean bRemovedFromHiders = false;
		
		//Checks whether they are in the hiders list and removes them
		for (i = 0 ; i < hiders.size() ; i++)
		{
			if (hiders.get(i).player.equals(player))
			{
				hiders.remove(i);
				bRemovedFromHiders = true;
				break;
			}
		}
		
		//If not removed from hiders, checks whether they are in the finders list and removes them
		if (!bRemovedFromHiders)
		{
			//Goes through seekers
			for (i = 0 ; i < finders.size() ; i++)
			{
				//If the player is found within the seekers list,
				if (finders.get(i).player.equals(player))
				{
					//Remove them from it
					finders.remove(i);
					break;
				}
			}
		}
		
		//Update the players array - Used for announcements
		//Update size
		players = new Player[finders.size() + hiders.size()];
		//Go through finders and add those
		for (i = 0 ; i < finders.size() ; i++)
		{
			getPlayers()[i] = finders.get(i).player;
		}
		//Go through hiders and add those
		for (i = finders.size() ; i < players.length ; i++)
		{
			getPlayers()[i] = hiders.get(i).player;
		}
		
		//Checks the size of each list to check if the game needs to end
		if (bRemovedFromHiders)
		{
			if (hiders.size() == 0)
			{
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] Final hider left");
				finalHiderLeft();
			}
		}
		else if (finders.size() == 0)
		{
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] Final seeker left");
			finalFinderLeft();
		}
	}
	
	//Deals with final hider leaving
	private void finalHiderLeft()
	{
		Announce.announce(getPlayers(), (ChatColor.GREEN +"The final hider left, the seekers have won!"));
		terminate();
	}
	
	//Deals with final seeker leaving
	private void finalFinderLeft()
	{
		Announce.announce(getPlayers(), (ChatColor.GREEN +"The final seeker left, the hiders have won!"));
		terminate();
	}
	
	//Terminates the game
	public void terminate()
	{
		try
		{
			//Records game termination in the log
			Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game ended");

			//Sets game as not running in the lobby
			//This is very important as if someone now leaves, it will prevent the game from attempting the remove them
			//...from the game which could then trigger another termination on top of the current one.
			plugin.HSLobby.gameIsRunning = false;

			//Unregisters damage listener
			if (registered)
			{
				DD.unRegister();
				registered = false;
			}

			//Announce termination and set terminate to true
			this.bTerminate = true;
			Announce.announce(getPlayers(), (ChatColor.GREEN +"The game has ended!"));

			//Records whether game play started in the log
			if (!bGamePlayStarted)
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game play did not start");
			else
				Bukkit.getConsoleSender().sendMessage("[Minigames] [Hide] The game play did start");

			//Checks whether game play started (i.e game added to DB)
			if (bGamePlayStarted)
			{
				//Record end of game in database
				game.setTimeEnd();
				game.storeGameEndInDatabase();
			}

			//Wait 6 seconds before sending players back to the lobby
			Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
			{
				@Override
				public void run()
				{            	
					//Notifies lobby of game ending
					plugin.HSLobby.gameFinished(getPlayers(), finders, bGamePlayStarted);
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

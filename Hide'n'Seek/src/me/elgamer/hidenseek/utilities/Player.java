package me.elgamer.hidenseek.utilities;

import me.elgamer.minigames.utilities.User;

public class Player {
	
	public int iFound;
	
	public User u;
	
	public Player(User u) {
		
		this.u = u;
		u.inLobby = false;
		
		iFound = 0;		
		
	}
	
	public void reset() {
		iFound = 0;
	}

}

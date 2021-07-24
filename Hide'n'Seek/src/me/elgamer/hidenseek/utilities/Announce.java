package me.elgamer.hidenseek.utilities;

import java.util.ArrayList;

import org.bukkit.Sound;

public class Announce {

	public static void announce(ArrayList<Player> players, String szMessage) {
		for (Player p : players) {
			p.u.p.sendMessage(szMessage);
		}
	}

	public static void playNote(ArrayList<Player> players, Sound Sound) {
		for (Player p : players) {
			p.u.p.playSound(p.u.p.getLocation(), Sound, 1.0F, 1.0F);
		}
	}
}

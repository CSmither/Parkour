package com.martenm.parkour.helpers.highscores;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Highscores {
	
	public static long getHighScore(JavaPlugin plugin, String parkour, Player player){
		return LocalStorage.getHighScore(plugin, parkour, player);
	}
	
	public static boolean setHighScore(JavaPlugin plugin, String parkour, Player player, long time){
		LocalStorage.setHighScore(plugin, parkour, player, time);
		return true;
	}
	
}

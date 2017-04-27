package com.martenm.parkour.helpers.highscores;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.martenm.parkour.helpers.Config;

public class LocalStorage {

	public static long getHighScore(JavaPlugin plugin, String parkour, Player player){
		
		Config HighscoresFile = new Config(plugin, "\\highscores\\" + parkour);
		if(!HighscoresFile.contains("highscores")) HighscoresFile.set("highscores", "{}");
		
		return HighscoresFile.getLong("highscores." + player.getUniqueId() + ".time");
	}
	
	public static boolean setHighScore(JavaPlugin plugin, String parkour, Player player, long time){
		
		Config HighscoresFile = new Config(plugin, "\\highscores\\" + parkour);
		if(!HighscoresFile.contains("highscores")) HighscoresFile.set("highscores", "{}");
		
		HighscoresFile.set("highscores." + player.getUniqueId() + ".name" , player.getName());
		HighscoresFile.set("highscores." + player.getUniqueId() + ".time", time);
		HighscoresFile.save();
		
		return true;
	}
	
	
}

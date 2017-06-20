package com.martenm.parkour.helpers.highscores;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.martenm.parkour.helpers.Config;

public class LocalStorage {

	public static long getHighScore(JavaPlugin plugin, String parkour, Player player){
		
		Config HighscoresFile = new Config(plugin, "/highscores/" + parkour);
		if(!HighscoresFile.contains("highscores")) HighscoresFile.set("highscores", "{}");
		
		return HighscoresFile.getLong("highscores." + player.getUniqueId() + ".time");
	}
	
	public static Map<String,Double> getHighScores(JavaPlugin plugin, String parkour){	// For Leader Heads Support
		Config HighscoresFile = new Config(plugin, "/highscores/" + parkour);
		if(!HighscoresFile.contains("highscores")) throw new IllegalArgumentException("No Parkour called "+parkour);
		Map<String,Double> scores = new HashMap<String,Double>();
		MemorySection highscores = (MemorySection)HighscoresFile.get("highscores");
		MemorySection person=(MemorySection)highscores.get("b6d8ea3a-ca98-4087-aa7b-6215db6e5038");
		String name=(String)person.get("name");
		Bukkit.getLogger().info(name);
		highscores.getValues(false).values();
		Collection<Object> results=highscores.getValues(false).values();
		for (Object result : results){
			MemorySection player = (MemorySection)result;
			scores.put((String)player.get("name"), (int)player.get("time")/1000.0);
		}
		return scores;
	}
	
	public static boolean setHighScore(JavaPlugin plugin, String parkour, Player player, long time){
		
		Config HighscoresFile = new Config(plugin, "/highscores/" + parkour);
		if(!HighscoresFile.contains("highscores")) HighscoresFile.set("highscores", "{}");
		
		HighscoresFile.set("highscores." + player.getUniqueId() + ".name" , player.getName());
		HighscoresFile.set("highscores." + player.getUniqueId() + ".time", time);
		HighscoresFile.save();
		
		return true;
	}
	
	
}

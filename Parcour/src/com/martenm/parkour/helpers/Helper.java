package com.martenm.parkour.helpers;

import com.martenm.parkour.MainClass;

import net.md_5.bungee.api.ChatColor;

public class Helper {

	public static String getTime(long time) {
		if (time < 0) {
			return ChatColor.RED + "ERROR";
		}
		int minutes = 0;
		int seconds = 0;
		int miliseconds = 0;
		
		seconds = (int) time / 1000;
		miliseconds = (int) (time - seconds * 1000);
		minutes = (int) seconds / 60;
		seconds -= minutes * 60;
		
		
		return minutes + ":" + seconds + ":" + miliseconds;
	}

	public static String formatString(MainClass plugin, String message){
		return ChatColor.translateAlternateColorCodes('&', message).replace("%prefix%", plugin.prefix);
	}
	
}

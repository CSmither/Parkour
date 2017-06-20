package com.martenm.parkour.helpers.highscores;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;

public class LeaderHeadsParkour extends DataCollector {
	
public static Map<String,LeaderHeadsParkour> leaderBoards = new HashMap<String,LeaderHeadsParkour>();
private String route;

public LeaderHeadsParkour(String route) {
    super(route, "Parkour", BoardType.DEFAULT, "&bParkour - "+route, "topparkour"+route, Arrays.asList(null, null, "&e{amount} points", null), true, String.class);
    this.route=route;
    Bukkit.getLogger().info("Parkour hooked LeaderHeads for "+route);
}

@Override
public List<Entry<?, Double>> requestAll() {
    Bukkit.getLogger().info("Parkour LH hook Request all: "+route);
    Map<String, Double> map = LocalStorage.getHighScores((JavaPlugin)Bukkit.getPluginManager().getPlugin("Parkour"), route);
    return LeaderHeadsAPI.sortMap(map);
}

}

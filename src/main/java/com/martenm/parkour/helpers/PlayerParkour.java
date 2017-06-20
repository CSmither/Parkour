package com.martenm.parkour.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerParkour {

	private Parkour parkour;
	private Player player;

	private long Time;
	private boolean isPlaying;

	private Location currentCheckpoint;
	
	public List<ParkourBlock> checkpoints;
	private int lastIndex;

	public PlayerParkour(Player player) {
		this.setPlayer(player);
		Time = 0;
		checkpoints = new ArrayList<ParkourBlock>();
	}

	public Parkour getParkour() {
		return parkour;
	}

	public void setParkour(Parkour parkour) {
		this.parkour = parkour;
	}

	public void stopTimer() {
		if (isPlaying) {
			Time = System.currentTimeMillis() - Time;
			isPlaying = false;
		} else
			Time = 0;
	}

	public void startTimer() {
		Time = System.currentTimeMillis();
		checkpoints.clear();
		currentCheckpoint = parkour.checkpoints.get(0).block.getLocation();
		isPlaying = true;
	}

	public long getTime() {
		return Time;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Location getCurrentCheckpoint() {
		return currentCheckpoint;
	}

	public void setCurrentCheckpoint(Location currentCheckpoint) {
		this.currentCheckpoint = currentCheckpoint;
	}
	
	public void addCheckpoint(ParkourBlock checkpoint){
		this.checkpoints.add(checkpoint);
		this.currentCheckpoint = checkpoint.block.getLocation();
	}

	public int getLastIndex() {
		return lastIndex;
	}

	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}

}

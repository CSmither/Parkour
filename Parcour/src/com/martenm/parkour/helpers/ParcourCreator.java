package com.martenm.parkour.helpers;

import org.bukkit.entity.Player;

public class ParcourCreator {

	private Player player;
	private String id;
	private String name;
	private Parkour parkour;

	public ParcourCreator(Player player, String id, String name) {
		this.player = player;
		this.id = id;
		this.name = name;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Parkour getParkour() {
		return parkour;
	}

	public void setParkour(Parkour parkour) {
		this.parkour = parkour;
	}

}

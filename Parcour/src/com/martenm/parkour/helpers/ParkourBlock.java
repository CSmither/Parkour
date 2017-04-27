package com.martenm.parkour.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

public class ParkourBlock {

	public ParkourBlock() {
		this.Texts = new ArrayList<ArmorStand>();
	}

	public Block block;
	public List<ArmorStand> Texts;

}

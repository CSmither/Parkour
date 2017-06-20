package com.martenm.parkour.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import com.martenm.parkour.MainClass;
import com.martenm.parkour.helpers.highscores.LeaderHeadsParkour;

import me.robin.leaderheads.api.LeaderHeadsAPI;
import net.md_5.bungee.api.ChatColor;

public class Parkour {

	private MainClass plugin;

	public Parkour(MainClass plugin) {
		this.plugin = plugin;
	}

	private boolean Status;

	public void open() {
		Status = true;
	}

	public void close() {
		for(PlayerParkour pp : plugin.playing.values()){
			if(pp.getParkour() == this){
				pp.getPlayer().sendMessage(ChatColor.RED + "Parkour has been closed. Your run has been cancelled.");
				plugin.playing.remove(pp.getPlayer());
			}
		}
		Status = false;
	}

	public boolean isOpen() {
		return Status;
	}

	private String ID;
	private String name;
	private String customeName;

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public boolean removeBlock(ParkourBlock pb) {
		if (checkpoints.contains(pb)) {
			for (ArmorStand a : pb.Texts) {
				a.remove();
			}
			checkpoints.remove(pb);
			return true;
		} else
			return false;
	}

	public boolean delete(boolean sure) {
		if (!sure)
			return false;
		Iterator<ParkourBlock> iterator = checkpoints.iterator();
		while (iterator.hasNext()) {
			ParkourBlock pb = iterator.next();
			for (ArmorStand a : pb.Texts) {
				a.remove();
			}
			if (plugin.getConfig().getBoolean("remove plates")) {
				pb.block.setType(Material.AIR);
			}
			pb.block.getLocation().getWorld()
					.spawnParticle(Particle.EXPLOSION_NORMAL, pb.block.getLocation().add(0, 0.4, 0), 0, 0, 0, 4, 0.01);
		}
		checkpoints = null;
		return true;
	}

	public void update() {
		// Remove current armor stands.

		for (ParkourBlock pb : checkpoints) {
			for (int i = 0; i < pb.Texts.size(); i++) {
				pb.Texts.get(i).remove();
			}
		}

		for (int i = 0; i < checkpoints.size(); i++) {

			ArmorStand a = (ArmorStand) checkpoints.get(i).block.getWorld()
					.spawnEntity(
							checkpoints.get(i).block.getLocation()
									.add(0.5,
											plugin.getConfig().getDouble(
													"text height"), 0.5),
							EntityType.ARMOR_STAND);
			a.setMarker(true);
			a.setInvulnerable(true);
			a.setGravity(false);
			a.setVisible(false);
			a.setCollidable(false);
			a.setCustomNameVisible(true);

			if (i == 0) {
				a.setCustomName(ChatColor.translateAlternateColorCodes('&',
						plugin.getConfig().getString("start")));
			} else if (i == checkpoints.size() - 1) {
				a.setCustomName(ChatColor.translateAlternateColorCodes('&',
						plugin.getConfig().getString("finish")));
			} else {
				a.setCustomName(ChatColor.translateAlternateColorCodes('&',
						plugin.getConfig().getString("checkpoint").replace("%number%", i + "")));
			}
			checkpoints.get(i).Texts.add(a);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCustomeName() {
		return customeName;
	}

	public void setCustomeName(String customeName) {
		this.customeName = customeName;
	}
	
	public void setStatus(boolean status){
		this.Status = status;
	}

	public List<ParkourBlock> checkpoints = new ArrayList<ParkourBlock>();

}

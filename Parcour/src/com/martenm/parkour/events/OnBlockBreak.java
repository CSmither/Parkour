package com.martenm.parkour.events;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.martenm.parkour.MainClass;
import com.martenm.parkour.helpers.Parkour;
import com.martenm.parkour.helpers.ParkourBlock;

public class OnBlockBreak implements Listener {

	private MainClass plugin;

	public OnBlockBreak(MainClass plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void OnBlockBreakEvent(BlockBreakEvent event) {

		Block block = event.getBlock();
		if (!block.getType().equals(Material.STONE_PLATE)
				&& !block.getType().equals(Material.WOOD_PLATE)
				&& !block.getType().equals(Material.GOLD_PLATE)
				&& !block.getType().equals(Material.IRON_PLATE)) {
			//event.getPlayer().sendMessage(
			//		plugin.prefix + ChatColor.RED
			//				+ "That's not a valid block...");
			return;
		}

		for (int i = 0; i < plugin.parkours.size(); i++) {
			final Parkour p = plugin.parkours.get(i);
			for (ParkourBlock pb : p.checkpoints) {
				if (!pb.block.getLocation().equals(block.getLocation()))
					continue;
				if (p.removeBlock(pb)) {
					event.getPlayer().sendMessage(
							plugin.prefix + ChatColor.YELLOW
									+ "Checkpoint removed.");
					new BukkitRunnable() {
						public void run() {
							p.update();
						}
					}.runTaskLater(plugin, 1);
					break;
				}
			}
		}

	}
}

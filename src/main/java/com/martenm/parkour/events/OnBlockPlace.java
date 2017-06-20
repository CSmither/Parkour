package com.martenm.parkour.events;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.martenm.parkour.MainClass;
import com.martenm.parkour.helpers.ParcourCreator;
import com.martenm.parkour.helpers.ParkourBlock;

public class OnBlockPlace implements Listener {

	private MainClass plugin;
	public OnBlockPlace(MainClass plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockPlaceEvent event){
		Player player = event.getPlayer();
		
		if(plugin.creating.containsKey(player))
		{
			Block block = event.getBlockPlaced();
			if(!block.getType().equals(Material.STONE_PLATE) && !block.getType().equals(Material.WOOD_PLATE) && !block.getType().equals(Material.GOLD_PLATE) && !block.getType().equals(Material.IRON_PLATE)){
				player.sendMessage(plugin.prefix + ChatColor.RED + "That's not a valid block...");
				return;
			}
			
			final ParcourCreator pc = plugin.creating.get(player);
			if(pc.getParkour().checkpoints.size() == 0)
			{
				//player.sendMessage(plugin.prefix + ChatColor.GREEN + "You placed the start!");
			}
			
			ParkourBlock pb = new ParkourBlock();
			pb.block = block;
			
			pc.getParkour().checkpoints.add(pb);
			
			new BukkitRunnable(){
				public void run(){
					pc.getParkour().update();
				}
			}.runTaskLater(plugin, 1);
			
			
		}
		
	}
	
	
}

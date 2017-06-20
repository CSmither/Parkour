package com.martenm.parkour.events;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.martenm.parkour.MainClass;
import com.martenm.parkour.helpers.Helper;
import com.martenm.parkour.helpers.Parkour;
import com.martenm.parkour.helpers.PlayerParkour;
import com.martenm.parkour.helpers.highscores.Highscores;

public class OnPlayerInteractEvent implements Listener {

	private MainClass plugin;

	public OnPlayerInteractEvent(MainClass plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void OnPlayerInteract(PlayerInteractEvent event) {

		if (event.getAction().equals(Action.PHYSICAL)) {
			Block block = event.getClickedBlock();

			if (!block.getType().equals(Material.STONE_PLATE)
					&& !block.getType().equals(Material.WOOD_PLATE)
					&& !block.getType().equals(Material.GOLD_PLATE)
					&& !block.getType().equals(Material.IRON_PLATE)) {
				return;
			}

			Parkour parkour = null;
			int index = 0;
			for (Parkour p : plugin.parkours) {
				for (int i = 0; i < p.checkpoints.size(); i++) {
					if (p.checkpoints.get(i).block.getLocation().equals(
							block.getLocation())) {
						parkour = p;
						index = i;
						break;
					}
				}
				if (parkour != null)
					break;
			}

			if (parkour == null) {
				return;
			}

			final Player player = event.getPlayer();

			if (plugin.playing.containsKey(player)) {
				final PlayerParkour pp = plugin.playing.get(player);
				if (!pp.getParkour().equals(parkour)) {
					player.sendMessage(Helper.formatString(plugin, plugin
							.getConfig().getString("wrong parkour")));
					return;
				}

				// If finish
				if (index == parkour.checkpoints.size() - 1) {
					pp.stopTimer();

					final Parkour fparkour = parkour;
					BukkitRunnable task = new BukkitRunnable() {
						public void run() {

							long HighScore = Highscores.getHighScore(plugin,
							fparkour.getName(), player);
							long difference = pp.getTime() - HighScore;

							String time = Helper.getTime(pp.getTime());
							String s_highscore = Helper.getTime((HighScore));
							//String s_difference = (difference < 0: ChatColor.GREEN : ChatColor.RED) + "" );
							if (difference < 0 || HighScore == 0){
								//new highscore
								
								Highscores.setHighScore(plugin,
										fparkour.getName(), player,
										pp.getTime());
								
								player.sendMessage(Helper.formatString(
										plugin,
										plugin.getConfig()
												.getString("finish message highscore")
												.replace("%time%", time).replace("%highscore%", time)));
								
								//Message the whole server
								if(!plugin.getConfig().getString("finish message highscore server").equals("")){
									plugin.getServer().broadcastMessage(Helper.formatString(plugin, plugin.getConfig().getString("finish message highscore server"))
											.replace("%player%" , player.getName())
											.replace("%parkour%" , pp.getParkour().getName())
											.replace("%time%" , time));		
								}
								
								
								return;
							}

							player.sendMessage(Helper.formatString(
									plugin,
									plugin.getConfig()
											.getString("finish message")
											.replace("%time%", time).replace("%highscore%", s_highscore)));
						}
					};
					task.runTaskAsynchronously(plugin);

					// clear
					new BukkitRunnable() {
						public void run() {
							// TODO Auto-generated method stub
							player.playSound(player.getLocation(),
									Sound.BLOCK_NOTE_HARP, 1, 1);
							try {
								Thread.sleep(300);
							} catch (InterruptedException e1) {
							}
							player.playSound(player.getLocation(),
									Sound.BLOCK_NOTE_HARP, 1, (float) 1.5);
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
							}
							player.playSound(player.getLocation(),
									Sound.BLOCK_NOTE_HARP, 1, 2);
						}
					}.runTaskLaterAsynchronously(plugin, 1);
					plugin.playing.remove(player);
					return;
				}
				if (index == 0) {
					// So its a start
					pp.startTimer();
					pp.checkpoints.clear();
					pp.setLastIndex(0);
					pp.addCheckpoint(pp.getParkour().checkpoints.get(0));
					player.sendMessage(Helper.formatString(plugin, plugin
							.getConfig().getString("time reset")));
					player.playSound(player.getLocation(),
							Sound.BLOCK_ANVIL_LAND, (float) 0.1, (float) 2);
					return;
				}

				// Checkpoint...
				if (pp.checkpoints.contains(parkour.checkpoints.get(index)))
					return;
				if (index <= pp.getLastIndex())
					return;

				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING,
						(float) 0.5, 2);
				pp.setLastIndex(index);
				pp.addCheckpoint(parkour.checkpoints.get(index));
				player.sendMessage(Helper.formatString(plugin,
						plugin.getConfig().getString("checkpoint message")
								.replace("%number%", String.valueOf(index))));
				return;

			} else {
				if (index == 0) {
					if (!parkour.isOpen()) {
						player.sendMessage(Helper.formatString(plugin, plugin
								.getConfig().getString("parkour closed")));
						return;
					}

					if (!player.hasPermission("parkour.start."
							+ parkour.getID())) {
						player.sendMessage(Helper.formatString(plugin, plugin
								.getConfig().getString("no start permission")));
						return;
					}
					player.sendMessage(Helper.formatString(plugin, plugin
							.getConfig().getString("start message")));
					player.playSound(player.getLocation(),
							Sound.BLOCK_NOTE_HARP, 1, 3);
					PlayerParkour pp = new PlayerParkour(player);
					pp.setParkour(parkour);
					pp.addCheckpoint(parkour.checkpoints.get(index));
					pp.startTimer();
					plugin.playing.put(player, pp);

					return;
				} else if (index == parkour.checkpoints.size() - 1) {
					player.sendMessage(Helper.formatString(plugin, plugin
							.getConfig().getString("not started finish")));
					return;
				}

				player.sendMessage(Helper.formatString(plugin, plugin
						.getConfig().getString("not started checkpoint")));
				return;

			}
		}
	}

}

package com.martenm.parkour;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.martenm.parkour.commands.ParkourCommand;
import com.martenm.parkour.events.OnBlockBreak;
import com.martenm.parkour.events.OnBlockPlace;
import com.martenm.parkour.events.OnPlayerInteractEvent;
import com.martenm.parkour.helpers.Config;
import com.martenm.parkour.helpers.ParcourCreator;
import com.martenm.parkour.helpers.Parkour;
import com.martenm.parkour.helpers.ParkourBlock;
import com.martenm.parkour.helpers.PlayerParkour;
import com.martenm.parkour.helpers.highscores.LeaderHeadsParkour;

import net.md_5.bungee.api.ChatColor;

public class MainClass extends JavaPlugin {

	Logger log;
	public String cPrefix = "[" + "Parkour" + "] ";

	public boolean enabled;
	private boolean leaderheadsHook;
	public String prefix = "";

	public HashMap<Player, PlayerParkour> playing;

	public HashMap<Player, ParcourCreator> creating;

	public List<Parkour> parkours = new ArrayList<Parkour>();

	public Config ParkourFile;

	public void onEnable() {
		log = getServer().getLogger();
		log.log(Level.INFO, cPrefix + "Enabling parcour plugin...");

		// Setup variables;
		playing = new HashMap<Player, PlayerParkour>();
		creating = new HashMap<Player, ParcourCreator>();

		// region Register stuff
		registerEvents();
		log.log(Level.INFO, cPrefix + "Succesfully registered the events.");
		registerCommands();
		log.log(Level.INFO, cPrefix + "Succesfully registered the commmands.");
		registerConfig();
		log.log(Level.INFO, cPrefix + "Succesfully registered the config.");
		prefix = ChatColor.translateAlternateColorCodes('&', getConfig()
				.getString("prefix"));
		Plugin leaderheads = Bukkit.getPluginManager().getPlugin("LeaderHeads");
		if(leaderheads != null) {
		    setLeaderheadsHook(true);
		} else {
			setLeaderheadsHook(false);
		}
		enabled = true;
	}

	public boolean isLeaderheadsHook() {
		return leaderheadsHook;
	}

	private void setLeaderheadsHook(boolean leaderheadsHook) {
		this.leaderheadsHook = leaderheadsHook;
	}

	public void onDisable() {
		log.log(Level.WARNING, cPrefix + "Disabling the parcour plugin!");
		enabled = false;

		// Save parkours.
		saveParkours();
		// Safely clear the hashmaps
		for (PlayerParkour pp : playing.values()) {
			pp.stopTimer();
			pp.getPlayer()
					.sendMessage(
							ChatColor.RED
									+ "Parkour cancelled. Parcour plugin is disabling.");
		}
		playing.clear();
		//

	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new OnBlockPlace(this), this);
		pm.registerEvents(new OnBlockBreak(this), this);
		pm.registerEvents(new OnPlayerInteractEvent(this), this);
	}

	public void registerCommands() {
		getCommand("parkour").setExecutor(new ParkourCommand(this));

	}

	public void registerConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		ParkourFile = new Config(this, "Parkours");
		loadParkours();
	}

	public void saveParkours() {

		ParkourFile.set("parkours", null);

		for (int i = 0; i < parkours.size(); i++) {
			Parkour p = parkours.get(i);
			log.log(Level.WARNING, cPrefix + "Saving parcour: " + p.getName());
			ParkourFile.set("parkours." + i + ".id", p.getID());
			ParkourFile.set("parkours." + i + ".name", p.getName());
			ParkourFile.set("parkours." + i + ".custome name",
					p.getCustomeName());
			ParkourFile.set("parkours." + i + ".status", p.isOpen());

			// Get locations of checkpoints
			List<String> points = new ArrayList<String>();
			for (ParkourBlock b : p.checkpoints) {
				String world = b.block.getLocation().getWorld().getName();
				int x = (int) b.block.getLocation().getX();
				int y = (int) b.block.getLocation().getY();
				int z = (int) b.block.getLocation().getZ();
				points.add(world + " " + x + " " + y + " " + z);
			}
			ParkourFile.set("parkours." + i + ".checkpoints", points);

		}
		ParkourFile.save();
	}

	public void loadParkours() {
		log.log(Level.INFO, cPrefix + "Loading parkours...");
		try {
			for (String key : ParkourFile.getConfigurationSection("parkours")
					.getKeys(false)) {
				Parkour p = new Parkour(this);
				p.setID(ParkourFile.getString("parkours." + key + ".id"));
				p.setName(ParkourFile.getString("parkours." + key + ".name"));
				p.setCustomeName(ParkourFile.getString("parkours." + key
						+ ".custome name"));
				p.setStatus(Boolean.valueOf(ParkourFile.getString("parkours."
						+ key + ".status")));
				// Check if corrupt
				if (p.getName() == null || p.getID() == null) {
					log.log(Level.WARNING,
							cPrefix
									+ "A parkour has been deleted from save file. Name: "
									+ p.getName() + " Id: " + p.getID());
					ParkourFile.set("parkours." + key, null);
					continue;
				}
				List<String> points = new ArrayList<String>();
				points = ParkourFile.getStringList("parkours." + key
						+ ".checkpoints");

				for (String point : points) {
					try {

						String[] data = point.split(" ");

						World world = getServer().getWorld(data[0]);
						int x = Integer.parseInt(data[1]);
						int y = Integer.parseInt(data[2]);
						int z = Integer.parseInt(data[3]);
						Location loc = new Location(world, x, y, z);

						Block block = world.getBlockAt(loc);
						if (!block.getType().equals(Material.STONE_PLATE)
								&& !block.getType().equals(Material.WOOD_PLATE)
								&& !block.getType().equals(Material.GOLD_PLATE)
								&& !block.getType().equals(Material.IRON_PLATE)) {
							log.log(Level.WARNING,
									cPrefix
											+ "Pressure plate was not found. Deleting checkpoint from: ID: "
											+ p.getID() + " Name: "
											+ p.getName());
							continue;
						}

						ParkourBlock pb = new ParkourBlock();
						pb.block = block;

						Collection<Entity> entities = world.getNearbyEntities(
								loc, 1, 5, 1);
						List<ArmorStand> texts = new ArrayList<ArmorStand>();

						for (Entity e : entities) {
							if (e.getType().equals(EntityType.ARMOR_STAND)) {
								ArmorStand a = (ArmorStand) e;
								texts.add(a);
							}
						}
						pb.Texts = texts;

						// Add checkpoint!
						p.checkpoints.add(pb);
						if (!LeaderHeadsParkour.leaderBoards.containsKey(p.getName())){
							LeaderHeadsParkour.leaderBoards.put(p.getName(),new LeaderHeadsParkour(p.getName()));
						}
					} catch (Exception e) {
						// log.log(Level.WARNING, cPrefix +
						// "Something went wrong loading a checkpoint of the parkour. ID: "
						// + p.getID() + " Name: " + p.getName());
					}
				}
				parkours.add(p);
			}
		} catch (Exception e) {

		}
		ParkourFile.save();

	}

}

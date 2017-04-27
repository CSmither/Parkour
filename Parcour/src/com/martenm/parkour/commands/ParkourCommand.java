package com.martenm.parkour.commands;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.martenm.parkour.MainClass;
import com.martenm.parkour.helpers.Helper;
import com.martenm.parkour.helpers.ParcourCreator;
import com.martenm.parkour.helpers.Parkour;
import com.martenm.parkour.helpers.PlayerParkour;

public class ParkourCommand implements CommandExecutor {

	private MainClass plugin;

	public ParkourCommand(MainClass plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		// if (!label.equalsIgnoreCase("parkour") ||
		// !label.equalsIgnoreCase("pk")) {
		// return false;
		// }

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("help")) {
				sendHelp(sender);
				return true;
			} else if (args[0].equalsIgnoreCase("create")) {

				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED
							+ "This is a player only command!");
					return true;
				}
				if (!sender.hasPermission("parkour.create")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes(
							'&', plugin.getConfig().getString("no permission")));
					return true;
				}
				if (args.length < 3) {
					sender.sendMessage(plugin.prefix + ChatColor.RED
							+ "Use like this: /pk create <id> <name>");
					return true;
				}

				Player player = (Player) sender;

				if (plugin.creating.containsKey(player)) {
					sender.sendMessage(ChatColor.RED
							+ "You are already creating a parcour!");
					return true;
				}
				
				

				// SETUP PARCOUR CREATE MODE
				String name = "";
				for (int i = 2; i < args.length; i++) {
					if (i == 2)
						name += args[i];
					else
						name += " " + args[i];
				}

				String id = args[1];

				for(Parkour p : plugin.parkours){
					if(p.getID().equalsIgnoreCase(id)){
						player.sendMessage(plugin.prefix + ChatColor.RED + "There already exists a parkour with that ID.");
						return true;
					}
				}
				
				sender.sendMessage(plugin.prefix + ChatColor.GREEN
						+ "Creating a new parkour.");
				sender.sendMessage(plugin.prefix + ChatColor.GREEN + "ID: "
						+ ChatColor.GOLD + id + ChatColor.GREEN
						+ " and the name: " + ChatColor.GOLD + name
						+ ChatColor.GREEN + ".");
				sender.sendMessage(plugin.prefix
						+ ChatColor.YELLOW
						+ "Use pressure plates to create a start, checkpoint(s) and a finish.");
				sender.sendMessage(plugin.prefix + ChatColor.YELLOW
						+ "Use the command " + ChatColor.GREEN + "/pk open "
						+ ChatColor.YELLOW
						+ "or " + ChatColor.GREEN + "/pk edit "
						+ ChatColor.YELLOW
						+ "to stop editing the parcour. ");
				ParcourCreator pc = new ParcourCreator(player, id, name);
				Parkour p = new Parkour(plugin);
				p.close();
				p.setID(id);
				p.setName(name);
				pc.setParkour(p);
				plugin.parkours.add(p);
				plugin.creating.put(player, pc);
				return true;
			} else if (args[0].equalsIgnoreCase("open")) {

				if (!sender.hasPermission("parkour.open")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes(
							'&', plugin.getConfig().getString("no permission")));
					return true;
				}

				if (args.length > 1) {
					for (Parkour p : plugin.parkours) {
						if (!p.getID().equalsIgnoreCase(args[1]))
							continue;
						if (p.isOpen()) {
							sender.sendMessage(plugin.prefix + ChatColor.RED
									+ "Parcour is already opened.");
							return true;
						} else {
							p.open();
							if (p.checkpoints.size() < 2) {
								sender.sendMessage(plugin.prefix
										+ ChatColor.RED
										+ "Could not open parcour! There is no start / finish.");
							}
							sender.sendMessage(plugin.prefix + ChatColor.GREEN
									+ "Succesfully opened the parcour.");
							return true;
						}
					}
					sender.sendMessage(plugin.prefix
							+ ChatColor.RED
							+ "Could not find a parkour with that id.");
					return true;
				}

				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (!plugin.creating.containsKey(player)) {
						player.sendMessage(plugin.prefix
								+ ChatColor.RED
								+ "You are not editing a parcour. Please use /pk open <id>");
						return true;
					}

					ParcourCreator p = plugin.creating.get(player);
					if (p.getParkour().checkpoints.size() < 2) {
						player.sendMessage(plugin.prefix
								+ ChatColor.RED
								+ "Could not open parcour! There is no start / finish.");
					} else {
						p.getParkour().open();
						player.sendMessage(plugin.prefix + ChatColor.GREEN
								+ "Succesfully opened the parkour");
					}

					player.sendMessage(plugin.prefix + ChatColor.RED
							+ "You have been put out of editing mode.");
					plugin.creating.remove(player);
					return true;

				}
			}

			else if (args[0].equalsIgnoreCase("edit")) {
				if (!sender.hasPermission("parkour.edit")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes(
							'&', plugin.getConfig().getString("no permission")));
					return true;
				}

				if (!(sender instanceof Player)) {
					sender.sendMessage(plugin.prefix + ChatColor.RED
							+ "This is a player only command.");
					return true;
				}
				Player player = (Player) sender;
				if (args.length < 2) {
					if (plugin.creating.containsKey(player)) {
						player.sendMessage(plugin.prefix + ChatColor.YELLOW
								+ "You stopped editing the parkour.");
						plugin.creating.remove(player);
						return true;
					}
					sender.sendMessage(plugin.prefix + ChatColor.RED
							+ "Use like this: /pk edit <id>");
					return true;
				}

				if (plugin.creating.containsKey(player)) {
					sender.sendMessage(plugin.prefix + ChatColor.RED
							+ "You are already editing a parkour.");
					return true;
				}

				String id = args[1];
				for (Parkour parkour : plugin.parkours) {
					if (!parkour.getID().equalsIgnoreCase(id))
						continue;

					ParcourCreator pc = new ParcourCreator(player,
							parkour.getID(), parkour.getName());
					pc.setParkour(parkour);
					plugin.creating.put(player, pc);
					parkour.close();
					player.sendMessage(plugin.prefix + ChatColor.GREEN
							+ "You can now edit the parkour!");
					return true;
				}
				player.sendMessage(plugin.prefix + ChatColor.RED
						+ "No parkour found with that ID.");
				return true;
			}

			else if (args[0].equalsIgnoreCase("close")) {
				if (!sender.hasPermission("parkour.close")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes(
							'&', plugin.getConfig().getString("no permission")));
					return true;
				}

				if (args.length < 2) {
					sender.sendMessage(plugin.prefix + ChatColor.RED
							+ "Use like this: /pk close <id>");
					return true;
				}

				for (Parkour p : plugin.parkours) {
					if (!p.getID().equalsIgnoreCase(args[1]))
						continue;
					if (!p.isOpen()) {
						sender.sendMessage(plugin.prefix + ChatColor.RED
								+ "Parcour is already closed.");
						return true;
					} else {
						p.close();
						sender.sendMessage(plugin.prefix + ChatColor.GREEN
								+ "Succesfully closed the parcour.");
						return true;
					}
				}

			}

			else if (args[0].equalsIgnoreCase("remove")) {
				if (!sender.hasPermission("parkour.delete")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes(
							'&', plugin.getConfig().getString("no permission")));
					return true;
				}

				if (args.length < 3) {
					sender.sendMessage(plugin.prefix + ChatColor.RED
							+ "Use like this: /pk remove <id> <name>");
					return true;
				}

				Parkour p = null;
				String name = "";
				for (int i = 2; i < args.length; i++) {
					if (i == 2)
						name += args[i];
					else
						name += " " + args[i];
				}

				for (Parkour parkour : plugin.parkours) {
					if (!parkour.getID().equalsIgnoreCase(args[1])) {
						sender.sendMessage(parkour.getID() + " != " + args[1]);
						continue;
					}
					if (!parkour.getName().equalsIgnoreCase(name)) {
						sender.sendMessage(parkour.getName() + "!=" + name);
						continue;
					}
					p = parkour;
					break;
				}
				if (p == null) {
					sender.sendMessage(plugin.prefix + ChatColor.RED
							+ "That parcour does not exist.");
					return true;
				}
				p.close();
				p.delete(true);
				plugin.parkours.remove(p);
				for(ParcourCreator pc : plugin.creating.values()){
					if(pc.getParkour() == p){
						pc.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "Parkour has been removed. You have been put out of editing mode.");
						plugin.creating.remove(pc);
					}
				}
				sender.sendMessage(plugin.prefix + ChatColor.GREEN
						+ "Succesfully removed the parkour!");
				return true;
			}

			else if (args[0].equalsIgnoreCase("list")) {
				if (!sender.hasPermission("parkour.list")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes(
							'&', plugin.getConfig().getString("no permission")));
					return true;
				}
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
						ChatColor.DARK_GRAY + "╬" + line + "┤ "
								+ ChatColor.YELLOW + ChatColor.BOLD
								+ "Parkour list" + ChatColor.DARK_GRAY + " ├"
								+ line + "╬"));
				if(plugin.parkours.size() == 0){
					sender.sendMessage(ChatColor.RED + " This server does not have parkours.");
				}
				for (int i = 0; i < plugin.parkours.size(); i++) {
					Parkour p = plugin.parkours.get(i);
					String status = "";
					if(p.isOpen()) status = ChatColor.GREEN + "Open";
					else status = ChatColor.RED + "Closed";
					
					sender.sendMessage(ChatColor.DARK_GRAY + " [" + ChatColor.YELLOW + i + 
							ChatColor.DARK_GRAY + "] > "
							+ ChatColor.GRAY + "ID: " + ChatColor.GOLD + p.getID() + " "
							+ ChatColor.GRAY + "Name: " + ChatColor.GOLD + p.getName()
							+ ChatColor.GRAY + " Status: " + status
							+ ChatColor.GRAY + " Checkpoints: " + ChatColor.GOLD + p.checkpoints.size());
				}
				return true;

			}
			
			else if(args[0].equalsIgnoreCase("checkpoint")){
				if (!sender.hasPermission("parkour.delete")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes(
							'&', plugin.getConfig().getString("no permission")));
					return true;
				}
				
				if(!(sender instanceof Player)){
					sender.sendMessage(plugin.prefix + ChatColor.RED + "This is a player only command.");
					return true;
				}
				
				Player player = (Player) sender;
				if(!plugin.playing.containsKey(player)){
					sender.sendMessage(Helper.formatString(plugin, plugin.getConfig().getString("not playing")));
					return true;
				}
				PlayerParkour pp = plugin.playing.get(player);
				Location temp = pp.getCurrentCheckpoint();
				player.teleport(temp.add(0.5, 0, 0.5));
				return true;
			}
			else if (args[0].equalsIgnoreCase("quit")) {
				if(!(sender instanceof Player)){
					sender.sendMessage(ChatColor.RED + "This is a player only command.");
					return true;
				}
				Player player = (Player) sender;
				if(plugin.playing.containsKey(player)){
					plugin.playing.remove(player);
					player.sendMessage(Helper.formatString(plugin, plugin.getConfig().getString("quit")));
				}
			}

			sender.sendMessage(plugin.prefix + ChatColor.RED
					+ "Invalid argument. Use /pk help for help.");
			return true;
		}
		sendHelp(sender);
		return true;
	}

	//
	//
	//
	//
	//
	private String formatCommand(String command, String Desc) {
		return ChatColor.translateAlternateColorCodes('&', "&e   " + command
				+ " &7- &f" + Desc);
	}

	private final String line = "───────────";

	private void sendHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				ChatColor.DARK_GRAY + "╬" + line + "┤ " + ChatColor.YELLOW
						+ ChatColor.BOLD + "Parkour" + ChatColor.DARK_GRAY
						+ " ├" + line + "╬"));
		sender.sendMessage(formatCommand("/pk help",
				"Shows this neat help menu."));
		if (sender.hasPermission("parkour.create"))
			sender.sendMessage(formatCommand("/pk create",
					"Create a new parkour."));
		if (sender.hasPermission("parkour.open"))
			sender.sendMessage(formatCommand("/pk open <id>",
					"Opens the parcour for usage."));
		if (sender.hasPermission("parkour.close"))
			sender.sendMessage(formatCommand("/pk close <id>",
					"Closes the given parkour."));
		if (sender.hasPermission("parkour.edit"))
			sender.sendMessage(formatCommand("/pk edit <id>",
					"Edit a existing parkour."));
		if (sender.hasPermission("parkour.open"))
			sender.sendMessage(formatCommand("/pk remove <id> <name>",
					"Removes a parkour."));
		if (sender.hasPermission("parkour.list"))
			sender.sendMessage(formatCommand("/pk list",
					"Shows all parkours in a list."));

		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
				ChatColor.DARK_GRAY + "╬" + line + "┤ " + ChatColor.YELLOW
						+ ChatColor.BOLD + "-=-=-=-" + ChatColor.DARK_GRAY
						+ " ├" + line + "╬"));

	}
}

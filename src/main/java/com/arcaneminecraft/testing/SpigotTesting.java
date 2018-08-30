package com.arcaneminecraft.testing;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;

public class SpigotTesting extends JavaPlugin {
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("f") & sender.hasPermission("arcane.f")) {
			if (sender instanceof Player)
				return firework((Player) sender);
			else
				sender.sendMessage("Must be run as a player");
				return true;
		}

		if (cmd.getName().equalsIgnoreCase("entitiesnear")) {
			return entitiesNear(sender, args);
		}
		return false;
	}

	private boolean firework(Player p1) {
		Firework fw = p1.getWorld().spawn(p1.getLocation(), Firework.class);
		FireworkEffect effect = FireworkEffect.builder().trail(true).flicker(false).withColor(Color.RED)
				.with(FireworkEffect.Type.BURST).build();
		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.clearEffects();
		fwm.addEffects(effect);

		try {
			fwm.getClass().getDeclaredField("power");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();

		}

		fw.setFireworkMeta(fwm);
		return true;
	}

	private boolean entitiesNear(CommandSender sender, String[] args) {
		Player[] find;
		boolean worlds = false;
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Usage: /entitiesnear <radius> [-all|player ...] OR /entitiesnear <-world>");
			return true;
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("-world") || args[0].equalsIgnoreCase("-worlds")) {
				sender.sendMessage(ENTITIES_HEADER);
				return entitiesNearWorld(sender);
			} else if (sender instanceof Player) {
				find = new Player[]{(Player) sender};
			} else {
				sender.sendMessage("Must specify a player or use -all option to specify all players");
				return true;
			}
		} else if (args[1].equalsIgnoreCase("-all")) {
			Collection<? extends Player> pAll = getServer().getOnlinePlayers();
			find = pAll.toArray(new Player[0]);
			worlds = true;
		} else {
			find = new Player[args.length - 1];
			for (int i = 1; i < args.length; i++) {
				Player temp = getServer().getPlayer(args[i]);
				if (temp == null) {
					sender.sendMessage(ChatColor.RED + "Player " + args[1] + " does not exist");
					return true;
				}
				find[i-1] = temp;
			}
		}

		double r;
		try {
			r = Double.parseDouble(args[0]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not a (decimal) number.");
			return true;
		}

		if (r > 256) {
			sender.sendMessage(ChatColor.RED + "Radius " + r + " is too big, must be less than 256.");
			return true;
		}

		sender.sendMessage(ENTITIES_HEADER);
		int i = 0;
		while (i < find.length) {
			Player p = find[i];
			getServer().getScheduler().runTaskLater(this, () -> {
				List<Entity> list = p.getNearbyEntities(r, r, r);
				getServer().getScheduler().runTaskAsynchronously(this, () -> {
					sender.sendMessage(entitiesDetails(p.getName(), list));
				});
			}, ++i);
		}
		if (worlds)
			getServer().getScheduler().runTaskLater(this, () -> entitiesNearWorld(sender), ++i);

		return true;
	}

	private boolean entitiesNearWorld(CommandSender sender) {
		List<World> find = getServer().getWorlds();
		int i = 0;
		while (i < find.size()) {
			World w = find.get(i);
			getServer().getScheduler().runTaskLater(this, () -> {
				List<Entity> list = w.getEntities();
				sender.sendMessage(entitiesDetails(w.getName(), list));
			}, ++i);
		}
		return true;
	}

	private String entitiesDetails(String name, List<Entity> list) {
		int hostile = 0, animals = 0, water = 0, golem = 0, npc = 0, other;

		for (Entity e : list) {
			if (e instanceof Monster || e instanceof Slime) {
				hostile++;
			} else if (e instanceof Animals) {
				animals++;
			} else if (e instanceof WaterMob) {
				water++;
			} else if (e instanceof Golem) {
				golem++;
			} else if (e instanceof NPC) {
				npc++;
			}
		}
		other = list.size() - hostile - animals - water - golem - npc;

		return String.format(ENTITIES_FORMAT, name, list.size(), hostile, animals, water, golem, npc, other);
	}

	private static final String ENTITIES_FORMAT = ChatColor.GRAY + "%s: " +
			ChatColor.WHITE + "%d" + ChatColor.GRAY + " = " +
			ChatColor.RED + "%d" + ChatColor.GRAY + " + " +
			ChatColor.YELLOW + "%d" + ChatColor.GRAY + " + " +
			ChatColor.BLUE + "%d" + ChatColor.GRAY + " + " +
			ChatColor.LIGHT_PURPLE + "%d" + ChatColor.GRAY + " + " +
			ChatColor.GREEN + "%d" + ChatColor.GRAY + " + %d";
	private static final String ENTITIES_HEADER = ChatColor.DARK_GREEN + "-- Total Entities = " +
			ChatColor.RED + "Hostile" + ChatColor.DARK_GREEN + "+" +
			ChatColor.YELLOW + "Animals" + ChatColor.DARK_GREEN + "+" +
			ChatColor.BLUE + "Water" + ChatColor.DARK_GREEN + "+" +
			ChatColor.LIGHT_PURPLE + "Golem" + ChatColor.DARK_GREEN + "+" +
			ChatColor.GREEN + "NPC" + ChatColor.DARK_GREEN + "+Other --";
}

package com.arcaneminecraft.testing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotTesting extends JavaPlugin {
	private ItemMeta elytraShulker;
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Only command possible: Christmas
		if (!(sender instanceof Player)) {
			sender.sendMessage("Ho, ho, ho, you're not a player!  Come in the game first!");
			return true;
		}
		Player p = (Player)sender;
		
		// test command
		if (command.getName().equalsIgnoreCase("nbt")) {
			ItemStack i = p.getInventory().getItemInMainHand();
			Map<String,Object> ser = i.serialize();
			getConfig().set(i.getData().getItemType().toString(), ser);
			saveConfig();
			p.sendMessage("Serial Killer");
			return true;
		}
		
		givePresent(p);
		return true;
	}
	
	/**
	 * Gives the present to the player.
	 */
	private void givePresent(Player p) {
		ItemStack box = new ItemStack(Material.WHITE_SHULKER_BOX);
		//box.setItemMeta(elytraShulker.clone());
		box.getItemMeta().setDisplayName(p.getName() + "'s Present Box");
		List<String> lore = new ArrayList<String>();
		lore.add("Christmas 2016 Special Edition");
		box.getItemMeta().setLore(lore);
		p.getInventory().addItem(box);
	}
}

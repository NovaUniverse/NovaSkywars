package net.novauniverse.games.skywars.loottable.islandloottable;

import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.commons.utils.RandomGenerator;

@Deprecated
public class SkywarsLootEntry {
	private ItemStack item;
	private int minAmount;
	private int maxAmount;
	private int chance;
	
	private ArrayList<SkywarsLootEntry> extraItems;

	public SkywarsLootEntry(ItemStack item, int chance, int minAmount, int maxAmount, ArrayList<SkywarsLootEntry> extraItems) {
		this.item = item;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.extraItems = extraItems;
		this.chance = chance;
	}

	public ItemStack generateItem() {
		return this.generateItem(getAmount(minAmount, maxAmount));
	}

	public ItemStack generateItem(int amount) {
		ItemStack result = item.clone();

		result.setAmount(amount);
		
		return result;
	}

	public ItemStack getItem() {
		return item;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	private int getAmount(int min, int max) {
		if (min == max) {
			return min;
		}
		return RandomGenerator.generate(min, max);
		//return min + random.nextInt((max + 1) - min);
	}
	
	public boolean hasExtraItems() {
		return extraItems != null;
	}
	
	public ArrayList<SkywarsLootEntry> getExtraItems() {
		return extraItems;
	}
	
	public int getChance() {
		return chance;
	}
}
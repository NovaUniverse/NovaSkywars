package net.novauniverse.games.skywars.loottable.islandloottable;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.commons.utils.RandomGenerator;

@Deprecated
public class SkywarsLootGroup {
	private int minAmount;
	private int maxAmount;

	private ArrayList<SkywarsLootEntry> items;

	public SkywarsLootGroup(int minAmount, int maxAmount, ArrayList<SkywarsLootEntry> items) {
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;

		this.items = items;
	}

	public ArrayList<ItemStack> generate() {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		int amount = RandomGenerator.generate(minAmount, maxAmount);

		if (items.size() > 0) {
			for (int i = 0; i < amount; i++) {
				SkywarsLootEntry lootEntry = items.get(new Random().nextInt(items.size()));

				result.add(lootEntry.generateItem());
				if (lootEntry.hasExtraItems()) {
					result.addAll(getExtraItems(lootEntry));
				}
			}
		}

		return result;
	}

	private ArrayList<ItemStack> getExtraItems(SkywarsLootEntry lootEntry) {
		ArrayList<ItemStack> result = new ArrayList<ItemStack>();
		if (lootEntry.hasExtraItems()) {
			for (SkywarsLootEntry lootEntry2 : lootEntry.getExtraItems()) {
				if (lootEntry2.hasExtraItems()) {
					ArrayList<ItemStack> extra = this.getExtraItems(lootEntry2);
					result.addAll(extra);
				}
				result.add(lootEntry2.generateItem());
			}
		}

		return result;
	}
}
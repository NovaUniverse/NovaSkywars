package net.novauniverse.games.skywars.loottable.islandloottable;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONArray;
import org.json.JSONObject;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.loottable.LootTable;
import net.zeeraa.novacore.spigot.loottable.LootTableLoader;

@Deprecated
public class SkywarsLootLoader implements LootTableLoader {

	private static PotionEffect readPotionEffect(JSONObject potion) {
		PotionEffectType type = PotionEffectType.getByName(potion.getString("type"));
		int duration = potion.getInt("duration");

		int amplifier = 0;
		if (potion.has("amplifier")) {
			amplifier = potion.getInt("amplifier");
		}

		boolean ambient = false;
		if (potion.has("ambient")) {
			ambient = potion.getBoolean("ambient");
		}

		boolean particles = true;
		if (potion.has("particles")) {
			particles = potion.getBoolean("particles");
		}

		return new PotionEffect(type, duration, amplifier, ambient, particles);
	}

	private static SkywarsLootEntry readLootEntry(JSONObject itemJson) {
		ItemStack item;

		Material material = Material.getMaterial(itemJson.getString("material"));
		if(material == null) {
			Log.fatal("No material found with the name " + itemJson.getString("material"));
		}
		
		if (itemJson.has("data")) {
			short itemData = (short) itemJson.getInt("data");
			item = new ItemStack(material, 1, itemData);
		} else {
			item = new ItemStack(material, 1);
		}

		if (itemJson.has("display_name")) {
			String displayName = itemJson.getString("display_name");

			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName(displayName);

			item.setItemMeta(meta);
		}

		int minAmount = 1;
		int maxAmount = 1;

		if (itemJson.has("potion_data")) {
			PotionMeta meta = (PotionMeta) item.getItemMeta();

			JSONObject potionData = itemJson.getJSONObject("potion_data");

			if (potionData.has("main_effect")) {
				JSONObject mainEffect = potionData.getJSONObject("main_effect");
				PotionEffectType type = PotionEffectType.getByName(mainEffect.getString("type"));
				meta.setMainEffect(type);
			}

			if (potionData.has("custom_effects")) {
				JSONArray customEffects = potionData.getJSONArray("custom_effects");
				for (int i = 0; i < customEffects.length(); i++) {
					meta.addCustomEffect(readPotionEffect(customEffects.getJSONObject(i)), true);
				}
			}
			item.setItemMeta(meta);
		}

		if (itemJson.has("amount")) {
			minAmount = itemJson.getInt("amount");
			maxAmount = minAmount;
		} else {
			if (itemJson.has("min_amount")) {
				minAmount = itemJson.getInt("min_amount");
			}

			if (itemJson.has("max_amount")) {
				maxAmount = itemJson.getInt("max_amount");
				if (minAmount > maxAmount) {
					maxAmount = minAmount;
				}
			} else {
				maxAmount = minAmount;
			}
		}

		if (itemJson.has("enchantments")) {
			JSONObject enchantments = itemJson.getJSONObject("enchantments");

			for (String enchant : enchantments.keySet()) {
				int level = enchantments.getInt(enchant);

				item.addUnsafeEnchantment(Enchantment.getByName(enchant), level);
			}
		}

		int chance = 1;

		if (itemJson.has("chance")) {
			chance = itemJson.getInt("chance");
		}

		if (itemJson.has("display_name")) {
			String name = itemJson.getString("display_name");
			ItemMeta meta = item.getItemMeta();

			meta.setDisplayName(name);

			item.setItemMeta(meta);
		}

		ArrayList<SkywarsLootEntry> extraItems = null;

		if (itemJson.has("extra_items")) {
			extraItems = new ArrayList<SkywarsLootEntry>();
			JSONArray extraItemsJson = itemJson.getJSONArray("extra_items");

			for (int i = 0; i < extraItemsJson.length(); i++) {
				JSONObject extraItem = extraItemsJson.getJSONObject(i);

				extraItems.add(readLootEntry(extraItem));
			}
		}

		SkywarsLootEntry entry = new SkywarsLootEntry(item, chance, minAmount, maxAmount, extraItems);

		return entry;
	}

	@Override
	public LootTable read(JSONObject json) {
		try {
			Log.info("reading loot table named " + json.getString("name"));

			String lootTableName = json.getString("name");
			String lootTableDisplayName;

			if (json.has("display_name")) {
				lootTableDisplayName = json.getString("display_name");
			} else {
				lootTableDisplayName = lootTableName;
			}

			JSONObject groups = json.getJSONObject("groups");

			ArrayList<SkywarsLootGroup> lootGroups = new ArrayList<SkywarsLootGroup>();

			for (String key : groups.keySet()) {
				Log.trace("loot group: " + key);
				JSONObject group = groups.getJSONObject(key);

				ArrayList<SkywarsLootEntry> entries = new ArrayList<SkywarsLootEntry>();

				JSONArray items = group.getJSONArray("items");

				for (int i = 0; i < items.length(); i++) {
					try {
						SkywarsLootEntry entry = readLootEntry(items.getJSONObject(i));

						for (int j = 0; j < entry.getChance(); j++) {
							entries.add(entry);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Log.error("Failed to read a item from the loot table " + json.getString("name") + " JSON: \n" + items.getJSONObject(i).toString(4));
					}
				}

				Collections.shuffle(entries);

				SkywarsLootGroup lootGroup = new SkywarsLootGroup(group.getInt("min_items"), group.getInt("max_items"), entries);

				lootGroups.add(lootGroup);
			}

			SkywarsIslandLootTable lootTable = new SkywarsIslandLootTable(lootTableName, lootTableDisplayName, lootGroups);

			return (LootTable) lootTable;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getLoaderName() {
		return "mcf.skywars_island";
	}
}
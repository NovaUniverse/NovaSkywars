package net.novauniverse.games.skywars.loottable.hardcodedskywarsloottable.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.commons.utils.RandomGenerator;
import net.zeeraa.novacore.spigot.loottable.LootTable;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class HardCodedSkywarsLootTableV1 extends LootTable {

	public HardCodedSkywarsLootTableV1(String name, int minItems, int maxItems) {
		super(name, minItems, maxItems);
	}

	@Override
	public List<ItemStack> generateLoot(Random random, int count) {
		List<ItemStack> result = new ArrayList<ItemStack>();

		ItemStack food = new ItemStack(random.nextBoolean() ? Material.COOKED_BEEF : Material.COOKED_CHICKEN);
		food.setAmount(random.nextBoolean() ? 8 : 16);

		result.add(food);
		
		/* The chest will first generate either 8-16 stone blocks or wood planks. */
		ItemStack block = new ItemStack(random.nextBoolean() ? Material.STONE : Material.WOOD);
		block.setAmount(random.nextBoolean() ? 16 : 32);

		result.add(block);

		/*
		 * Than it has 66% of adding an iron pick with lvl 1 enchant and 33% of a normal
		 * diamond pickaxe - it will pick only one of them.
		 */
		ItemStack pickaxe;

		int pickaxeRandom = RandomGenerator.generate(1, 3, random);

		if (pickaxeRandom <= 2) {
			pickaxe = new ItemStack(Material.IRON_PICKAXE);
			pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);
		} else {
			pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
		}

		result.add(pickaxe);

		/*
		 * Than it will decide wich sword to add: stone sword ench lvl 1, iron sword,
		 * diamond sword or diamond sword ench lvl 1 - the chances go down from the
		 * lowest grade to the highest.
		 */

		List<ItemStack> swords = new ArrayList<ItemStack>();


		for (int i = 0; i < 7; i++) {
			swords.add(new ItemBuilder(Material.IRON_SWORD).build());
		}

		for (int i = 0; i < 2; i++) {
			swords.add(new ItemBuilder(Material.DIAMOND_SWORD).build());
		}

		for (int i = 0; i < 1; i++) {
			swords.add(new ItemBuilder(Material.DIAMOND_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 1).build());
		}

		result.add(swords.get(random.nextInt(swords.size())));

		/*
		 * after that it will add either 8-16 snowballs or eggs, and than it will choose
		 * one of the following a fishing rod, a water bucket, or a lava bucket -
		 * chances go down from fishing rod to lava bucket.
		 */

		/* egg or snowball */
		ItemStack projectile = new ItemStack(random.nextBoolean() ? Material.SNOW_BALL : Material.EGG);
		projectile.setAmount(random.nextBoolean() ? 8 : 16);

		result.add(projectile);

		/* rod, water or lava */
		List<ItemStack> utilityItem1 = new ArrayList<ItemStack>();

		for (int i = 0; i < 3; i++) {
			utilityItem1.add(new ItemBuilder(Material.FISHING_ROD).build());
		}

		for (int i = 0; i < 2; i++) {
			utilityItem1.add(new ItemBuilder(Material.WATER_BUCKET).build());
		}

		for (int i = 0; i < 1; i++) {
			utilityItem1.add(new ItemBuilder(Material.LAVA_BUCKET).build());
		}

		result.add(utilityItem1.get(random.nextInt(utilityItem1.size())));

		/*
		 * Finally, it will pick up an armor type each armor part has about 70% of being
		 * chosen and about 30% of not being chosen if the armor part was chosen than
		 * would pick a random material for it: iron, diamond, or leather. And that's
		 * it!
		 */

		for (HardCodedSkywarsLootTableV1ArmorParts part : HardCodedSkywarsLootTableV1ArmorParts.values()) {
			int rand = RandomGenerator.generate(1, 10, random);

			if (rand <= 7) {
				Material material;
				
				int rand2 = RandomGenerator.generate(1, 10, random);
				if(rand2 < 8) {
					material = part.getIronVersion();
				} else {
					material = part.getDiamondVersion();
				}
				
				

				result.add(new ItemBuilder(material).build());
			}
		}

		/* Add a special item */
		if (random.nextBoolean()) {
			ItemStack special;
			switch (RandomGenerator.generate(1, 8, random)) {
			case 1:
				special = new ItemBuilder(Material.GOLDEN_APPLE).setAmount(1).build();
				break;
			case 2:
				// Regeneration Splash (0:33) Potion
				special = new ItemBuilder(Material.POTION).setDurability((short) 16385).setAmount(1).build();
				break;
			case 3:
				// Swiftness Splash (2:15) Potion
				special = new ItemBuilder(Material.POTION).setDurability((short) 16386).setAmount(1).build();
				break;
			case 4:
				// Healing Splash Potion
				special = new ItemBuilder(Material.POTION).setDurability((short) 16389).setAmount(1).build();
				break;
			case 5:
				// Harming Splash Potion
				special = new ItemBuilder(Material.POTION).setDurability((short) 16396).setAmount(1).build();
				break;
			case 6:
				// Regeneration II Splash (0:16) Potion
				special = new ItemBuilder(Material.POTION).setDurability((short) 16417).setAmount(1).build();
				break;
			case 7:
				// Swiftness II Splash (1:07) Potion
				special = new ItemBuilder(Material.POTION).setDurability((short) 16418).setAmount(1).build();
				break;
			case 8:
				// Fire Resistance Splash (6:00) Potion
				special = new ItemBuilder(Material.POTION).setDurability((short) 16451).setAmount(1).build();
				break;

			default:
				special = new ItemBuilder(Material.GOLDEN_APPLE).setName("Special golden apple").addLore("If you are reading this").addLore("it means you would have").addLore("received some random item").addLore("but zeeraa messed up and you").addLore("got this epic placeholder item instead").addEnchant(Enchantment.DIG_SPEED, 10, true).build();
				break;
			}

			if (special != null) {
				result.add(special);
			}
		}
		
		/* Cobweb */
		if (RandomGenerator.generate(1, 2, random) == 1) {
			int amount = random.nextBoolean() ? 4 : 8;
			
			ItemStack cobweb = new ItemBuilder(Material.WEB).setAmount(amount).build();
			
			result.add(cobweb);
		}

		return result;
	}
}
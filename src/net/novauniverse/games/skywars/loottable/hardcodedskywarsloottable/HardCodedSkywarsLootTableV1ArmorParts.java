package net.novauniverse.games.skywars.loottable.hardcodedskywarsloottable;

import org.bukkit.Material;

public enum HardCodedSkywarsLootTableV1ArmorParts {
	/* Helmet */
	HELMET(Material.IRON_HELMET, Material.DIAMOND_HELMET),
	/* Chestplate */
	CHESTPLATE(Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE),
	/* Leggings */
	LEGGINGS(Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS),
	/* Boots */
	BOOTS(Material.IRON_BOOTS, Material.DIAMOND_BOOTS);

	private Material ironVersion;
	private Material diamondVersion;

	private HardCodedSkywarsLootTableV1ArmorParts(Material ironVersion, Material diamondVersion) {
		this.ironVersion = ironVersion;
		this.diamondVersion = diamondVersion;
	}

	public Material getDiamondVersion() {
		return diamondVersion;
	}
	
	public Material getIronVersion() {
		return ironVersion;
	}
}
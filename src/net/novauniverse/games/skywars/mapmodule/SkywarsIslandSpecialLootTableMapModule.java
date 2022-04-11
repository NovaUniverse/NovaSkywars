package net.novauniverse.games.skywars.mapmodule;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.json.JSONObject;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.Game;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModule;
import net.zeeraa.novacore.spigot.loottable.LootTable;
import net.zeeraa.novacore.spigot.module.modules.chestloot.ChestType;
import net.zeeraa.novacore.spigot.module.modules.chestloot.events.ChestFillEvent;

public class SkywarsIslandSpecialLootTableMapModule extends MapModule implements Listener {
	private int islandRadius;
	private LootTable islandLootTable;

	public SkywarsIslandSpecialLootTableMapModule(JSONObject json) {
		super(json);

		this.islandRadius = json.getInt("island_radius");
		this.islandLootTable = NovaCore.getInstance().getLootTableManager().getLootTable(json.getString("loot_table"));

		if (islandLootTable == null) {
			Log.fatal("Island loot", "Could not find loot table named " + json.getString("loot_table"));
		}
	}

	@Override
	public void onGameStart(Game game) {
		Bukkit.getServer().getPluginManager().registerEvents(this, NovaCore.getInstance());
	}

	@Override
	public void onGameEnd(Game game) {
		HandlerList.unregisterAll(this);
	}

	public int getIslandRadius() {
		return islandRadius;
	}

	public LootTable getIslandLootTable() {
		return islandLootTable;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onChestFill(ChestFillEvent e) {
		// If this is not called check if the original loot table exits
		if (e.getChestType() == ChestType.CHEST) {
			Location location = e.getLocation();

			MapGame game = (MapGame) GameManager.getInstance().getActiveGame();

			if (game.getActiveMap().getWorld() == location.getWorld()) {
				for (Location spawnLocation : game.getActiveMap().getStarterLocations()) {
					Location l2 = spawnLocation.clone();

					l2.setY(location.getY());

					// Log.trace("Distance: " + location.distance(l2) + " radius: " + islandRadius);
					if (location.distance(l2) <= islandRadius) {
						Log.trace("Replacing loot table for chest on island with " + getIslandLootTable().getName());
						e.setLootTable(getIslandLootTable());
						break;
					}
				}
			}
		}
	}
}
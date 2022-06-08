package net.novauniverse.games.skywars;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.games.skywars.game.Skywars;
import net.novauniverse.games.skywars.loottable.hardcodedskywarsloottable.v1.HardCodedSkywarsLootTableV1;
import net.novauniverse.games.skywars.mapmodule.SkywarsIslandSpecialLootTableMapModule;
import net.novauniverse.games.skywars.modules.RegenOnKill;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.events.VersionIndependentPlayerAchievementAwardedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodule.MapModuleManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.mapselector.selectors.guivoteselector.GUIMapVote;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.module.modules.compass.event.CompassTrackingEvent;

public class NovaSkywars extends JavaPlugin implements Listener {
	private static NovaSkywars instance;

	public static NovaSkywars getInstance() {
		return instance;
	}

	private Skywars game;

	private boolean allowReconnect;
	private boolean combatTagging;
	private int reconnectTime;

	public boolean isAllowReconnect() {
		return allowReconnect;
	}

	public boolean isCombatTagging() {
		return combatTagging;
	}

	public int getReconnectTime() {
		return reconnectTime;
	}

	@Override
	public void onEnable() {
		NovaSkywars.instance = this;

		saveDefaultConfig();

		allowReconnect = getConfig().getBoolean("allow_reconnect");
		combatTagging = getConfig().getBoolean("combat_tagging");
		reconnectTime = getConfig().getInt("player_elimination_delay");

		GameManager.getInstance().setUseCombatTagging(combatTagging);

		// Create files and folders
		File mapFolder = new File(this.getDataFolder().getPath() + File.separator + "Maps");
		File worldFolder = new File(this.getDataFolder().getPath() + File.separator + "Worlds");
		File lootTableFolder = new File(this.getDataFolder().getPath() + File.separator + "LootTables");

		File mapOverrides = new File(this.getDataFolder().getPath() + File.separator + "map_overrides.json");
		if (mapOverrides.exists()) {
			Log.info("Trying to read map overrides file");
			try {
				JSONObject mapFiles = JSONFileUtils.readJSONObjectFromFile(mapOverrides);

				boolean relative = mapFiles.getBoolean("relative");

				mapFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("maps_folder"));
				worldFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("worlds_folder"));
				lootTableFolder = new File((relative ? this.getDataFolder().getPath() + File.separator : "") + mapFiles.getString("loot_tables_folder"));

				Log.info("New paths:");
				Log.info("Map folder: " + mapFolder.getAbsolutePath());
				Log.info("World folder: " + worldFolder.getAbsolutePath());
				Log.info("Loot table folder:" + lootTableFolder.getAbsolutePath());
			} catch (JSONException | IOException e) {
				e.printStackTrace();
				Log.error("Failed to read map overrides from file " + mapOverrides.getAbsolutePath());
			}
		}

		try {
			FileUtils.forceMkdir(getDataFolder());
			FileUtils.forceMkdir(mapFolder);
			FileUtils.forceMkdir(worldFolder);
			FileUtils.forceMkdir(lootTableFolder);
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.fatal("Skywars", "Failed to setup data directory");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		// Enable required modules
		ModuleManager.enable(GameManager.class);
		ModuleManager.enable(GameLobby.class);
		ModuleManager.enable(CompassTracker.class);

		// Load modules
		ModuleManager.loadModule(this, RegenOnKill.class, true);

		// Register map modules
		MapModuleManager.addMapModule("skywars.island_special_loot", SkywarsIslandSpecialLootTableMapModule.class);

		// Init game and maps
		this.game = new Skywars();

		GameManager.getInstance().loadGame(game);

		GUIMapVote mapSelector = new GUIMapVote();

		GameManager.getInstance().setMapSelector(mapSelector);

		// Register events
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getServer().getPluginManager().registerEvents(mapSelector, this);

		// Register loot loaders
		/*
		 * NovaCore.getInstance().getLootTableManager().addLoader(new
		 * SkywarsLootLoader());
		 */
		NovaCore.getInstance().getLootTableManager().addLootTable(new HardCodedSkywarsLootTableV1("novauniverse.games.skywars_island_hardcoded_v1", 0, 0));

		// Load loot tables
		NovaCore.getInstance().getLootTableManager().loadAll(lootTableFolder);

		// Read maps
		Log.info("Skywars", "Loading maps from " + mapFolder.getPath());
		GameManager.getInstance().readMapsFromFolder(mapFolder, worldFolder);
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onCompassTracking(CompassTrackingEvent e) {
		boolean enabled = false;
		if (GameManager.getInstance().isEnabled()) {
			if (GameManager.getInstance().hasGame()) {
				if (GameManager.getInstance().getActiveGame().hasStarted()) {
					enabled = true;
				}
			}
		}
		e.setCancelled(!enabled);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVersionIndependantPlayerAchievementAwarded(VersionIndependentPlayerAchievementAwardedEvent e) {
		e.setCancelled(true);
	}
}
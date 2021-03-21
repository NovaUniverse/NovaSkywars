package net.novauniverse.games.skywars.modules;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerEliminatedEvent;

public class RegenOnKill extends NovaModule implements Listener {
	public String getName() {
		return "RegenOnKill";
	}

	@EventHandler
	public void on(PlayerEliminatedEvent e) {
		if (e.getKiller() instanceof Player) {
			Player killer = (Player) e.getKiller();
			if (!killer.isDead()) {
				if (killer.getGameMode() == GameMode.SURVIVAL || killer.getGameMode() == GameMode.ADVENTURE) {
					killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 2), false);
				}
			}
		}
	}
}
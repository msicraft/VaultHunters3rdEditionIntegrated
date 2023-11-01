package me.msicraft.vaulthuntersintegrated.Event;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEnterLoveModeEvent;

public class WorldRelatedEvent implements Listener {

    private static double breedingPercent = 1;

    public static void reloadVariables() {
        breedingPercent = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.Breeding.Percent") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("Setting.Breeding.Percent") : 1;
    }

    @EventHandler
    public void breeding(EntityEnterLoveModeEvent e) {
        boolean check = Math.random() < breedingPercent;
        if (!check) {
            e.setCancelled(true);
        }
    }

}

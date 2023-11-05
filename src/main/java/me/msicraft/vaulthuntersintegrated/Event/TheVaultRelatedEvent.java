package me.msicraft.vaulthuntersintegrated.Event;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TheVaultRelatedEvent implements Listener {

    private static boolean disableTeleportToVault = false;
    private static boolean isIgnoreOp = false;

    public static void reloadVariables() {
        disableTeleportToVault = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.DisableTeleportToVault.Enabled") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("Setting.DisableTeleportToVault.Enabled");
        isIgnoreOp = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.DisableTeleportToVault.Ignore-Op") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("Setting.DisableTeleportToVault.Ignore-Op");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerTeleportToVault(PlayerTeleportEvent e) {
        if (disableTeleportToVault) {
            PlayerTeleportEvent.TeleportCause cause = e.getCause();
            if (cause == PlayerTeleportEvent.TeleportCause.PLUGIN) {
                Player player = e.getPlayer();
                if (isIgnoreOp) {
                    if (player.isOp()) {
                        return;
                    }
                }
                Location fromLoc = e.getFrom();
                Location toLoc = e.getTo();
                if (toLoc == null) {
                    return;
                }
                if (fromLoc.getWorld() == null || toLoc.getWorld() == null) {
                    return;
                }
                NamespacedKey fromNamespace = fromLoc.getWorld().getKey();
                NamespacedKey toNamespace = toLoc.getWorld().getKey();
                if (fromNamespace.getNamespace().equals("minecraft") && toNamespace.getNamespace().equals(WorldUtil.THE_VAULT_NAMESPACE_NAME)) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "해당 장소로는 이동 불가능합니다");
                } else if (fromNamespace.getNamespace().equals(WorldUtil.THE_VAULT_NAMESPACE_NAME) && toNamespace.getNamespace().equals("minecraft")) {
                    e.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "해당 장소로는 이동 불가능합니다");
                } else if (fromNamespace.getNamespace().equals(WorldUtil.THE_VAULT_NAMESPACE_NAME) && toNamespace.getNamespace().equals(WorldUtil.THE_VAULT_NAMESPACE_NAME)) {
                    if (!fromNamespace.getKey().equals(toNamespace.getKey())) {
                        e.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "해당 장소로는 이동 불가능합니다");
                    }
                }
            }
        }
    }
}

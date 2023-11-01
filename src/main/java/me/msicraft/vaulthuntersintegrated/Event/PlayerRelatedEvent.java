package me.msicraft.vaulthuntersintegrated.Event;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerRelatedEvent implements Listener {

    private static boolean disableBoneMeal = false;
    private static boolean disableMount = false;
    private static int noDamageTick = 20;
    private static boolean useDamageCause = false;
    private static final List<EntityDamageEvent.DamageCause> disableDamageCauses = new ArrayList<>();
    private static boolean useInventoryTotem = false;
    private static int totemInvulnerableTick = 1;

    public static void reloadVariables() {
        disableMount = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.Disable-Mount") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("Setting.Disable-Mount");
        disableBoneMeal = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.Disable-BoneMeal") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("Setting.Disable-BoneMeal");
        useDamageCause = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.NoDamageTick.UseDamageCause") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("Setting.NoDamageTick.UseDamageCause");
        noDamageTick = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.NoDamageTick.Tick") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("Setting.NoDamageTick.Tick") : 20;
        for (String s : VaultHuntersIntegrated.getPlugin().getConfig().getStringList("NoDamageTickDamageCause")) {
            EntityDamageEvent.DamageCause damageCause = null;
            try {
                damageCause = EntityDamageEvent.DamageCause.valueOf(s.toUpperCase());
            } catch (NullPointerException | IllegalArgumentException ignored) {}
            if (damageCause != null) {
                disableDamageCauses.add(damageCause);
            }
        }
        useInventoryTotem = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.UseInventoryTotem.Enabled") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("Setting.UseInventoryTotem.Enabled");
        totemInvulnerableTick = VaultHuntersIntegrated.getPlugin().getConfig().contains("Setting.UseInventoryTotem.InvulnerableTick") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("Setting.UseInventoryTotem.InvulnerableTick") : 1;
    }

    @EventHandler
    public void EntityVehicle(VehicleEnterEvent e) {
        if (disableMount) {
            if (e.getEntered().getType() != EntityType.PLAYER) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDisableBoneMeal(PlayerInteractEvent e) {
        if (disableBoneMeal) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null && e.getItem().getType() == Material.BONE_MEAL) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void noDamageTick(EntityDamageEvent e) {
        EntityDamageEvent.DamageCause damageCause = e.getCause();
        if (useDamageCause) {
            if (disableDamageCauses.contains(damageCause)) {
                return;
            }
        }
        if (e.getEntity() instanceof LivingEntity livingEntity) {
            Bukkit.getScheduler().runTask(VaultHuntersIntegrated.getPlugin(), ()-> {
                livingEntity.setNoDamageTicks(noDamageTick);
            });
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        double absorptionAmount = player.getAbsorptionAmount();
        if (absorptionAmount > 0) {
            player.setAbsorptionAmount(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void useInventoryTotem(EntityDamageEvent e) {
        if (useInventoryTotem) {
            if (e.getCause() != EntityDamageEvent.DamageCause.VOID || e.getCause() != EntityDamageEvent.DamageCause.SUICIDE) {
                if (e.getEntity() instanceof Player player) {
                    double finalDamage = e.getFinalDamage();
                    if (player.getHealth() - finalDamage <= 2) {
                        int totemSlot = EntityUtil.hasTotemOfUndying(player);
                        if (totemSlot != -1) {
                            ItemStack totemStack = player.getInventory().getItem(totemSlot);
                            if (totemStack != null) {
                                EntityUtil.applyTotemOfUndying(player, totemStack);
                                e.setDamage(0);
                                e.setCancelled(true);
                                player.setInvulnerable(true);
                                Bukkit.getScheduler().runTaskLater(VaultHuntersIntegrated.getPlugin(), () -> {
                                    player.setInvulnerable(false);
                                }, totemInvulnerableTick);
                            }
                        }
                    }
                }
            }
        }
    }

}

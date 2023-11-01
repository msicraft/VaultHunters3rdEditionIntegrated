package me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.Event;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.AttributeUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.EntityUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PlayerKillPointRelatedEvent implements Listener {

    private static boolean isEnableFixEvent = false;
    private static boolean isEnabled = false;
    private static boolean isEnabledDistributionKillPointExp = false;
    private static int distributionDistance = 1;
    private static double spawnerEntityMulti = 1;

    public static void reloadVariables() {
        isEnabled = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.Enabled") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("KillPointSetting.Enabled");
        isEnableFixEvent = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.Fix-DeathEvent") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("KillPointSetting.Fix-DeathEvent");
        isEnabledDistributionKillPointExp = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.DistributionKillPointExp") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("KillPointSetting.DistributionKillPointExp");
        distributionDistance = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.DistributionDistance") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("KillPointSetting.DistributionDistance") : 1;
        spawnerEntityMulti = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.SpawnerEntityMulti") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("KillPointSetting.SpawnerEntityMulti") : 1.0;
    }

    private ItemStack getKillPointItemStack(double value) {
        ItemStack itemStack = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        itemMeta.setDisplayName(ChatColor.WHITE + "Kill Point Exp");
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        data.set(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_KillPointItem"), PersistentDataType.STRING, "VHI_KillPointItem");
        data.set(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_KillPointItem-Value"), PersistentDataType.STRING, String.valueOf(value));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onSpawnerMobTag(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            PersistentDataContainer data = e.getEntity().getPersistentDataContainer();
            data.set(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI-SpawnerEntity"), PersistentDataType.STRING, "VHI-Spawner");
        }
    }

    private boolean hasSpawnerTag(LivingEntity livingEntity) {
        boolean check = false;
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI-SpawnerEntity"), PersistentDataType.STRING)) {
            check = true;
        }
        return check;
    }

    @EventHandler
    public void playerKillEntity(EntityDeathEvent e) {
        if (isEnabled && !isEnableFixEvent) {
            String killPointExpEquations = KillPointUtil.getKillPointExpEquations();
            if (killPointExpEquations != null) {
                LivingEntity livingEntity = e.getEntity();
                Player player = livingEntity.getKiller();
                if (player != null) {
                    double exp = 0;
                    double maxHealth = AttributeUtil.getMaxHealth(livingEntity);
                    double damage = AttributeUtil.getDamageValue(livingEntity);
                    double armor = AttributeUtil.getArmorValue(livingEntity);
                    double armorToughness = AttributeUtil.getArmorToughnessValue(livingEntity);
                    exp = KillPointUtil.calculateEntityKillPointExp(maxHealth, damage, armor, armorToughness);
                    boolean isSpawner = hasSpawnerTag(livingEntity);
                    if (isSpawner) {
                        exp = exp * spawnerEntityMulti;
                    }
                    if (isEnabledDistributionKillPointExp) {
                        List<Player> nearPlayers = new ArrayList<>();
                        for (Entity entity : livingEntity.getNearbyEntities(distributionDistance, 5, distributionDistance)) {
                            if (entity instanceof Player p) {
                                nearPlayers.add(p);
                            }
                        }
                        if (!nearPlayers.isEmpty()) {
                            double perPlayerExp = exp / nearPlayers.size();
                            for (Player np : nearPlayers) {
                                KillPointUtil.addKillPointExp(np, perPlayerExp);
                            }
                            if (VaultHuntersIntegrated.isDebug) {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "플레이어 킬 포인트 경험치 분배");
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Living Entity: " + ChatColor.GREEN + livingEntity.getCustomName() + " | " + livingEntity.getType() + " | " + livingEntity.getName());
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Killer Player: " + ChatColor.GREEN + player.getName());
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "총 플레이어: " + ChatColor.GREEN + nearPlayers.size() + "| 플레이어: " + ChatColor.GREEN + nearPlayers);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "총 경험치: " + ChatColor.GREEN + exp + " | 분배 경험치: " + ChatColor.GREEN + perPlayerExp);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "스포너 몹: " + ChatColor.GREEN + isSpawner);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Health: " + ChatColor.GREEN + maxHealth + " | Damage: " + damage);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Armor: " + ChatColor.GREEN + armor + " | ArmorToughness: " + armorToughness);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                            }
                        }
                    } else {
                        KillPointUtil.addKillPointExp(player, exp);
                        if (VaultHuntersIntegrated.isDebug) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "PlayerKillEntity Event");
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Living Entity: " + ChatColor.GREEN + livingEntity.getCustomName() + " | " + livingEntity.getType() + " | " + livingEntity.getName());
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Killer Player: " + ChatColor.GREEN + player.getName());
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Health: " + ChatColor.GREEN + maxHealth + " | Damage: " + damage);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Armor: " + ChatColor.GREEN + armor + " | ArmorToughness: " + armorToughness);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Equations: " + ChatColor.GREEN + killPointExpEquations);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Get KillPoint Exp: " + ChatColor.GREEN + exp);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKillEntityFix(EntityDamageByEntityEvent e) {
        if (isEnabled && isEnableFixEvent) {
            String killPointExpEquations = KillPointUtil.getKillPointExpEquations();
            if (killPointExpEquations != null) {
                if (e.getEntityType() != EntityType.PLAYER) {
                    Entity entity = e.getEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        double currentHealth = livingEntity.getHealth();
                        double finalDamage = e.getFinalDamage();
                        if (currentHealth - finalDamage <= 0) {
                            Bukkit.getScheduler().runTask(VaultHuntersIntegrated.getPlugin(), () -> {
                                if (EntityUtil.isDeadEntity(livingEntity)) {
                                    double exp;
                                    double maxHealth = AttributeUtil.getMaxHealth(livingEntity);
                                    double damage = AttributeUtil.getDamageValue(livingEntity);
                                    double armor = AttributeUtil.getArmorValue(livingEntity);
                                    double armorToughness = AttributeUtil.getArmorToughnessValue(livingEntity);
                                    exp = KillPointUtil.calculateEntityKillPointExp(maxHealth, damage, armor, armorToughness);
                                    boolean isSpawner = hasSpawnerTag(livingEntity);
                                    if (isSpawner) {
                                        exp = exp * spawnerEntityMulti;
                                    }
                                    ItemStack itemStack = getKillPointItemStack(exp);
                                    Location location = livingEntity.getLocation();
                                    World world = livingEntity.getWorld();
                                    world.dropItemNaturally(location, itemStack);
                                    Bukkit.getScheduler().runTask(VaultHuntersIntegrated.getPlugin(), () -> {
                                        for (Entity nearbyItem : world.getNearbyEntities(location, 2.5, 1, 2.5)) {
                                            if (nearbyItem instanceof Item item) {
                                                ItemMeta itemMeta = item.getItemStack().getItemMeta();
                                                if (itemMeta != null) {
                                                    PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                                                    if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_KillPointItem"), PersistentDataType.STRING)) {
                                                        item.setCustomName(ChatColor.BOLD + "" + ChatColor.GREEN + "Kill Point");
                                                        item.setCustomNameVisible(true);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    if (VaultHuntersIntegrated.isDebug) {
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "PlayerKillEntity Fix Event");
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Living Entity: " + ChatColor.GREEN + livingEntity.getCustomName() + " | " + livingEntity.getType());
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Equations: " + ChatColor.GREEN + killPointExpEquations);
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Drop KillPoint Exp: " + ChatColor.GREEN + exp);
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void pickUpKillPointItem(EntityPickupItemEvent e) {
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof Player player) {
            Item item = e.getItem();
            ItemStack itemStack = item.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_KillPointItem"), PersistentDataType.STRING)) {
                    e.setCancelled(true);
                    String s = data.get(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_KillPointItem-Value"), PersistentDataType.STRING);
                    if (s != null) {
                        double value = Double.parseDouble(s);
                        KillPointUtil.addKillPointExp(player, value);
                        if (VaultHuntersIntegrated.isDebug) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player Pickup KillPoint item");
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player: " + ChatColor.GREEN + player.getName());
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Drop KillPoint Exp: " + ChatColor.GREEN + value);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                        }
                    }
                    item.remove();
                }
            }
        }
    }

}

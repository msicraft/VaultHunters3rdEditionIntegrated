package me.msicraft.vaulthuntersintegrated.aCommon.Util;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {

    public static boolean isDeadEntity(LivingEntity livingEntity) {
        return livingEntity.isDead() || livingEntity.getHealth() <= 0;
    }

    public static int getPlayerEmptySlot(Player player) {
        int slot = -1;
        int size = 36;
        for (int a = 0; a<size; a++) {
            ItemStack itemStack = player.getInventory().getItem(a);
            if (itemStack == null) {
                slot = a;
                break;
            }

        }
        return slot;
    }

    public static int hasTotemOfUndying(Player player) {
        int slot = -1;
        for (int a = 0; a<36; a++) {
            ItemStack itemStack = player.getInventory().getItem(a);
            if (itemStack != null && itemStack.getType() == Material.TOTEM_OF_UNDYING) {
                slot = a;
                break;
            }
        }
        return slot;
    }

    private static final PotionEffect FIRE_RESISTANT_EFFECT = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, (20*30), 0, false, false);
    private static final PotionEffect REGENERATION_EFFECT = new PotionEffect(PotionEffectType.REGENERATION, (20*60), 0, false, false);

    public static void applyTotemOfUndying(Player player, ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() == Material.TOTEM_OF_UNDYING) {
            itemStack.setAmount(itemStack.getAmount() - 1);
            player.playEffect(EntityEffect.TOTEM_RESURRECT);
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
            double maxHealth = player.getMaxHealth();
            double cal = maxHealth * 0.5;
            player.setHealth(cal);
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
            player.addPotionEffect(REGENERATION_EFFECT);
            player.addPotionEffect(FIRE_RESISTANT_EFFECT);
        }
    }

    public static void healLivingEntity(LivingEntity livingEntity, double amount) {
        double currentHealth = livingEntity.getHealth();
        double cal = currentHealth + amount;
        if (cal > livingEntity.getMaxHealth()) {
            cal = livingEntity.getMaxHealth();
        }
        livingEntity.setHealth(cal);
    }

    public static List<Player> getNearPlayer(Player centerPlayer, int radius, boolean containSelf) {
        List<Player> list = new ArrayList<>();
        if (containSelf) {
            list.add(centerPlayer);
        }
        for (Entity entity : centerPlayer.getNearbyEntities(radius, 4, radius)) {
            if (entity instanceof Player player) {
                list.add(player);
            }
        }
        return list;
    }
    public static List<Player> getNearPlayer(Player centerPlayer, int radius, int max, boolean containSelf) {
        List<Player> list = new ArrayList<>();
        if (containSelf) {
            list.add(centerPlayer);
        }
        int count = 0;
        for (Entity entity : centerPlayer.getNearbyEntities(radius, 4, radius)) {
            if (count >= max) {
                break;
            }
            if (entity instanceof Player player) {
                list.add(player);
                count++;
            }
        }
        return list;
    }

}

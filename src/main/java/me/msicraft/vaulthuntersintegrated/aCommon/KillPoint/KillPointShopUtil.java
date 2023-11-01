package me.msicraft.vaulthuntersintegrated.aCommon.KillPoint;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KillPointShopUtil {

    public static List<String> getShopItemInternalNames() {
        List<String> list = new ArrayList<>();
        ConfigurationSection section = VaultHuntersIntegrated.getPlugin().getConfig().getConfigurationSection("KillPointShop");
        if (section != null) {
            list.addAll(section.getKeys(false));
        }
        return list;
    }

    public static boolean isEnabled(String name) {
        boolean check = false;
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointShop." + name + ".Enabled")) {
            check = VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("KillPointShop." + name + ".Enabled");
        }
        return check;
    }

    public static int getRequiredKillPoint(String name) {
        int value = -1;
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointShop." + name + ".Required-KillPoint")) {
            value = VaultHuntersIntegrated.getPlugin().getConfig().getInt("KillPointShop." + name + ".Required-KillPoint");
        }
        return value;
    }

    public static Material getMaterial(String name) {
        Material material = Material.STONE;
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointShop." + name + ".Material")) {
            String getMaterialName = VaultHuntersIntegrated.getPlugin().getConfig().getString("KillPointShop." + name + ".Material");
            if (getMaterialName != null) {
                try {
                    material = Material.valueOf(getMaterialName.toUpperCase());
                } catch (IllegalArgumentException | NullPointerException ignored) {
                }
            }
        }
        return material;
    }

    public static String getName(String name) {
        String a = null;
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointShop." + name + ".Name")) {
            a = VaultHuntersIntegrated.getPlugin().getConfig().getString("KillPointShop." + name + ".Name");
            if (a != null) {
                a = ChatColor.translateAlternateColorCodes('&', a);
            }
        }
        return a;
    }

    public static List<String> getLoreList(String name) {
        List<String> list = new ArrayList<>();
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointShop." + name + ".Lore")) {
            List<String> temp = VaultHuntersIntegrated.getPlugin().getConfig().getStringList("KillPointShop." + name + ".Lore");
            for (String s : temp) {
                s = s.replaceAll("<requiredKillPoint>", String.valueOf(getRequiredKillPoint(name)));
                list.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        return list;
    }

    public static Location getSpawnLocation() {
        Location location = null;
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("SpawnLocation")) {
            String s = VaultHuntersIntegrated.getPlugin().getConfig().getString("SpawnLocation");
            if (s != null) {
                String[] a = s.split(":");
                World world = Bukkit.getWorld(a[0]);
                if (world != null) {
                    double x = Double.parseDouble(a[1]);
                    double y = Double.parseDouble(a[2]);
                    double z = Double.parseDouble(a[3]);
                    float yaw = Float.parseFloat(a[4]);
                    float pitch = Float.parseFloat(a[5]);
                    location = new Location(world, x, y, z, yaw, pitch);
                }
            }
        }
        return location;
    }

    public static boolean moveSpawn(Player player) {
        Location location = getSpawnLocation();
        if (location != null) {
            player.teleport(location);
            player.sendMessage(ChatColor.GREEN + "스폰 지점으로 이동 되었습니다.");
            return true;
        }
        return false;
    }

}

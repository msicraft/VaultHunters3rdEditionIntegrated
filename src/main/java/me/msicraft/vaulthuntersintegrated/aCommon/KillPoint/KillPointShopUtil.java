package me.msicraft.vaulthuntersintegrated.aCommon.KillPoint;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

    public static List<String> getLoreList(String name, Player player) {
        List<String> list = new ArrayList<>();
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointShop." + name + ".Lore")) {
            List<String> temp = VaultHuntersIntegrated.getPlugin().getConfig().getStringList("KillPointShop." + name + ".Lore");
            for (String s : temp) {
                s = s.replaceAll("<requiredKillPoint>", String.valueOf(getRequiredKillPoint(name)));
                s = s.replaceAll("<lastDeathLocation>", String.valueOf(PlayerDataUtil.getPlayerData(player).getLastDeathLocationToString()));
                list.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        return list;
    }

    public static Location getSpawnLocation() {
        Location location = null;
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("SpawnLocation")) {
            location = VaultHuntersIntegrated.getPlugin().getConfig().getLocation("SpawnLocation");
            /*
            String s = VaultHuntersIntegrated.getPlugin().getConfig().getString("SpawnLocation");
            if (s != null) {
                String[] a = s.split(":");
                String namespace = a[0];
                String key = a[1];
                World world = null;
                for (World w : Bukkit.getWorlds()) {
                    NamespacedKey namespacedKey = w.getKey();
                    if (namespacedKey.getNamespace().equals(namespace) && namespacedKey.getKey().equals(key)) {
                        world = w;
                        break;
                    }
                }
                if (world != null) {
                    double x = Double.parseDouble(a[2]);
                    double y = Double.parseDouble(a[3]);
                    double z = Double.parseDouble(a[4]);
                    float yaw = Float.parseFloat(a[5]);
                    float pitch = Float.parseFloat(a[6]);
                    location = new Location(world, x, y, z, yaw, pitch);
                }
            }
             */
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

    public static boolean backLastDeathLocation(Player player) {
        Location location = PlayerDataUtil.getPlayerData(player).getLastDeathLocation();
        if (location != null) {
            player.teleport(location);
            player.sendMessage(ChatColor.GREEN + "죽은 위치로 이동 되었습니다.");
            return true;
        }
        return false;
    }

}

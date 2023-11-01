package me.msicraft.vaulthuntersintegrated.aCommon.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDataUtil {

    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    public static Set<UUID> getUUIDList() {
        return playerDataMap.keySet();
    }

    public static PlayerData getPlayerData(Player player) {
        PlayerData playerData = null;
        UUID uuid = player.getUniqueId();
        if (playerDataMap.containsKey(uuid)) {
            playerData = playerDataMap.get(uuid);
        } else {
            playerData = new PlayerData(player);
            playerDataMap.put(uuid, playerData);
        }
        return playerData;
    }

    public static PlayerData getPlayerData(UUID uuid) {
        PlayerData playerData = null;
        if (playerDataMap.containsKey(uuid)) {
            playerData = playerDataMap.get(uuid);
        } else {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                playerData = new PlayerData(player);
                playerDataMap.put(uuid, playerData);
            }
        }
        return playerData;
    }

    public static void registerPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData playerData;
        if (playerDataMap.containsKey(uuid)) {
            playerData = playerDataMap.get(uuid);
        } else {
            playerData = new PlayerData(player);
        }
        playerDataMap.put(uuid, playerData);
    }

    public static void unregisterPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerDataMap.containsKey(uuid)) {
            PlayerData playerData = playerDataMap.get(uuid);
            savePlayerData(playerData);
        }
        playerDataMap.remove(uuid);
    }

    public static void savePlayerData(PlayerData playerData) {
        playerData.savePlayerData();
    }

}

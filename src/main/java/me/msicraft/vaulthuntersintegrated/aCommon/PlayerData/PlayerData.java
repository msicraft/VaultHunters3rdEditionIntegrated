package me.msicraft.vaulthuntersintegrated.aCommon.PlayerData;

import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.File.PlayerDataFile;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerSpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerStats;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.SpellAbility;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    private final Player player;
    private final PlayerDataFile playerDataFile;
    private final PlayerSpellAbility playerSpellAbility;
    private final PlayerStats playerStats;

    private int killPoint;
    private double killPointExp;

    private Location lastDeathLocation;

    public PlayerData(Player player) {
        this.player = player;
        playerDataFile = new PlayerDataFile(player);
        playerSpellAbility = new PlayerSpellAbility(player, playerDataFile);
        playerStats = new PlayerStats(player, playerDataFile);
        killPoint = playerDataFile.getConfig().contains("KillPoint.Point") ? playerDataFile.getConfig().getInt("KillPoint.Point") : 0;
        killPointExp = playerDataFile.getConfig().contains("KillPoint.Exp") ? playerDataFile.getConfig().getDouble("KillPoint.Exp") : 0;
        lastDeathLocation = playerDataFile.getConfig().contains("LastDeathLocation") ? playerDataFile.getConfig().getLocation("LastDeathLocation") : null;
    }

    public void savePlayerData() {
        PlayerDataFile playerDataFile = this.playerDataFile;
        if (playerDataFile != null) {
            playerDataFile.getConfig().set("KillPoint.Point", this.killPoint);
            playerDataFile.getConfig().set("KillPoint.Exp", this.killPointExp);
            playerDataFile.getConfig().set("LastDeathLocation", this.lastDeathLocation);
            playerDataFile.saveConfig();
            saveSpellAbility();
            savePlayerStats();
        }
    }

    private void saveSpellAbility() {
        List<String> purchasedSpellAbilities = new ArrayList<>();
        playerDataFile.getConfig().set("SpellAbility.Bind", null);
        for (int a : getPlayerSpellAbility().getBindSpellAbilityMap().keySet()) {
            SpellAbility spellAbility = getPlayerSpellAbility().getBindSpellAbilityMap().get(a);
            if (spellAbility != null) {
                playerDataFile.getConfig().set("SpellAbility.Bind." + a + ".SpellKey", spellAbility.getKey());
                if (getPlayerSpellAbility().getSpellAbilityCoolDownMap().containsKey(spellAbility)) {
                    playerDataFile.getConfig().set("SpellAbility.Bind." + a + ".LastUseTime", getPlayerSpellAbility().getSpellAbilityCoolDownMap().get(spellAbility));
                }
            }
        }
        for (SpellAbility spellAbility : getPlayerSpellAbility().getPurchasedSpellAbilityMap().keySet()) {
            int level = getPlayerSpellAbility().getPurchasedSpellAbilityMap().get(spellAbility);
            purchasedSpellAbilities.add(spellAbility.getKey() + ":" + level);
        }
        playerDataFile.getConfig().set("SpellAbility.PurchasedSpellAbilityList", purchasedSpellAbilities);
        playerDataFile.saveConfig();
    }

    private void savePlayerStats() {
        playerDataFile.getConfig().set("PlayerStats.Mana", playerStats.getMana());
        playerDataFile.getConfig().set("PlayerStats.ManaLevel", playerStats.getManaLevel());
        playerDataFile.getConfig().set("PlayerStats.ManaRegenerationLevel", playerStats.getManaRegenLevel());
        playerDataFile.saveConfig();
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDataFile getPlayerDataFile() {
        return playerDataFile;
    }

    public PlayerSpellAbility getPlayerSpellAbility() {
        return playerSpellAbility;
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public int getKillPoint() {
        return killPoint;
    }

    public void setKillPoint(int killPoint) {
        this.killPoint = killPoint;
    }

    public double getKillPointExp() {
        return killPointExp;
    }

    public void setKillPointExp(double killPointExp) {
        this.killPointExp = killPointExp;
    }

    public Location getLastDeathLocation() {
        return lastDeathLocation;
    }

    public void setLastDeathLocation(Location location) {
        this.lastDeathLocation = location;
    }
}

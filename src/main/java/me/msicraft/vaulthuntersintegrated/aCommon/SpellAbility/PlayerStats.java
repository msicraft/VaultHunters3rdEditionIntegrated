package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.File.PlayerDataFile;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Task.ManaRegenerationTask;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.PlayerStatsUtil;
import org.bukkit.entity.Player;

public class PlayerStats {

    private final Player player;

    private double mana;
    private int manaLevel;
    private int manaRegenLevel;

    public PlayerStats(Player player, PlayerDataFile playerDataFile) {
        this.player = player;
        this.mana = playerDataFile.getConfig().contains("PlayerStats.Mana") ? playerDataFile.getConfig().getDouble("PlayerStats.Mana") : PlayerStatsUtil.getBaseMana();
        this.manaLevel = playerDataFile.getConfig().contains("PlayerStats.ManaLevel") ? playerDataFile.getConfig().getInt("PlayerStats.ManaLevel") : 1;
        this.manaRegenLevel = playerDataFile.getConfig().contains("PlayerStats.ManaRegenerationLevel") ? playerDataFile.getConfig().getInt("PlayerStats.ManaRegenerationLevel") : 1;
        new ManaRegenerationTask(player).runTaskTimer(VaultHuntersIntegrated.getPlugin(), 0L, 20L);
    }

    public boolean hasEnoughMana(double requiredMana) {
        return mana >= requiredMana;
    }

    public Player getPlayer() {
        return player;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public void addMana(double amount) {
        mana = mana + amount;
        if (mana < 0) {
            mana = 0;
        }
    }

    public int getManaLevel() {
        return manaLevel;
    }

    public void setManaLevel(int manaLevel) {
        this.manaLevel = manaLevel;
    }

    public int getManaRegenLevel() {
        return manaRegenLevel;
    }

    public void setManaRegenLevel(int manaRegenLevel) {
        this.manaRegenLevel = manaRegenLevel;
    }

    public double getMaxMana() {
        int level = manaLevel;
        double mana = PlayerStatsUtil.getBaseMana() + (PlayerStatsUtil.getLevelPerMana() * (level - 1));
        return Math.round(mana * 100.0) / 100.0;
    }

    public double getManaRegen() {
        int level = manaRegenLevel;
        double regen = PlayerStatsUtil.getBaseManaRegeneration() + (PlayerStatsUtil.getLevelPerManaRegen() * (level - 1));
        return Math.round(regen * 100.0) / 100.0;
    }

}

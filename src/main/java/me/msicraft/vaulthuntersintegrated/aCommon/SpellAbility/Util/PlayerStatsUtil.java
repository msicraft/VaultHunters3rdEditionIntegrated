package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;

public class PlayerStatsUtil {

    public static double getBaseMana() {
        return VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.BaseMana") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("SpellAbility.BaseMana"): 20;
    }

    public static double getBaseManaRegeneration() {
        return VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.BaseManaRegeneration") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("SpellAbility.BaseManaRegeneration"): 0.01;
    }

    public static double getLevelPerMana() {
        return VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.LevelPerMana") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("SpellAbility.LevelPerMana"): 0;
    }

    public static double getLevelPerManaRegen() {
        return VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.LevelPerManaRegen") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("SpellAbility.LevelPerManaRegen"): 0;
    }

    public static int getRequiredManaKillPoint() {
        return VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.RequiredManaKillPoint") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("SpellAbility.RequiredManaKillPoint") : 999;
    }

    public static int getRequiredManaRegenKillPoint() {
        return VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.RequiredManaRegenKillPoint") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("SpellAbility.RequiredManaRegenKillPoint") : 999;
    }

    public static int getMaxManaLevel() {
        return VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.MaxManaLevel") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("SpellAbility.MaxManaLevel") : 1;
    }

    public static int getMaxManaRegenLevel() {
        return VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.MaxManaRegenLevel") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("SpellAbility.MaxManaRegenLevel") : 1;
    }

}

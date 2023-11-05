package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util;

import io.lumine.mythic.bukkit.MythicBukkit;
import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerSpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.SpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.EntityUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellAbilityUtil {

    public static boolean isEnabled = false;
    public static boolean isDisplayBuffOnTabList = false;
    public static int buffOnTablistPerLine = 4;

    public static void reloadVariables() {
        isEnabled = VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.Enabled") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("SpellAbility.Enabled");
        isDisplayBuffOnTabList = VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.BuffDisplayTablist.Enabled") && VaultHuntersIntegrated.getPlugin().getConfig().getBoolean("SpellAbility.BuffDisplayTablist.Enabled");
        buffOnTablistPerLine = VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.BuffDisplayTablist.PerLine") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("SpellAbility.BuffDisplayTablist.PerLine") : 4;
        update();
    }

    private static final Map<String, SpellAbility> spellAbilityMap = new HashMap<>(); //spellNameKey, spellAbility

    public static void initialize() {
        for (SpellAbility spellAbility : SpellAbility.values()) {
            updateSpellAbility(spellAbility);
            registerSpellAbility(spellAbility);
        }
    }

    private static void update() {
        for (String s : spellAbilityMap.keySet()) {
            SpellAbility spellAbility = spellAbilityMap.get(s);
            updateSpellAbility(spellAbility);
        }
    }

    public static void registerSpellAbility(SpellAbility spellAbility) {
        spellAbilityMap.put(spellAbility.getKey(), spellAbility);
    }

    private static void updateSpellAbility(SpellAbility spellAbility) {
        String path = spellAbility.getPath();
        if (spellAbility.isMythicSkill()) {
            spellAbility.setMythicSkillInternalName(VaultHuntersIntegrated.spellAbilityDataFile.getConfig().contains(path + ".MythicSkill") ? VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getString(path + ".MythicSkill") : null);
        }
        spellAbility.setDisplayName(VaultHuntersIntegrated.spellAbilityDataFile.getConfig().contains(path + ".DisplayName") ? VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getString(path + ".DisplayName") : "Unknown");
        spellAbility.setCoolDown(VaultHuntersIntegrated.spellAbilityDataFile.getConfig().contains(path + ".CoolDown") ? VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getDouble(path + ".CoolDown") : 9999);
        spellAbility.setValue(VaultHuntersIntegrated.spellAbilityDataFile.getConfig().contains(path + ".Value") ? VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getDouble(path + ".Value") : 1);
        spellAbility.setLevelPerValue(VaultHuntersIntegrated.spellAbilityDataFile.getConfig().contains(path + ".LevelPerValue") ? VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getDouble(path + ".LevelPerValue") : 0);
        spellAbility.setMaxLevel(VaultHuntersIntegrated.spellAbilityDataFile.getConfig().contains(path + ".MaxLevel") ? VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getInt(path + ".MaxLevel") : 1);
        spellAbility.setRequiredKillPoint(VaultHuntersIntegrated.spellAbilityDataFile.getConfig().contains(path + ".RequiredKillPoint") ? VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getInt(path + ".RequiredKillPoint") : 9999);
        spellAbility.setMana(VaultHuntersIntegrated.spellAbilityDataFile.getConfig().contains(path + ".Mana") ? VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getDouble(path + ".Mana") : 9999);
    }

    public static SpellAbility getSpellAbility(String spellKey) {
        if (spellAbilityMap.containsKey(spellKey)) {
            return spellAbilityMap.get(spellKey);
        }
        return null;
    }

    public static int getMaxBindSpellSize() {
        if (VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.MaxBindSize")) {
            return VaultHuntersIntegrated.getPlugin().getConfig().getInt("SpellAbility.MaxBindSize");
        }
        return 1;
    }

    public static void castSpell(Player player, SpellAbility spellAbility) {
        PlayerSpellAbility playerSpellAbility = PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility();
        int level = playerSpellAbility.getSpellAbilityLevel(spellAbility);
        if (level == 0) {
            return;
        }
        level = level - 1;
        if (spellAbility.isMythicSkill()) {
            float power = 1;
            double levelPerValue = spellAbility.getLevelPerValue() / 100.0;
            power = (float) (power + (levelPerValue * level));
            MythicBukkit.inst().getAPIHelper().castSkill(player, spellAbility.getMythicSkillInternalName(), power);
        } else {
            double value = spellAbility.getValue();
            double levelPerValue = spellAbility.getLevelPerValue();
            double calValue = value + (levelPerValue * level);
            switch (spellAbility) {
                case SELF_HEAL -> EntityUtil.healLivingEntity(player, calValue);
                case RANGE_HEAL -> {
                    List<Player> playerList = EntityUtil.getNearPlayer(player, 10, true);
                    for (Player p : playerList) {
                        EntityUtil.healLivingEntity(p, calValue);
                    }
                }
                case ATTACK_AURA -> playerSpellAbility.addBuff(spellAbility, 25, null);
                case ARMOR_AURA -> playerSpellAbility.addBuff(spellAbility, 20, null);
            }
        }
    }

}

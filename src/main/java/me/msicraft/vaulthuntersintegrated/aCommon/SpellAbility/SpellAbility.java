package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public enum SpellAbility {

    SELF_HEAL("Spell.SelfHeal", "SelfHeal" ,"힐", false, null,
            1, 9999, 0, 0, 9999, 9999),
    DOT_HEAL("Spell.DotHeal", "DotHeal", "도트 힐", false, null,
            1, 9999, 0, 0, 9999, 9999),
    RANGE_HEAL("Spell.RangeHeal", "RangeHeal", "범위 힐", false, null,
            1, 9999, 0, 0, 9999, 9999),
    RANGE_DOT_HEAL("Spell.RangeDotHeal", "RangeDotHeal", "범위 도트 힐", false, null,
            1, 9999, 0, 0, 9999, 9999),
    ATTACK_AURA("Spell.AttackAura", "AttackAura", "공격 오라", false, null,
            1, 9999, 0, 0, 9999, 9999)
    ;

    private final String path;
    private final String key;
    private String displayName;
    private boolean isMythicSkill;
    private String mythicSkillInternalName;
    private int maxLevel;
    private double coolDown;
    private double value;
    private double levelPerValue;
    private int requiredKillPoint;
    private double mana;

    SpellAbility(String path, String key, String displayName, boolean isMythicSkill, String mythicSkillInternalName, int maxLevel, double coolDown, double value, double levelPerValue, int requiredKillPoint, double mana) {
        this.path = path;
        this.key = key;
        this.displayName = displayName;
        this.isMythicSkill = isMythicSkill;
        this.mythicSkillInternalName = mythicSkillInternalName;
        this.maxLevel = maxLevel;
        this.coolDown = coolDown;
        this.value = value;
        this.levelPerValue = levelPerValue;
        this.requiredKillPoint = requiredKillPoint;
        this.mana = mana;
    }

    public String getPath() {
        return path;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        displayName = ChatColor.translateAlternateColorCodes('&', displayName);
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isMythicSkill() {
        return isMythicSkill;
    }

    public void setMythicSkill(boolean mythicSkill) {
        isMythicSkill = mythicSkill;
    }

    public String getMythicSkillInternalName() {
        return mythicSkillInternalName;
    }

    public void setMythicSkillInternalName(String mythicSkillInternalName) {
        this.mythicSkillInternalName = mythicSkillInternalName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public double getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(double coolDown) {
        this.coolDown = coolDown;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getLevelPerValue() {
        return levelPerValue;
    }

    public void setLevelPerValue(double levelPerValue) {
        this.levelPerValue = levelPerValue;
    }

    public int getRequiredKillPoint() {
        return requiredKillPoint;
    }

    public void setRequiredKillPoint(int requiredKillPoint) {
        this.requiredKillPoint = requiredKillPoint;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        for (String s : VaultHuntersIntegrated.spellAbilityDataFile.getConfig().getStringList(path + ".Lore")) {
            s = s.replace("<value>", String.valueOf(value));
            s = s.replace("<level_per_value>", String.valueOf(levelPerValue));
            s = s.replace("<cooldown>", String.valueOf(coolDown));
            s = s.replace("<max_level>", String.valueOf(maxLevel));
            s = s.replace("<required_killpoint>", String.valueOf(requiredKillPoint));
            s = s.replace("<mana>", String.valueOf(mana));
            s = ChatColor.translateAlternateColorCodes('&', s);
            lore.add(s);
        }
        return lore;
    }

}

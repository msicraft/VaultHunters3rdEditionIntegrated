package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.File.PlayerDataFile;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Task.BuffDurationTask;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Task.DisplaySpellAbilityTask;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.SpellAbilityUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerSpellAbility {

    private final Player player;
    private boolean isCastingMode = false;

    private final Map<SpellAbility, Buff> buffMap = new HashMap<>();

    private final Map<Integer, SpellAbility> bindSpellAbilityMap = new HashMap<>(9); // 0~8, spellAbility
    private final Map<SpellAbility, Long> spellAbilityCoolDownMap = new HashMap<>();

    private final Map<SpellAbility, Integer> purchasedSpellAbilityMap = new HashMap<>();

    public PlayerSpellAbility(Player player, PlayerDataFile playerDataFile) {
        this.player = player;
        this.isCastingMode = false;
        bindSpellAbility(playerDataFile);
        setPurchasedSpellAbilityMap(playerDataFile);
        new DisplaySpellAbilityTask(player).runTaskTimer(VaultHuntersIntegrated.getPlugin(), 0L, 10L);
        new BuffDurationTask(player).runTaskTimer(VaultHuntersIntegrated.getPlugin(), 0L, 20L);
    }

    private void bindSpellAbility(PlayerDataFile playerDataFile) {
        if (!bindSpellAbilityMap.isEmpty()) {
            bindSpellAbilityMap.clear();
        }
        for (int a = 0; a<9; a++) {
            String spellKey = playerDataFile.getConfig().contains("SpellAbility.Bind." + a + ".SpellKey") ? playerDataFile.getConfig().getString("SpellAbility.Bind." + a + ".SpellKey") : null;
            if (spellKey != null) {
                long lastUseTime = playerDataFile.getConfig().getLong("SpellAbility.Bind." + a + ".LastUseTime");
                SpellAbility spellAbility = SpellAbilityUtil.getSpellAbility(spellKey);
                bindSpellAbilityMap.put(a, spellAbility);
                if (spellAbility != null) {
                    spellAbilityCoolDownMap.put(spellAbility, lastUseTime);
                }
            } else {
                bindSpellAbilityMap.put(a, null);
            }
        }
    }

    public void setBindSpell(int slot, SpellAbility spellAbility) {
        bindSpellAbilityMap.put(slot, spellAbility);
    }

    private void setPurchasedSpellAbilityMap(PlayerDataFile playerDataFile) {
        if (!purchasedSpellAbilityMap.isEmpty()) {
            purchasedSpellAbilityMap.clear();
        }
        for (String s : playerDataFile.getConfig().getStringList("SpellAbility.PurchasedSpellAbilityList")) {
            try {
                String[] a = s.split(":");
                SpellAbility spellAbility = SpellAbilityUtil.getSpellAbility(a[0]);
                if (spellAbility != null) {
                    int level = Integer.parseInt(a[1]);
                    purchasedSpellAbilityMap.put(spellAbility, level);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean hasSpellAbility(SpellAbility spellAbility) {
        return purchasedSpellAbilityMap.containsKey(spellAbility);
    }

    public int getSpellAbilityLevel(SpellAbility spellAbility) {
        if (purchasedSpellAbilityMap.containsKey(spellAbility)) {
            return purchasedSpellAbilityMap.get(spellAbility);
        }
        return 0;
    }

    public void setSpellAbilityLevel(SpellAbility spellAbility, int level) {
        purchasedSpellAbilityMap.put(spellAbility, level);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCastingMode() {
        return isCastingMode;
    }

    public Map<Integer, SpellAbility> getBindSpellAbilityMap() {
        return bindSpellAbilityMap;
    }

    public Map<SpellAbility, Long> getSpellAbilityCoolDownMap() {
        return spellAbilityCoolDownMap;
    }

    public Map<SpellAbility, Integer> getPurchasedSpellAbilityMap() {
        return purchasedSpellAbilityMap;
    }

    public int getSpellCount() {
        return purchasedSpellAbilityMap.size();
    }

    public void setCastingMode(boolean castingMode) {
        isCastingMode = castingMode;
    }

    public SpellAbility getSpellAbility(int slot) {
        return bindSpellAbilityMap.get(slot);
    }

    public boolean isCoolDown(SpellAbility spellAbility) {
        if (spellAbilityCoolDownMap.containsKey(spellAbility)) {
            return spellAbilityCoolDownMap.get(spellAbility) > System.currentTimeMillis();
        }
        return false;
    }

    public void setCoolDown(SpellAbility spellAbility, long time) {
        spellAbilityCoolDownMap.put(spellAbility, time);
    }

    public double getLeftCoolDown(SpellAbility spellAbility) {
        if (spellAbilityCoolDownMap.containsKey(spellAbility)) {
            double left = (spellAbilityCoolDownMap.get(spellAbility) - System.currentTimeMillis()) / 1000.0;
            return Math.round(left * 10.0) / 10.0;
        }
        return 0;
    }

    public boolean hasBuff(SpellAbility spellAbility) {
        return buffMap.containsKey(spellAbility);
    }

    public void addBuff(SpellAbility spellAbility, int seconds, String buffName) {
        buffMap.put(spellAbility, new Buff(spellAbility, seconds, buffName));
    }

    public void removeBuff(SpellAbility spellAbility) {
        buffMap.remove(spellAbility);
    }

    public Buff getBuff(SpellAbility spellAbility) {
        if (hasBuff(spellAbility)) {
            return buffMap.get(spellAbility);
        }
        return null;
    }

    public Set<SpellAbility> getBuffList() {
        return buffMap.keySet();
    }

}

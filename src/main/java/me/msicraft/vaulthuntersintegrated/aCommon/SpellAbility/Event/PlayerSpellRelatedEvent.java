package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Event;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerSpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerStats;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.SpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.SpellAbilityUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerSpellRelatedEvent implements Listener {

    public static void reloadVariables() {
        castingDelay = VaultHuntersIntegrated.getPlugin().getConfig().contains("SpellAbility.CastingDelay") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("SpellAbility.CastingDelay") : 0;
    }

    @EventHandler
    public void onChangeCastingMode(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        if (player.isSneaking()) {
            e.setCancelled(true);
            PlayerSpellAbility playerSpellAbility = PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility();
            playerSpellAbility.setCastingMode(!playerSpellAbility.isCastingMode());
        }
    }

    private final Map<UUID, Long> castingDelayMap = new HashMap<>();
    private static double castingDelay = 0;

    @EventHandler
    public void playerCastingSpell(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        PlayerSpellAbility playerSpellAbility = PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility();
        if (playerSpellAbility.isCastingMode()) {
            e.setCancelled(true);
            if (castingDelayMap.containsKey(player.getUniqueId())) {
                if (castingDelayMap.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    return;
                }
            }
            long delayTime = (long) (System.currentTimeMillis() + (castingDelay * 1000L));
            castingDelayMap.put(player.getUniqueId(), delayTime);
            int selectSlot = e.getNewSlot();
            SpellAbility spellAbility = playerSpellAbility.getSpellAbility(selectSlot);
            if (spellAbility != null) {
                if (!playerSpellAbility.isCoolDown(spellAbility)) {
                    PlayerStats playerStats = PlayerDataUtil.getPlayerData(player).getPlayerStats();
                    double requiredMana = spellAbility.getMana();
                    if (playerStats.hasEnoughMana(requiredMana)) {
                        playerStats.addMana(-requiredMana);
                        double coolDown = spellAbility.getCoolDown();
                        long time = (long) (System.currentTimeMillis() + (coolDown * 1000L));
                        playerSpellAbility.setCoolDown(spellAbility, time);
                        SpellAbilityUtil.castSpell(player, spellAbility);
                    } else {
                        player.sendMessage(ChatColor.RED + "충분한 마나가 없습니다.");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerMeleeAttackEvent(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (damager instanceof Player player) {
            if (e.getEntity() instanceof LivingEntity livingEntity) {
                if (livingEntity.getType() == EntityType.PLAYER) {
                    return;
                }
                PlayerSpellAbility playerSpellAbility = PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility();
                Set<SpellAbility> buffSets = playerSpellAbility.getBuffList();
                double damage = e.getDamage();
                for (SpellAbility spellAbility : buffSets) {
                    int spellLevel = playerSpellAbility.getSpellAbilityLevel(spellAbility);
                    spellLevel = spellLevel - 1;
                    switch (spellAbility) {
                        case ATTACK_AURA -> {
                            double extraDamageP = spellAbility.getValue() / 100.0;
                            double extraLevelPer = spellAbility.getLevelPerValue() / 100.0;
                            double extra = extraDamageP + (extraLevelPer * spellLevel);
                            damage = damage + (damage * extra);
                        }
                    }
                }
                e.setDamage(damage);
            }
        }
    }

}

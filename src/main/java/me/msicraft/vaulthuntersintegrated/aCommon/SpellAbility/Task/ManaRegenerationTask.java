package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Task;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerStats;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ManaRegenerationTask extends BukkitRunnable {

    private final Player player;

    public ManaRegenerationTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            if (!EntityUtil.isDeadEntity(player)) {
                PlayerStats playerStats = PlayerDataUtil.getPlayerData(player).getPlayerStats();
                double currentMana = playerStats.getMana();
                double maxMana = playerStats.getMaxMana();
                double manaRegen = playerStats.getManaRegen();
                double nextMana = currentMana + manaRegen;
                if (nextMana > maxMana) {
                    nextMana = maxMana;
                }
                playerStats.setMana(nextMana);
            }
        } else {
            if (Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId())) {
                if (VaultHuntersIntegrated.isDebug) {
                    Bukkit.getConsoleSender().sendMessage("마나 리젠 스케쥴러 취소: " + player.getName() + " | TaskId: " + getTaskId());
                }
                cancel();
            }
        }
    }
}

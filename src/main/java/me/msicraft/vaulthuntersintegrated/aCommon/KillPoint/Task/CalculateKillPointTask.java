package me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.Task;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CalculateKillPointTask extends BukkitRunnable {

    private final Player player;

    public CalculateKillPointTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            int requiredKillPointExp = KillPointUtil.getRequiredKillPointExp();
            if (requiredKillPointExp > 0) {
                double currentExp = KillPointUtil.getKillPointExp(player);
                if (currentExp >= requiredKillPointExp) {
                    int restValue = (int) (currentExp / requiredKillPointExp);
                    double cal = currentExp - (requiredKillPointExp * restValue);
                    KillPointUtil.addKillPoint(player, restValue);
                    KillPointUtil.setKillPointExp(player, cal);
                }
            }
        } else {
            if (Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId())) {
                if (VaultHuntersIntegrated.isDebug) {
                    Bukkit.getConsoleSender().sendMessage("킬 포인트 스케쥴러 취소: " + player.getName() + " | TaskId: " + getTaskId());
                }
                cancel();
            }
        }
    }

}

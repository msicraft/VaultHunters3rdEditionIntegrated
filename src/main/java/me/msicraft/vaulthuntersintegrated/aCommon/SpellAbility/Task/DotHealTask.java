package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Task;

import me.msicraft.vaulthuntersintegrated.aCommon.Util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DotHealTask extends BukkitRunnable {

    private final Player player;

    private final double perHealAmount;
    private double counter = 0;

    public DotHealTask(Player player, double totalAmount, int duration) {
        this.player = player;
        perHealAmount = totalAmount / (duration/20.0);
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            if (EntityUtil.isDeadEntity(player)) {
                cancel();
            } else {
                if (counter >= perHealAmount) {
                    cancel();
                    return;
                }
                EntityUtil.healLivingEntity(player, perHealAmount);
                counter = counter + perHealAmount;
            }
        } else {
            if (Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId())) {
                cancel();
            }
        }
    }
}

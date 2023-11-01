package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Task;

import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Buff;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerSpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.SpellAbility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BuffDurationTask extends BukkitRunnable {

    private final Player player;

    private final Map<Buff, BossBar> buffBossBarMap = new HashMap<>();

    public BuffDurationTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            PlayerSpellAbility playerSpellAbility = PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility();
            Set<SpellAbility> buffSets = Set.copyOf(playerSpellAbility.getBuffList());
            for (SpellAbility spellAbility : buffSets) {
                if (!playerSpellAbility.hasBuff(spellAbility)) {
                    playerSpellAbility.removeBuff(spellAbility);
                    continue;
                }
                Buff buff = playerSpellAbility.getBuff(spellAbility);
                if (buff != null) {
                    long currentTime = System.currentTimeMillis();
                    long endTime = buff.getEndTime();
                    BossBar bossBar = getBuffBar(buff);
                    if (endTime >= currentTime) {
                        if (!bossBar.isVisible()) {
                            bossBar.setVisible(true);
                        }
                        double left = (endTime - currentTime) / 1000.0;
                        double leftRate = left / buff.getSeconds();
                        if (leftRate < 0) {
                            leftRate = 0;
                        }
                        bossBar.setProgress(leftRate);
                        continue;
                    }
                    bossBar.removeAll();
                    buffBossBarMap.remove(buff);
                    playerSpellAbility.removeBuff(spellAbility);
                } else {
                    playerSpellAbility.removeBuff(spellAbility);
                }
            }
        } else {
            cancel();
        }
    }

    private BossBar getBuffBar(Buff buff) {
        BossBar bossBar;
        if (!buffBossBarMap.containsKey(buff)) {
            bossBar = Bukkit.createBossBar(ChatColor.BOLD + "" + buff.getBuffName(), BarColor.WHITE, BarStyle.SEGMENTED_20);
            buffBossBarMap.put(buff, bossBar);
            bossBar.addPlayer(player);
            bossBar.setVisible(true);
        } else {
            bossBar = buffBossBarMap.get(buff);
        }
        return bossBar;
    }

}

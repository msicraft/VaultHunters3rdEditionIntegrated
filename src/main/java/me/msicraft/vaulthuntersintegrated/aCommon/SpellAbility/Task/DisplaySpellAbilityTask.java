package me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Task;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerSpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.SpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.SpellAbilityUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.EntityUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class DisplaySpellAbilityTask extends BukkitRunnable {

    private final Player player;

    public DisplaySpellAbilityTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            if (!EntityUtil.isDeadEntity(player)) {
                PlayerSpellAbility playerSpellAbility = PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility();
                if (playerSpellAbility.isCastingMode()) {
                    Map<Integer, SpellAbility> bindMap = playerSpellAbility.getBindSpellAbilityMap();
                    StringBuilder message = new StringBuilder("");
                    int maxBindSize = SpellAbilityUtil.getMaxBindSpellSize();
                    int bindCount = 0;
                    for (int a : bindMap.keySet()) {
                        if (bindCount >= maxBindSize) {
                            break;
                        }
                        SpellAbility spellAbility = bindMap.get(a);
                        if (spellAbility != null) {
                            if (playerSpellAbility.isCoolDown(spellAbility)) {
                                double leftCoolDown = playerSpellAbility.getLeftCoolDown(spellAbility);
                                message.append("§c§o§l| [").append((a+1)).append("] ").append(spellAbility.getDisplayName()).append("§c§o§l(§5").append(leftCoolDown).append("§c) | ");
                            } else {
                                message.append("§a§o§l| [").append((a+1)).append("] ").append(spellAbility.getDisplayName()).
                                        append("(§3§o§l").append(spellAbility.getMana()).append("§a§o§l) | ");
                            }
                            bindCount++;
                        }
                    }
                    if (!message.isEmpty()) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message.toString()));
                    }
                }
            }
        } else {
            if (Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId())) {
                if (VaultHuntersIntegrated.isDebug) {
                    Bukkit.getConsoleSender().sendMessage("스펠 표시 스케쥴러 취소: " + player.getName() + " | TaskId: " + getTaskId());
                }
                cancel();
            }
        }
    }
}

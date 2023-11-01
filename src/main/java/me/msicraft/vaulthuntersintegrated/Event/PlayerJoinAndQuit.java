package me.msicraft.vaulthuntersintegrated.Event;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.Task.CalculateKillPointTask;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerJoinAndQuit implements Listener {

    private static final Map<UUID, List<Integer>> playerActiveTasks = new HashMap<>();

    public static boolean containActiveTaskMap(Player player) { return playerActiveTasks.containsKey(player.getUniqueId()); }
    private static List<Integer> getActiveTasks(Player player) { return playerActiveTasks.get(player.getUniqueId()); }
    public static void removeActiveTaskMap(Player player) { playerActiveTasks.remove(player.getUniqueId()); }

    public static void addPlayerTask(Player player, int taskId) {
        List<Integer> tasks = new ArrayList<>();
        if (containActiveTaskMap(player)) {
            tasks.addAll(getActiveTasks(player));
        }
        tasks.add(taskId);
        playerActiveTasks.put(player.getUniqueId(), tasks);
    }

    private void unRegisterPlayerTask(Player player) {
        List<Integer> tasks = new ArrayList<>();
        if (containActiveTaskMap(player)) {
            tasks = getActiveTasks(player);
        }
        for (int id : tasks) {
            Bukkit.getScheduler().cancelTask(id);
        }
        if (VaultHuntersIntegrated.isDebug) {
            Bukkit.getConsoleSender().sendMessage("작업 취소됨: " + player.getName() + " | TaskId: " + tasks);
        }
        removeActiveTaskMap(player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerDataUtil.registerPlayerData(player);
        BukkitTask playerKillPointCalTask = new CalculateKillPointTask(player).runTaskTimer(VaultHuntersIntegrated.getPlugin(), 20L, 1200L);
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
        }
        if (VaultHuntersIntegrated.isDebug) {
            Bukkit.getConsoleSender().sendMessage("플레이어 킬 포인트 작업 등록됨: " + player.getName() + " | TaskId: " + playerKillPointCalTask.getTaskId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        PlayerDataUtil.unregisterPlayerData(player);
        unRegisterPlayerTask(player);
    }

}

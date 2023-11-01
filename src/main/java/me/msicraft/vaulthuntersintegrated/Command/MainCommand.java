package me.msicraft.vaulthuntersintegrated.Command;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.MainMenu.MainGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("vaulthuntersintegrated")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/vaulthuntersintegrated help");
            }
            if (args.length >= 1) {
                String var = args[0];
                if (var != null) {
                    switch (var) {
                        case "test" -> {
                            if (sender instanceof Player player) {
                                player.sendMessage("test: " + player.getWorld() + " | " + player.getWorld().getName());
                                player.sendMessage("test2: " + player.getWorld().getKey());
                            }
                        }
                        case "help" -> {
                            if (args.length == 1) {
                                sender.sendMessage(ChatColor.YELLOW + "/vaulthuntersintegrated help : " + ChatColor.WHITE + "List of plugin commands");
                                sender.sendMessage(ChatColor.YELLOW + "/vaulthuntersintegrated reload : " + ChatColor.WHITE + "Reload the plugin config files");
                                return true;
                            }
                        }
                        case "reload" -> {
                            if (args.length == 1) {
                                VaultHuntersIntegrated.getPlugin().reloadDataFiles();
                                sender.sendMessage(VaultHuntersIntegrated.getPrefix() + ChatColor.GREEN + " Plugin config files reloaded");
                                return true;
                            }
                        }
                        case "getbukkittask" -> {
                            if (args.length == 1 && sender.hasPermission("vaulthuntersintegrated.command.getbukkittask")) {
                                List<BukkitTask> activeTasks = Bukkit.getScheduler().getPendingTasks();
                                if (activeTasks.isEmpty()) {
                                    sender.sendMessage(ChatColor.RED + "등록된 스케쥴러 없음");
                                } else {
                                    sender.sendMessage(ChatColor.YELLOW + "========================================");
                                    sender.sendMessage(ChatColor.GREEN + "스케쥴러 목록");
                                    for (BukkitTask bukkitTask : activeTasks) {
                                        sender.sendMessage("Plugin: " + bukkitTask.getOwner() + " | TaskId: " + bukkitTask.getTaskId());
                                    }
                                    sender.sendMessage(ChatColor.YELLOW + "========================================");
                                }
                            }
                        }
                        case "menu" -> {
                            if (args.length == 1) {
                                if (sender instanceof Player player) {
                                    MainGui mainGui = new MainGui(player);
                                    player.openInventory(mainGui.getInventory());
                                    mainGui.setUpMainMenu(player);
                                }
                            }
                        }
                        case "setspawn" -> { //vhi setspawn
                            if (sender.hasPermission("vaulthuntersintegrated.command.setspawn")) {
                                if (sender instanceof Player player) {
                                    try {
                                        String var2 = args[1];
                                        if (var2.equals("null")) {
                                            VaultHuntersIntegrated.getPlugin().getConfig().set("SpawnLocation", "");
                                            VaultHuntersIntegrated.getPlugin().saveConfig();
                                            player.sendMessage(ChatColor.GREEN + "스폰 위치가 초기화 되었습니다.");
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        Location location = player.getLocation();
                                        String s = player.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
                                        VaultHuntersIntegrated.getPlugin().getConfig().set("SpawnLocation", s);
                                        VaultHuntersIntegrated.getPlugin().saveConfig();
                                        player.sendMessage(ChatColor.GREEN + "스폰 위치가 변경되었습니다.");
                                        player.sendMessage(ChatColor.GREEN + "월드: " + player.getWorld().getName());
                                        player.sendMessage(ChatColor.GREEN + "X: " + location.getX() + " | Y: " + location.getY() + " | Z: " + location.getZ());
                                        player.getWorld().setSpawnLocation(location);
                                    }
                                }
                            }
                        }
                        case "killpoint" -> { //vhi killpoint [point, exp] [get, set, add] <player> [value]
                            if (sender.hasPermission("vaulthuntersintegrated.command.killpoint")) {
                                try {
                                    String type = args[1];
                                    String type2 = args[2];
                                    Player target = Bukkit.getPlayer(args[3]);
                                    if (target == null) {
                                        sender.sendMessage(ChatColor.RED + "플레이어가 온라인이 아닙니다");
                                        return false;
                                    }
                                    int value;
                                    switch (type) {
                                        case "point" -> {
                                            switch (type2) {
                                                case "get" -> {
                                                    sender.sendMessage(ChatColor.GREEN + "플레이어: " + ChatColor.GRAY + target.getName());
                                                    sender.sendMessage(ChatColor.GREEN + "킬 포인트: " + ChatColor.GRAY + KillPointUtil.getKillPoint(target));
                                                }
                                                case "set" -> {
                                                    value = Integer.parseInt(args[4]);
                                                    sender.sendMessage(ChatColor.GREEN + "플레이어: " + ChatColor.GRAY + target.getName());
                                                    sender.sendMessage(ChatColor.GREEN + "Set killPoint: " + ChatColor.GRAY + value);
                                                    KillPointUtil.setKillPoint(target, value);
                                                }
                                                case "add" -> {
                                                    value = Integer.parseInt(args[4]);
                                                    sender.sendMessage(ChatColor.GREEN + "플레이어: " + ChatColor.GRAY + target.getName());
                                                    sender.sendMessage(ChatColor.GREEN + "Before KillPoint: " + ChatColor.GRAY + KillPointUtil.getKillPoint(target));
                                                    KillPointUtil.addKillPoint(target, value);
                                                    sender.sendMessage(ChatColor.GREEN + "After KillPoint: " + ChatColor.GRAY + KillPointUtil.getKillPoint(target));
                                                }
                                            }
                                        }
                                        case "exp" -> {
                                            switch (type2) {
                                                case "get" -> {
                                                    sender.sendMessage(ChatColor.GREEN + "플레이어: " + ChatColor.GRAY + target.getName());
                                                    sender.sendMessage(ChatColor.GREEN + "킬 포인트: " + ChatColor.GRAY + KillPointUtil.getKillPointExp(target));
                                                }
                                                case "set" -> {
                                                    value = Integer.parseInt(args[4]);
                                                    sender.sendMessage(ChatColor.GREEN + "플레이어: " + ChatColor.GRAY + target.getName());
                                                    sender.sendMessage(ChatColor.GREEN + "Set killPointExp: " + ChatColor.GRAY + value);
                                                    KillPointUtil.setKillPointExp(target, value);
                                                }
                                                case "add" -> {
                                                    value = Integer.parseInt(args[4]);
                                                    sender.sendMessage(ChatColor.GREEN + "플레이어: " + ChatColor.GRAY + target.getName());
                                                    sender.sendMessage(ChatColor.GREEN + "Before KillPointExp: " + ChatColor.GRAY + KillPointUtil.getKillPointExp(target));
                                                    KillPointUtil.setKillPointExp(target, value);
                                                    sender.sendMessage(ChatColor.GREEN + "After KillPointExp: " + ChatColor.GRAY + KillPointUtil.getKillPointExp(target));
                                                }
                                            }
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    sender.sendMessage(ChatColor.RED + "/vhi killpoint [point, exp] [get, set, add] <player> [value]");
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}

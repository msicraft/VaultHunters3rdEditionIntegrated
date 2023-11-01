package me.msicraft.vaulthuntersintegrated.aCommon.KillPoint;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerData;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.MathUtil;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KillPointUtil {

    private static String killPointExpEquations = null;
    private static double perEntityMaxExpValue = 0;
    private static Expression expression = null;
    private static double killPointExpSpread = 0;
    private static int requiredKillPointExp = 1000;

    public static String getKillPointExpEquations() { return killPointExpEquations; }
    public static int getRequiredKillPointExp() { return requiredKillPointExp; }

    public static void reloadVariables() {
        killPointExpEquations = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.KillPointExpEquations") ? VaultHuntersIntegrated.getPlugin().getConfig().getString("KillPointSetting.KillPointExpEquations") : null;
        perEntityMaxExpValue = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.PerEntity-MaxExpValue") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("KillPointSetting.PerEntity-MaxExpValue") : 1000;
        if (killPointExpEquations == null) {
            killPointExpEquations = "((H*0.25)+(D*2)+(A*1.25)+(AT*1.75))";
        }
        try {
            expression = new ExpressionBuilder(killPointExpEquations).variables("H", "D", "A", "AT").build();
        } catch (IllegalArgumentException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "잘못된 표현식 발견: " + ChatColor.GREEN + killPointExpEquations);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "기본 표현식이 사용됩니다. " + "((H*0.25)+(D*2)+(A*1.25)+(AT*1.75))");
            killPointExpEquations = "((H*0.25)+(D*2)+(A*1.25)+(AT*1.75))";
        }
        killPointExpSpread = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.KillPointExpSpread") ? VaultHuntersIntegrated.getPlugin().getConfig().getDouble("KillPointSetting.KillPointExpSpread") : 0;
        requiredKillPointExp = VaultHuntersIntegrated.getPlugin().getConfig().contains("KillPointSetting.Required-Exp") ? VaultHuntersIntegrated.getPlugin().getConfig().getInt("KillPointSetting.Required-Exp") : 1000;
    }

    public static int getKillPointNextLevelToExpPercent(Player player) { // (xx)%
        double cal = KillPointUtil.getKillPointExp(player) / KillPointUtil.getRequiredKillPointExp() * 100.0;
        return (int) (Math.round(cal * 100.0) / 100.0);
    }

    public static double calculateEntityKillPointExp(double health, double damage, double armor, double armorToughness) {
        double exp = 0;
        if (killPointExpEquations != null && expression != null) {
            double v;
            v = expression.setVariable("H", health).setVariable("D", damage).setVariable("A", armor).setVariable("AT", armorToughness).evaluate();
            v = Math.round(v * 1000.0) / 1000.0;
            double spreadV = v * killPointExpSpread;
            double max = v + spreadV;
            double min = v - spreadV;
            if (min < 0) {
                min = 0;
            }
            double randomExp = MathUtil.getRandomValueDouble(max, min);
            if (randomExp > perEntityMaxExpValue) {
                randomExp = perEntityMaxExpValue;
            }
            exp = randomExp;
            if (VaultHuntersIntegrated.isDebug) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "랜덤 킬 포인트 경험치 계산");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "범위: " + killPointExpSpread + "| 최대: " + max + "| 최소: " + min + "| 값: " + exp);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
            }
        } else {
            if (VaultHuntersIntegrated.isDebug) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "KillPointExpEquations: " + ChatColor.GRAY + killPointExpEquations);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Expression: " + ChatColor.GRAY + expression);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
            }
        }
        return exp;
    }

    public static boolean hasEnoughKillPoint(Player player, int requiredKillPoint) {
        int currentPoint = getKillPoint(player);
        return currentPoint >= requiredKillPoint;
    }

    public static int getKillPoint(Player player) {
        return PlayerDataUtil.getPlayerData(player).getKillPoint();
    }

    public static void setKillPoint(Player player, int point) {
        PlayerData playerData = PlayerDataUtil.getPlayerData(player);
        playerData.setKillPoint(point);
    }

    public static void addKillPoint(Player player, int amount) {
        PlayerData playerData = PlayerDataUtil.getPlayerData(player);
        int current = playerData.getKillPoint();
        int cal = current + amount;
        if (cal < 0) {
            cal = 0;
        }
        playerData.setKillPoint(cal);
    }

    public static double getKillPointExp(Player player) {
        return PlayerDataUtil.getPlayerData(player).getKillPointExp();
    }

    public static void setKillPointExp(Player player, double exp) {
        PlayerData playerData = PlayerDataUtil.getPlayerData(player);
        playerData.setKillPointExp(exp);
    }

    public static void addKillPointExp(Player player, double exp) {
        PlayerData playerData = PlayerDataUtil.getPlayerData(player);
        double current = playerData.getKillPointExp();
        double cal = current + exp;
        if (cal < 0) {
            cal = 0;
        }
        playerData.setKillPointExp(cal);
    }


}

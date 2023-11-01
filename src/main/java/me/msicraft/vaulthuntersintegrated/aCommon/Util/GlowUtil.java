package me.msicraft.vaulthuntersintegrated.aCommon.Util;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.regex.Pattern;

public class GlowUtil {

    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private static final Scoreboard SCOREBOARD = Bukkit.getScoreboardManager() != null ? Bukkit.getScoreboardManager().getMainScoreboard() : null;

    public enum GlowTeam {
        AQUA("VHI_AQUA", ChatColor.AQUA), BLACK("VHI_BLACK", ChatColor.BLACK), BLUE("VHI_BLUE", ChatColor.BLUE),
        DARK_AQUA("VHI_DARK_AQUA", ChatColor.DARK_AQUA), DARK_BLUE("VHI_DARK_BLUE", ChatColor.DARK_BLUE),
        DARK_GRAY("VHI_DARK_GRAY", ChatColor.DARK_GRAY), DARK_GREEN("VHI_DARK_GREEN", ChatColor.DARK_GREEN),
        DARK_PURPLE("VHI_DARK_PURPLE", ChatColor.DARK_PURPLE), DARK_RED("VHI_DARK_RED", ChatColor.DARK_RED),
        GOLD("VHI_GOLD", ChatColor.GOLD), GRAY("VHI_GRAY", ChatColor.GRAY), GREEN("VHI_GREEN", ChatColor.GREEN),
        LIGHT_PURPLE("VHI_LIGHT_PURPLE", ChatColor.LIGHT_PURPLE), RED("VHI_RED", ChatColor.RED), WHITE("VHI_WHITE", ChatColor.WHITE),
        YELLOW("VHI_YELLOW", ChatColor.YELLOW)
        ;

        private final String name;
        private final ChatColor color;
        public String getName() {
            return name;
        }
        public ChatColor getColor() {
            return color;
        }
        GlowTeam(String name, ChatColor color) {
            this.name = name;
            this.color = color;
        }
    }

    public static void register() {
        if (SCOREBOARD != null) {
            for (GlowTeam glowTeam : GlowTeam.values()) {
                Team team = SCOREBOARD.registerNewTeam(glowTeam.getName());
                team.setColor(glowTeam.getColor());
                team.setPrefix(ChatColor.WHITE + "");
            }
        }
    }

    public static void unregister() {
        if (SCOREBOARD != null) {
            for (GlowTeam glowTeam : GlowTeam.values()) {
                Team team = SCOREBOARD.getTeam(glowTeam.getName());
                if (team != null) {
                    team.unregister();
                }
            }
        }
    }

    public static boolean hasGlow(Entity entity) {
        String name;
        if (entity instanceof Player player) {
            name = player.getName();
        } else {
            name = entity.getUniqueId().toString();
        }
        if (name != null) {
            Team team = SCOREBOARD.getEntryTeam(name);
            return team != null;
        }
        return false;
    }

    public static boolean hasGlowTeam(Entity entity, GlowTeam glowTeam) {
        Team team = SCOREBOARD.getTeam(glowTeam.getName());
        if (team != null) {
            String name;
            if (entity instanceof Player player) {
                name = player.getName();
            } else if (entity instanceof LivingEntity livingEntity) {
                name = livingEntity.getUniqueId().toString();
            } else {
                name = entity.getUniqueId().toString();
            }
            if (name != null) {
                return team.hasEntry(name);
            }
        }
        return false;
    }

    public static void applyGlow(GlowTeam glowTeam, Entity entity) {
        Team team = SCOREBOARD.getTeam(glowTeam.getName());
        if (team != null) {
            String name;
            if (entity instanceof Player player) {
                name = player.getName();
            } else {
                name = entity.getUniqueId().toString();
            }
            if (name != null) {
                team.addEntry(name);
                entity.setGlowing(true);
            }
        }
    }

    public static void applyGlow(GlowTeam glowTeam, Entity entity, long durationTick) {
        Team team = SCOREBOARD.getTeam(glowTeam.getName());
        if (team != null) {
            String name;
            if (entity instanceof Player player) {
                name = player.getName();
            } else if (entity instanceof LivingEntity livingEntity) {
                name = livingEntity.getUniqueId().toString();
            } else {
                name = entity.getUniqueId().toString();
            }
            team.addEntry(name);
            entity.setGlowing(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    removeGlow(glowTeam, entity);
                }
            }.runTaskLater(VaultHuntersIntegrated.getPlugin(), durationTick);
        }
    }

    public static void removeGlow(GlowTeam glowTeam, Entity entity) {
        Team team = SCOREBOARD.getTeam(glowTeam.getName());
        if (team != null) {
            String name;
            if (entity instanceof Player player) {
                name = player.getName();
            } else if (entity instanceof LivingEntity livingEntity) {
                name = livingEntity.getUniqueId().toString();
            } else {
                name = entity.getUniqueId().toString();
            }
            if (team.hasEntry(name)) {
                entity.setGlowing(false);
                team.removeEntry(name);
            }
        }
    }

}

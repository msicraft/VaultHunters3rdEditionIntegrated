package me.msicraft.vaulthuntersintegrated;

import me.msicraft.vaulthuntersintegrated.Command.MainCommand;
import me.msicraft.vaulthuntersintegrated.Command.MainTabComplete;
import me.msicraft.vaulthuntersintegrated.Event.PlayerJoinAndQuit;
import me.msicraft.vaulthuntersintegrated.Event.PlayerRelatedEvent;
import me.msicraft.vaulthuntersintegrated.Event.WorldRelatedEvent;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.Event.PlayerKillPointRelatedEvent;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.MainMenu.Event.MainMenuChatEditEvent;
import me.msicraft.vaulthuntersintegrated.aCommon.MainMenu.Event.MainMenuEvent;
import me.msicraft.vaulthuntersintegrated.aCommon.PlaceholderAPI.VHIPlaceholder;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Event.PlayerSpellRelatedEvent;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.File.SpellAbilityDataFile;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.SpellAbilityUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.GlowUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class VaultHuntersIntegrated extends JavaPlugin {

    private static VaultHuntersIntegrated plugin;

    public static VaultHuntersIntegrated getPlugin() {
        return plugin;
    }

    public static String getPrefix() {
        return ChatColor.GREEN + "[Vault Hunters 3rd Integrated]";
    }

    public static ItemStack airStack;
    public static boolean isDebug = false;
    private boolean isEnabledGlowing = false;
    private GlowUtil.GlowTeam glowTeam = GlowUtil.GlowTeam.WHITE;

    public static SpellAbilityDataFile spellAbilityDataFile;

    @Override
    public void onEnable() {
        plugin = this;
        airStack = new ItemStack(Material.AIR, 1);
        spellAbilityDataFile = new SpellAbilityDataFile(this);
        createConfigFiles();
        final int configVersion = plugin.getConfig().contains("config-version", true) ? plugin.getConfig().getInt("config-version") : -1;
        if (configVersion != 1) {
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getPrefix() + ChatColor.RED + " You are using the old config");
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getPrefix() + ChatColor.RED + " Created the latest config.yml after replacing the old config.yml with config_old.yml");
            replaceConfig();
            createConfigFiles();
        } else {
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getPrefix() + " You are using the latest version of config.yml");
        }
        reloadDataFiles();
        eventRegister();
        commandsRegister();
        SpellAbilityUtil.initialize();
        GlowUtil.register();
        BukkitTask tabListTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.isOnline()) {
                        World world = player.getWorld();
                        String worldName = WorldUtil.getWorldName(world, true);
                        player.setPlayerListName(ChatColor.WHITE + player.getName() +
                                " (" + ChatColor.AQUA + worldName + " | " + WorldUtil.getWorldTimeTo24Format(world.getTime()) +
                                ChatColor.WHITE + ")");
                        if (isEnabledGlowing) {
                            if (!GlowUtil.hasGlowTeam(player, glowTeam)) {
                                GlowUtil.applyGlow(glowTeam, player);
                            }
                            if (!player.isGlowing()) {
                                player.setGlowing(true);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 200L);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getConsoleSender().sendMessage("Detect PlaceHolderAPI plugin");
            new VHIPlaceholder(this).register();
        }
        if (isDebug) {
            Bukkit.getConsoleSender().sendMessage("탭 리스트 스케쥴러 등록: " + tabListTask.getTaskId());
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + getPrefix() + " Plugin Enable");
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            PlayerDataUtil.unregisterPlayerData(player);
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + getPrefix() + ChatColor.RED +" Plugin Disable");
    }

    private void eventRegister() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinAndQuit(), this);
        pluginManager.registerEvents(new PlayerKillPointRelatedEvent(), this);
        pluginManager.registerEvents(new PlayerRelatedEvent(), this);
        pluginManager.registerEvents(new WorldRelatedEvent(), this);
        pluginManager.registerEvents(new MainMenuEvent(), this);
        pluginManager.registerEvents(new MainMenuChatEditEvent(), this);
        pluginManager.registerEvents(new PlayerSpellRelatedEvent(), this);
    }

    private void commandsRegister() {
        PluginCommand pluginCommand = getServer().getPluginCommand("vaulthuntersintegrated");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(new MainCommand());
            pluginCommand.setTabCompleter(new MainTabComplete());
        }
    }

    public void reloadDataFiles() {
        reloadConfig();
        spellAbilityDataFile.reloadConfig();
        SpellAbilityUtil.update();
        KillPointUtil.reloadVariables();
        PlayerKillPointRelatedEvent.reloadVariables();
        PlayerRelatedEvent.reloadVariables();
        WorldRelatedEvent.reloadVariables();
        PlayerSpellRelatedEvent.reloadVariables();
        isDebug = getPlugin().getConfig().contains("Debug") && getPlugin().getConfig().getBoolean("Debug");
        isEnabledGlowing = getPlugin().getConfig().contains("Setting.Glowing.Enabled") && getPlugin().getConfig().getBoolean("Setting.Glowing.Enabled");
        String glowTeamColorName = getPlugin().getConfig().contains("Setting.Glowing.Color") ? getPlugin().getConfig().getString("Setting.Glowing.Color") : "WHITE";
        if (glowTeamColorName != null) {
            try {
                glowTeam = GlowUtil.GlowTeam.valueOf(glowTeamColorName.toUpperCase());
            } catch (IllegalArgumentException ex) {
                glowTeam = GlowUtil.GlowTeam.WHITE;
            }
        }
    }

    protected FileConfiguration config;

    private void createConfigFiles() {
        File configf = new File(getDataFolder(), "config.yml");
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void replaceConfig() {
        File file = new File(getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        File config_old = new File(getDataFolder(),"config_old-" + dateFormat.format(date) + ".yml");
        file.renameTo(config_old);
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getPrefix() + " Plugin replaced the old config.yml with config_old.yml and created a new config.yml");
    }

}

package me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.File;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class PlayerDataFile {

    private final VaultHuntersIntegrated plugin = VaultHuntersIntegrated.getPlugin();
    private final String folderpath = "PlayerData";
    private File file;
    private FileConfiguration config;

    public File getFile() { return this.file; }

    public PlayerDataFile(Player player) {
        String fileS = player.getUniqueId() + ".yml";
        this.file = new File(plugin.getDataFolder() + File.separator + folderpath, fileS);
        if (!this.file.exists()) {
            createFile(player);
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public boolean hasConfigFile() {
        return this.file.exists();
    }

    public void createFile(Player player) {
        if(!this.file.exists()) {
            try {
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);
                yamlConfiguration.set("Name", player.getName());
                yamlConfiguration.set("KillPoint.Point", 0);
                yamlConfiguration.set("KillPoint.Exp", 0);
                yamlConfiguration.save(this.file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createFile(String name) {
        if(!this.file.exists()) {
            try {
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);
                yamlConfiguration.set("Name", name);
                yamlConfiguration.set("KillPoint.Point", 0);
                yamlConfiguration.set("KillPoint.Exp", 0);
                yamlConfiguration.save(this.file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void reloadConfig() {
        if(this.config == null) {
            return;
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        Reader defaultConfigStream;
        try {
            defaultConfigStream = new InputStreamReader(plugin.getResource(this.file.getName()), StandardCharsets.UTF_8);
            if(defaultConfigStream != null) {
                YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream);
                config.setDefaults(defaultConfig);
            }
        }catch(NullPointerException ex) {
            //ex.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            getConfig().save(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig(Player player) {
        String fileS = player.getUniqueId() + ".yml";
        if(this.file == null) {
            this.file = new File(plugin.getDataFolder() + File.separator + this.folderpath, fileS);
        }
        if(!this.file.exists()) {
            plugin.saveResource(fileS, false);
        }
    }


}

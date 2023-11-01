package me.msicraft.vaulthuntersintegrated.aCommon.PlaceholderAPI;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.ExpUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VHIPlaceholder extends PlaceholderExpansion {

    private VaultHuntersIntegrated plugin;

    public VHIPlaceholder(VaultHuntersIntegrated plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vhi";
    }

    @Override
    public @NotNull String getAuthor() {
        return "msicraft";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player.isOnline()) {
            Player onlineP = player.getPlayer();
            if (onlineP != null) {
                switch (params) {
                    case "killpoint" -> {
                        return String.valueOf(KillPointUtil.getKillPoint(onlineP));
                    }
                    case "killpointexp" -> {
                        int percent = KillPointUtil.getKillPointNextLevelToExpPercent(onlineP);
                        return String.valueOf(percent);
                    }
                    case "exp" -> {
                        return String.valueOf(ExpUtil.getPlayerExp(onlineP));
                    }
                    case "mana" -> {
                        int mana = (int) PlayerDataUtil.getPlayerData(onlineP).getPlayerStats().getMana();
                        return String.valueOf(mana);
                    }
                    case "max_mana" -> {
                        double maxMana = PlayerDataUtil.getPlayerData(onlineP).getPlayerStats().getMaxMana();
                        return String.valueOf(maxMana);
                    }
                }
            }
        }
        return null;
    }

}

package me.msicraft.vaulthuntersintegrated.aCommon.MainMenu.Event;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.MainMenu.MainGui;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.SpellAbilityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MainMenuChatEditEvent implements Listener {

    public static boolean hasSpellBindTag(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellBinding"), PersistentDataType.STRING);
    }

    public static void addSpellBindTag(Player player, boolean isEdit, String spellKey) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (isEdit) {
            data.set(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellBinding"), PersistentDataType.STRING, spellKey);
        } else {
            data.remove(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellBinding"));
        }
    }

    public static String getSpellBindingKey(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellBinding"), PersistentDataType.STRING)) {
            return data.get(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellBinding"), PersistentDataType.STRING);
        }
        return null;
    }

    @EventHandler
    public void bindingSpell(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (hasSpellBindTag(player)) {
            String spellKey = getSpellBindingKey(player);
            if (spellKey != null) {
                String message = e.getMessage();
                e.setCancelled(true);
                MainGui mainGui = new MainGui(player);
                if (message.equals("cancel")) {
                    Bukkit.getScheduler().runTask(VaultHuntersIntegrated.getPlugin(), ()-> {
                        player.openInventory(mainGui.getInventory());
                        mainGui.setSpellAbilityManagement(player);
                    });
                } else {
                    message = message.replaceAll("[^0-9]", "");
                    int slot = Integer.parseInt(message) - 1;
                    if (slot < 0) {
                        slot = 0;
                    }
                    if (slot > 8) {
                        slot = 8;
                    }
                    player.sendMessage(ChatColor.GREEN + "스펠이 등록되었습니다.");
                    player.sendMessage(ChatColor.GREEN + "슬롯: " + message);
                    player.sendMessage(ChatColor.GREEN + "스펠: " + spellKey);
                    PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility().setBindSpell(slot, SpellAbilityUtil.getSpellAbility(spellKey));
                    Bukkit.getScheduler().runTask(VaultHuntersIntegrated.getPlugin(), ()-> {
                        player.openInventory(mainGui.getInventory());
                        mainGui.setSpellAbilityManagement(player);
                    });
                }
                addSpellBindTag(player, false, null);
            }
        }
    }

}

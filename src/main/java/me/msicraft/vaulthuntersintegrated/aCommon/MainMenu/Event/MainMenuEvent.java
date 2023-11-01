package me.msicraft.vaulthuntersintegrated.aCommon.MainMenu.Event;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointShopUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.MainMenu.MainGui;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerSpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerStats;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.SpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.PlayerStatsUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.SpellAbilityUtil;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MainMenuEvent implements Listener {

    private void sendKillPointMessage(Player player, int requiredPoint, boolean isCloseInv) {
        KillPointUtil.addKillPoint(player, -requiredPoint);
        if (isCloseInv) {
            player.closeInventory();
        }
        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "킬포인트 -" + requiredPoint);
        player.sendMessage(ChatColor.GREEN + "남은 킬포인트: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
    }

    @EventHandler
    public void onClickMainMenu(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) { return; }
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase(player.getName() + "'s Main Menu")) {
            ClickType type = e.getClick();
            if (type == ClickType.NUMBER_KEY || type == ClickType.SWAP_OFFHAND) {
                e.setCancelled(true);
                return;
            }
            if (e.getCurrentItem() == null) {
                return;
            }
            e.setCancelled(true);
            MainGui mainGui = new MainGui(player);
            ItemStack selectItem = e.getCurrentItem();
            ItemMeta selectItemMeta = selectItem.getItemMeta();
            if (selectItemMeta != null) {
                PersistentDataContainer data = selectItemMeta.getPersistentDataContainer();
                if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_MainMenu"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_MainMenu"), PersistentDataType.STRING);
                    if (var != null) {
                        switch (var) {
                            case "KillPointShop" -> {
                                player.openInventory(mainGui.getInventory());
                                mainGui.setKillPointShop(player);
                            }
                            case "SpellAbilityManagement" -> {
                                if (SpellAbilityUtil.isEnabled) {
                                    player.openInventory(mainGui.getInventory());
                                    mainGui.setSpellAbilityManagement(player);
                                } else {
                                    player.sendMessage(ChatColor.RED + "현재 사용 불가능 합니다.");
                                }
                            }
                            case "PlayerStatsManagement" -> {
                                if (SpellAbilityUtil.isEnabled) {
                                    player.openInventory(mainGui.getInventory());
                                    mainGui.setPlayerStatsManagement(player);
                                } else {
                                    player.sendMessage(ChatColor.RED + "현재 사용 불가능 합니다.");
                                }
                            }
                        }
                    }
                } else if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_KillPointShop"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_KillPointShop"), PersistentDataType.STRING);
                    if (var != null && e.isLeftClick()) {
                        int requiredKillPoint = KillPointShopUtil.getRequiredKillPoint(var);
                        switch (var) {
                            case "MoveSpawn" -> {
                                if (KillPointUtil.hasEnoughKillPoint(player, requiredKillPoint)) {
                                    if (KillPointShopUtil.moveSpawn(player)) {
                                        sendKillPointMessage(player, requiredKillPoint, true);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "스폰 위치가 없습니다.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "충분한 킬 포인트를 가지고 있지 않습니다.");
                                }
                            }
                            case "BackDeathLocation" -> {
                                if (KillPointUtil.hasEnoughKillPoint(player, requiredKillPoint)) {
                                    if (KillPointShopUtil.backLastDeathLocation(player)) {
                                        sendKillPointMessage(player, requiredKillPoint, true);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "충분한 킬 포인트를 가지고 있지 않습니다.");
                                }
                            }
                        }
                    }
                } else if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellAbilityManagement_Button"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellAbilityManagement_Button"), PersistentDataType.STRING);
                    if (var != null) {
                        int maxPage = mainGui.maxSpellAbilityManagementPage();
                        int current = mainGui.getSpellAbilityManagementPage(player);
                        switch (var) {
                            case "Back" -> {
                                player.openInventory(mainGui.getInventory());
                                mainGui.setUpMainMenu(player);
                            }
                            case "Next" -> {
                                int next = current + 1;
                                if (next > maxPage) {
                                    next = 0;
                                }
                                mainGui.setSpellAbilityManagementPage(player, next);
                                player.openInventory(mainGui.getInventory());
                                mainGui.setSpellAbilityManagement(player);
                            }
                            case "Previous" -> {
                                int previous = current - 1;
                                if (previous < 0) {
                                    previous = maxPage;
                                }
                                mainGui.setSpellAbilityManagementPage(player, previous);
                                player.openInventory(mainGui.getInventory());
                                mainGui.setSpellAbilityManagement(player);
                            }
                        }
                    }
                } else if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellAbility_Management"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellAbility_Management"), PersistentDataType.STRING);
                    if (var != null) {
                        PlayerSpellAbility playerSpellAbility = PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility();
                        SpellAbility spellAbility = SpellAbilityUtil.getSpellAbility(var);
                        if (spellAbility != null) {
                            if (type == ClickType.LEFT) {
                                int requiredPoint = spellAbility.getRequiredKillPoint();
                                if (KillPointUtil.hasEnoughKillPoint(player, requiredPoint)) {
                                    int maxLevel = spellAbility.getMaxLevel();
                                    int currentLevel = playerSpellAbility.getSpellAbilityLevel(spellAbility);
                                    int nextLevel = currentLevel + 1;
                                    if (nextLevel > maxLevel) {
                                        player.sendMessage(ChatColor.RED + "최대 레벨에 도달했습니다.");
                                        return;
                                    }
                                    sendKillPointMessage(player, requiredPoint, false);
                                    if (playerSpellAbility.hasSpellAbility(spellAbility)) {
                                        playerSpellAbility.setSpellAbilityLevel(spellAbility, nextLevel);
                                        player.sendMessage(ChatColor.GREEN + "해당 스펠 레벨이 올랐습니다.");
                                    } else {
                                        playerSpellAbility.setSpellAbilityLevel(spellAbility, 1);
                                        player.sendMessage(ChatColor.GREEN + "해당 스펠을 구매했습니다.");
                                    }
                                    player.openInventory(mainGui.getInventory());
                                    mainGui.setSpellAbilityManagement(player);
                                } else {
                                    player.sendMessage(ChatColor.RED + "충분한 킬 포인트를 가지고 있지 않습니다.");
                                }
                            } else if (type == ClickType.RIGHT) {
                                if (playerSpellAbility.hasSpellAbility(spellAbility)) {
                                    player.sendMessage(ChatColor.YELLOW + "========================================");
                                    player.sendMessage(ChatColor.GRAY + " 등록을 원하는 슬롯 숫자을 입력해주세요 (1~9)");
                                    player.sendMessage(ChatColor.GRAY + " 'cancel' 입력시 취소'");
                                    player.sendMessage(ChatColor.YELLOW + "========================================");
                                    player.closeInventory();
                                    MainMenuChatEditEvent.addSpellBindTag(player, true, var);
                                } else {
                                    player.sendMessage(ChatColor.RED + "해당 스펠을 보유하고 있지 않습니다");
                                }
                            }
                        }
                    }
                } else if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellAbility_BindBook"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_SpellAbility_BindBook"), PersistentDataType.STRING);
                    if (var != null) {
                        SpellAbility spellAbility = SpellAbilityUtil.getSpellAbility(var);
                        if (spellAbility != null) {
                            int slot = e.getSlot();
                            PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility().setBindSpell(slot, null);
                            player.sendMessage(ChatColor.GREEN + "해당 슬롯의 스펠이 등록 해제되었습니다");
                            player.openInventory(mainGui.getInventory());
                            mainGui.setSpellAbilityManagement(player);
                        }
                    }
                } else if (data.has(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_PlayerStats_Management"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), "VHI_PlayerStats_Management"), PersistentDataType.STRING);
                    if (var != null && e.isLeftClick()) {
                        PlayerStats playerStats = PlayerDataUtil.getPlayerData(player).getPlayerStats();
                        if (var.equals("maxmana")) {
                            int requiredKillPoint = PlayerStatsUtil.getRequiredManaKillPoint();
                            if (KillPointUtil.hasEnoughKillPoint(player, requiredKillPoint)) {
                                int maxLevel = PlayerStatsUtil.getMaxManaLevel();
                                int currentLevel = playerStats.getManaLevel();
                                int nextLevel = currentLevel + 1;
                                if (nextLevel > maxLevel) {
                                    player.sendMessage(ChatColor.RED + "최대 레벨에 도달하였습니다.");
                                } else {
                                    sendKillPointMessage(player, requiredKillPoint, false);
                                    playerStats.setManaLevel(nextLevel);
                                    player.openInventory(mainGui.getInventory());
                                    mainGui.setPlayerStatsManagement(player);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "충분한 킬 포인트를 가지고 있지 않습니다.");
                            }
                        } else if (var.equals("manaregen")) {
                            int requiredKillPoint = PlayerStatsUtil.getRequiredManaRegenKillPoint();
                            if (KillPointUtil.hasEnoughKillPoint(player, requiredKillPoint)) {
                                int maxLevel = PlayerStatsUtil.getMaxManaRegenLevel();
                                int currentLevel = playerStats.getManaRegenLevel();
                                int nextLevel = currentLevel + 1;
                                if (nextLevel > maxLevel) {
                                    player.sendMessage(ChatColor.RED + "최대 레벨에 도달하였습니다.");
                                } else {
                                    sendKillPointMessage(player, requiredKillPoint, false);
                                    playerStats.setManaRegenLevel(nextLevel);
                                    player.openInventory(mainGui.getInventory());
                                    mainGui.setPlayerStatsManagement(player);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "충분한 킬 포인트를 가지고 있지 않습니다.");
                            }
                        }
                    }
                }
            }
        }
    }

}

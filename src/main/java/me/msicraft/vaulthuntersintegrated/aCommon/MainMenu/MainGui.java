package me.msicraft.vaulthuntersintegrated.aCommon.MainMenu;

import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointShopUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.File.PlayerDataFile;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerData;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerSpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.PlayerStats;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.SpellAbility;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.PlayerStatsUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.SpellAbility.Util.SpellAbilityUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.Util.GuiUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MainGui implements InventoryHolder {

    private Inventory gui;

    private static final int[] SLOTS =
            {9,10,11,12,13,14,15,16,17,
            18,19,20,21,22,23,24,25,26,
            27,28,29,30,31,32,33,34,35,
            36,37,38,39,40,41,42,43,44};

    public MainGui(Player player) {
        gui = Bukkit.createInventory(player, 54, player.getName() + "'s Main Menu");
    }

    public void setUpMainMenu(Player player) {
        String tag = "VHI_MainMenu";
        ItemStack itemStack;
        itemStack = GuiUtil.getKillPointInfoBook(player, tag, "InfoBook");
        gui.setItem(4, itemStack);
        itemStack = GuiUtil.createNormalItem(Material.HOPPER, "킬 포인트 상점", GuiUtil.emptyLore, tag, "KillPointShop");
        gui.setItem(10, itemStack);
        itemStack = GuiUtil.createNormalItem(Material.ENCHANTED_BOOK, "스펠 관리", GuiUtil.emptyLore, tag, "SpellAbilityManagement");
        gui.setItem(11, itemStack);
        itemStack = GuiUtil.createNormalItem(Material.POTION, "스탯 관리", GuiUtil.emptyLore, tag, "PlayerStatsManagement");
        gui.setItem(12, itemStack);
    }

    public void setKillPointShop(Player player) {
        String tag = "VHI_KillPointShop";
        ItemStack itemStack;
        itemStack = GuiUtil.getKillPointInfoBook(player, tag, "InfoBook");
        gui.setItem(4, itemStack);
        List<String> internalNames = KillPointShopUtil.getShopItemInternalNames();
        int guiCount = 0;
        for (String s : internalNames) {
            if (KillPointShopUtil.isEnabled(s)) {
                int slot = SLOTS[guiCount];
                Material material = KillPointShopUtil.getMaterial(s);
                String name = KillPointShopUtil.getName(s);
                List<String> lore = KillPointShopUtil.getLoreList(s, player);
                itemStack = GuiUtil.createNormalItem(material, name, lore, tag, s);
                gui.setItem(slot, itemStack);
                guiCount++;
            }
        }
    }

    public void setSpellAbilityManagement(Player player) {
        setSpellManagementButton(player);
        ItemStack itemStack;
        PlayerSpellAbility playerSpellAbility = PlayerDataUtil.getPlayerData(player).getPlayerSpellAbility();
        int bindCount = 0;
        int maxBindSize = SpellAbilityUtil.getMaxBindSpellSize();
        List<String> bindLore = new ArrayList<>();
        for (int a : playerSpellAbility.getBindSpellAbilityMap().keySet()) {
            if (bindCount >= maxBindSize) {
                break;
            }
            if (!bindLore.isEmpty()) {
                bindLore.clear();
            }
            SpellAbility spellAbility = playerSpellAbility.getBindSpellAbilityMap().get(a);
            if (spellAbility != null) {
                bindLore.add(ChatColor.YELLOW + "우 클릭시 등록 취소");
                bindLore.add("");
                bindLore.add(ChatColor.WHITE + "현재 스펠 레벨: " + ChatColor.GREEN + playerSpellAbility.getPurchasedSpellAbilityMap().get(spellAbility));
                itemStack = GuiUtil.createNormalItem(Material.BOOK, (a+1) + " : " + spellAbility.getDisplayName(), bindLore, "VHI_SpellAbility_BindBook", spellAbility.getKey());
            } else {
                itemStack = GuiUtil.createNormalItem(Material.BOOK, String.valueOf(a+1), GuiUtil.emptyLore, "VHI_SpellAbility_BindBook", "");
            }
            gui.setItem(a, itemStack);
            bindCount++;
        }
        String tag = "VHI_SpellAbility_Management";
        SpellAbility[] spellAbilities = SpellAbility.values();
        List<String> lore = new ArrayList<>();
        int maxSize = spellAbilities.length;
        int pageCount = getSpellAbilityManagementPage(player);
        int guiCount = 9;
        int lastCount = pageCount * 36;
        for (int a = lastCount; a<maxSize; a++) {
            SpellAbility spellAbility = SpellAbilityUtil.getSpellAbility(spellAbilities[a].getKey());
            if (spellAbility != null) {
                if (!lore.isEmpty()) {
                    lore.clear();
                }
                lore.add(ChatColor.YELLOW + "좌 클릭시 스펠 구매(레벨 업)");
                lore.add(ChatColor.YELLOW + "우 클릭시 스펠 등록");
                boolean isBuy = playerSpellAbility.hasSpellAbility(spellAbility);
                if (isBuy) {
                    lore.add(ChatColor.YELLOW + "구매 상태: " + ChatColor.GREEN + "O");
                } else {
                    lore.add(ChatColor.YELLOW + "구매 상태: " + ChatColor.RED + "X");
                }
                lore.add("");
                lore.add(ChatColor.YELLOW + "현재 레벨: " + ChatColor.GREEN + playerSpellAbility.getSpellAbilityLevel(spellAbility));
                lore.addAll(spellAbility.getLore());
                itemStack = GuiUtil.createNormalItem(Material.PAPER, spellAbility.getDisplayName(), lore, tag, spellAbility.getKey());
                gui.setItem(guiCount, itemStack);
                guiCount++;
                if (guiCount >= 45) {
                    break;
                }
            }
        }
    }

    public void setPlayerStatsManagement(Player player) {
        String tag = "VHI_PlayerStats_Management";
        PlayerStats playerStats = PlayerDataUtil.getPlayerData(player).getPlayerStats();
        ItemStack itemStack;
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_AQUA + "최대 마나: " + playerStats.getMaxMana());
        lore.add("");
        lore.add(ChatColor.YELLOW + "좌 클릭: 레벨 상승");
        lore.add(ChatColor.YELLOW + "레벨 당 마나 상승량: " + ChatColor.GREEN + PlayerStatsUtil.getLevelPerMana());
        lore.add(ChatColor.YELLOW + "필요 킬 포인트: " + ChatColor.GREEN + PlayerStatsUtil.getRequiredManaKillPoint());
        lore.add(ChatColor.YELLOW + "현재 레벨: " + ChatColor.GREEN + playerStats.getManaLevel());
        lore.add(ChatColor.YELLOW + "최대 레벨: " + ChatColor.GREEN + PlayerStatsUtil.getMaxManaLevel());
        itemStack = GuiUtil.createNormalItem(Material.BLUE_DYE, "최대 마나", lore, tag, "maxmana");
        gui.setItem(10, itemStack);
        lore.clear();
        lore.add(ChatColor.DARK_AQUA + "마나 재생: " + playerStats.getManaRegen());
        lore.add("");
        lore.add(ChatColor.YELLOW + "좌 클릭: 레벨 상승");
        lore.add(ChatColor.YELLOW + "레벨 당 마나 재생 상승량: " + ChatColor.GREEN + PlayerStatsUtil.getLevelPerManaRegen());
        lore.add(ChatColor.YELLOW + "필요 킬 포인트: " + ChatColor.GREEN + PlayerStatsUtil.getRequiredManaRegenKillPoint());
        lore.add(ChatColor.YELLOW + "현재 레벨: " + ChatColor.GREEN + playerStats.getManaRegenLevel());
        lore.add(ChatColor.YELLOW + "최대 레벨: " + ChatColor.GREEN + PlayerStatsUtil.getMaxManaRegenLevel());
        itemStack = GuiUtil.createNormalItem(Material.LIGHT_BLUE_DYE, "마나 재생", lore, tag, "manaregen");
        gui.setItem(12, itemStack);
    }

    private void setSpellManagementButton(Player player) {
        String tag = "VHI_SpellAbilityManagement_Button";
        ItemStack itemStack;
        itemStack = GuiUtil.createNormalItem(Material.BARRIER, ChatColor.RED + "Back", GuiUtil.emptyLore, tag, "Back");
        gui.setItem(45, itemStack);
        itemStack = GuiUtil.createNormalItem(Material.ARROW, "Next", GuiUtil.emptyLore, tag, "Next");
        gui.setItem(50, itemStack);
        itemStack = GuiUtil.createNormalItem(Material.ARROW, "Previous", GuiUtil.emptyLore, tag, "Previous");
        gui.setItem(48, itemStack);
        String page = "Page: " + (getSpellAbilityManagementPage(player)+1) + "/" + (maxSpellAbilityManagementPage()+1);
        itemStack = GuiUtil.createNormalItem(Material.BOOK, page, GuiUtil.emptyLore, tag, "page");
        gui.setItem(49, itemStack);
    }

    public int maxSpellAbilityManagementPage() {
        int maxFileSize = SpellAbility.values().length;
        return maxFileSize / 36;
    }

    public int getSpellAbilityManagementPage(Player player) {
        int page = 0;
        PlayerData playerData = PlayerDataUtil.getPlayerData(player);
        PlayerDataFile playerDataFile = playerData.getPlayerDataFile();
        if (playerDataFile.getConfig().contains("SpellAbility.Page")) {
            page = playerDataFile.getConfig().getInt("SpellAbility.Page");
        }
        return page;
    }

    public void setSpellAbilityManagementPage(Player player, int page) {
        PlayerData playerData = PlayerDataUtil.getPlayerData(player);
        PlayerDataFile playerDataFile = playerData.getPlayerDataFile();
        playerDataFile.getConfig().set("SpellAbility.Page", page);
        playerDataFile.saveConfig();
    }

    @Override
    public Inventory getInventory() {
        return gui;
    }

}

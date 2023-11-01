package me.msicraft.vaulthuntersintegrated.aCommon.Util;

import me.msicraft.vaulthuntersintegrated.VaultHuntersIntegrated;
import me.msicraft.vaulthuntersintegrated.aCommon.KillPoint.KillPointUtil;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerData;
import me.msicraft.vaulthuntersintegrated.aCommon.PlayerData.PlayerDataUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiUtil {

    public static final List<String> emptyLore = Collections.emptyList();

    public static ItemStack createNormalItem(Material material, String name, List<String> list, String dataTag, String data) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(list);
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(VaultHuntersIntegrated.getPlugin(), dataTag), PersistentDataType.STRING, data);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getKillPointInfoBook(Player player, String dataTag, String data) {
        ItemStack itemStack;
        PlayerData playerData = PlayerDataUtil.getPlayerData(player);
        List<String> lore = new ArrayList<>();
        String name = player.getName() + " 의 정보";
        lore.add("");
        lore.add(ChatColor.GREEN + "킬 포인트: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
        lore.add(ChatColor.GREEN + "킬 포인트 경험치: " + ChatColor.GRAY  + KillPointUtil.getKillPointNextLevelToExpPercent(player) + "%");
        lore.add("");
        lore.add(ChatColor.GREEN + "마나: " + ChatColor.DARK_AQUA + PlayerDataUtil.getPlayerData(player).getPlayerStats().getMana() + "/" + PlayerDataUtil.getPlayerData(player).getPlayerStats().getMaxMana());
        lore.add(ChatColor.GREEN + "마나 재생: " + ChatColor.DARK_AQUA + PlayerDataUtil.getPlayerData(player).getPlayerStats().getManaRegen() + "/s");
        lore.add("");
        lore.add(ChatColor.GREEN + "---------플레이어 능력치----------");
        lore.add(ChatColor.GRAY + "체력: " + ChatColor.GREEN + (Math.round(AttributeUtil.getMaxHealth(player)*100.0)/100.0));
        lore.add(ChatColor.GRAY + "공격 데미지: " + ChatColor.GREEN + AttributeUtil.getDamageValue(player));
        lore.add(ChatColor.GRAY + "공격 속도: " + ChatColor.GREEN + AttributeUtil.getAttackSpeedValue(player));
        lore.add(ChatColor.GRAY + "방어: " + ChatColor.GREEN + AttributeUtil.getArmorValue(player));
        lore.add(ChatColor.GRAY + "방어강도: " + ChatColor.GREEN + AttributeUtil.getArmorToughnessValue(player));
        itemStack = GuiUtil.createNormalItem(Material.BOOK, name, lore, dataTag, data);
        return itemStack;
    }

}

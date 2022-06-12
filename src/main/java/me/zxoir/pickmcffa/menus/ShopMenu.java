package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.pickmcffa.utils.Utils.colorize;
import static me.zxoir.pickmcffa.utils.Utils.duplicateInventory;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/9/2022
 */
public class ShopMenu implements Listener {
    @Getter
    private static final String inventoryName = colorize("&7Shop");
    @Getter
    private static Inventory inventory;

    public static void loadMenu() {
        inventory = Bukkit.createInventory(new MenuHolder(), 27, inventoryName);

        for (int i = 0; i < 27; i++) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            getInventory().setItem(i, new ItemStackBuilder(glass).withName("&a").build());
        }

        ItemStack tempKits = new ItemStackBuilder(Material.NAME_TAG).withName("&aTemp Kits").resetFlags().build();
        ItemStack perks = new ItemStackBuilder(Material.DIAMOND_AXE).withEnchantment(Enchantment.DURABILITY, 1).withName("&aPerks").resetFlags().build();

        inventory.setItem(11, tempKits);
        inventory.setItem(15, perks);
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack clicked = event.getCurrentItem();

        if (inventory.getHolder() == null || !inventory.getHolder().getClass().equals(MenuHolder.class) || clicked == null)
            return;

        if (!inventory.getName().equalsIgnoreCase(inventoryName))
            return;

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        event.setCancelled(true);

        if (clicked.getType().equals(Material.NAME_TAG))
            player.openInventory(duplicateInventory(TempKitShopMenu.getInventory()));

        if (clicked.getType().equals(Material.DIAMOND_AXE))
            player.openInventory(duplicateInventory(PerkShopMenu.getInventory()));
    }
}

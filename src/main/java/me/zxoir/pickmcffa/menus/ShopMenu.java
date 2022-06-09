package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/9/2022
 */
public class ShopMenu {
    @Getter
    private static Inventory inventory;
    @Getter
    private static final String inventoryName = colorize("&7Shop");

    public static void loadMenu() {
        inventory = Bukkit.createInventory(new MenuHolder(), 27, inventoryName);

        for (int i = 0; i < 27; i++) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
            getInventory().setItem(i, new ItemStackBuilder(glass).withName("&a").build());
        }

        ItemStack tempKits = new ItemStackBuilder(Material.NAME_TAG).withName("&aTemp Kits").resetFlags().build();
        ItemStack perks = new ItemStackBuilder(Material.DIAMOND_AXE).withEnchantment(Enchantment.DURABILITY, 1).withName("&aPerks").resetFlags().build();

        inventory.setItem(11, tempKits);
        inventory.setItem(15, perks);
    }
}

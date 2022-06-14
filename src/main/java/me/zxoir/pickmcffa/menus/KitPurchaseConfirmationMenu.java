package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import me.zxoir.pickmcffa.utils.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static me.zxoir.pickmcffa.managers.KitManager.canPurchaseKit;
import static me.zxoir.pickmcffa.managers.KitManager.hasKit;
import static me.zxoir.pickmcffa.utils.Utils.colorize;
import static me.zxoir.pickmcffa.utils.Utils.duplicateInventory;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/12/2022
 */
public class KitPurchaseConfirmationMenu implements Listener {
    @Getter
    private static final String inventoryName = colorize("&7Confirm Kit");
    private static final HashMap<User, Kit> userKitHashMap = new HashMap<>();
    private static Inventory inventory;

    public static void loadMenu() {
        inventory = Bukkit.createInventory(new MenuHolder(), 27, inventoryName);

        for (int i = 0; i < 27; i++) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            inventory.setItem(i, new ItemStackBuilder(glass).withName("&a").build());
        }

        ItemStack cancel = new ItemStackBuilder(new ItemStack(Material.STAINED_CLAY, 1, (short) 14))
                .withName("&c&lCancel")
                .withLore("&7Click to cancel")
                .resetFlags()
                .build();

        ItemStack purchase = new ItemStackBuilder(new ItemStack(Material.STAINED_CLAY, 1, (short) 13))
                .withName("&a&lPurchase")
                .resetFlags()
                .build();

        inventory.setItem(11, cancel);
        inventory.setItem(15, purchase);
    }

    @NotNull
    public static Inventory getInventory(@NotNull User user, @NotNull Kit kit) {

        if (!canPurchaseKit(user, kit) || hasKit(user, kit) || user.getPlayer() == null) {
            return duplicateInventory(TempKitShopMenu.getInventory());
        }

        userKitHashMap.put(user, kit);
        Inventory inv = duplicateInventory(inventory);
        ItemStack purchaseButton = inv.getItem(15);
        new ItemStackBuilder(purchaseButton).withLore("&7Click to purchase &8&l" + kit.getName()).build();

        ItemStack kitIcon = new ItemStackBuilder(kit.getIcon().clone())
                .withLore("&7" + kit.getDescription())
                .withLore("")
                .withLore("&7Price: &e" + (kit.getPrice() == null || kit.getPrice() <= 0 ? "Free" : kit.getPrice()))
                .withLore("&7Level required: &e" + (kit.getLevel() == null || kit.getLevel() <= 0 ? "0" : kit.getLevel()))
                .withLore("&7Expires after: &e" + (kit.getExpire() == null || kit.getExpire() == 0 ? "Never" : TimeManager.formatTime(kit.getExpire())))
                .build();

        inv.setItem(13, kitIcon);
        return inv;
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        User user = PickMcFFA.getCachedUsers().getIfPresent(event.getPlayer().getUniqueId());

        if (user == null || event.getInventory().getHolder() == null || !event.getInventory().getHolder().getClass().equals(MenuHolder.class) || !event.getInventory().getName().equals(KitPurchaseConfirmationMenu.getInventoryName()))
            return;

        userKitHashMap.remove(user);
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

        if (!userKitHashMap.containsKey(user))
            return;

        Kit kit = userKitHashMap.get(user);

        if (clicked.getItemMeta() != null && clicked.getItemMeta().getDisplayName().equals(colorize("&c&lCancel"))) {
            player.openInventory(duplicateInventory(TempKitShopMenu.getInventory()));
            return;
        }

        if (clicked.getItemMeta() == null || !clicked.getItemMeta().getDisplayName().equals(colorize("&a&lPurchase")))
            return;

        if (!canPurchaseKit(user, kit) || hasKit(user, kit)) {
            player.openInventory(duplicateInventory(TempKitShopMenu.getInventory()));
            return;
        }

        player.closeInventory();
        player.sendMessage(ConfigManager.getKitPurchaseMessage(kit.getName(), kit.getPrice() == null ? 0 : kit.getPrice()));
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
        user.getStats().deductCoins(kit.getPrice());

        if (kit.getPermissions() != null && !kit.getPermissions().isEmpty()) {

            for (String permission : kit.getPermissions()) {
                String command = kit.getExpire() != null && kit.getExpire() > 0 ?
                        "lp user " + player.getName() + " permission settemp " + permission + " true " + TimeManager.formatTimeWithoutSpace(kit.getExpire()) :
                        "lp user " + player.getName() + " permission set " + permission;

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }

        }

    }
}

package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.Perk;
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

import static me.zxoir.pickmcffa.utils.Utils.colorize;
import static me.zxoir.pickmcffa.utils.Utils.duplicateInventory;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/12/2022
 */
public class PerkPurchaseConfirmationMenu implements Listener {
    @Getter
    private static final String inventoryName = colorize("&7Confirm Perk");
    private static Inventory inventory;
    private static final HashMap<User, Perk> userPerkHashMap = new HashMap<>();

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
    public static Inventory getInventory(@NotNull User user, @NotNull Perk perk) {

        if (!canPurchasePerk(user, perk) || hasPerk(user, perk) || user.getPlayer() == null) {
            return duplicateInventory(PerkShopMenu.getInventory());
        }

        userPerkHashMap.put(user, perk);
        Inventory inv = duplicateInventory(inventory);
        ItemStack purchaseButton = inv.getItem(15);
        new ItemStackBuilder(purchaseButton).withLore("&7Click to purchase &8&l" + perk.getName()).build();

        ItemStack perkIcon = new ItemStackBuilder(perk.getIcon().clone())
                .withLore("&7" + perk.getDescription())
                .withLore("")
                .withLore("&7Price: &e" + (perk.getPrice() == null || perk.getPrice() <= 0 ? "Free" : perk.getPrice()))
                .withLore("&7Level required: &e" + (perk.getLevel() == null || perk.getLevel() <= 0 ? "0" : perk.getLevel()))
                .withLore("&7Expires after: &e" + (perk.getExpire() == null || perk.getExpire() == 0 ? "Never" : TimeManager.formatTime(perk.getExpire())))
                .build();

        inv.setItem(13, perkIcon);
        return inv;
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        User user = PickMcFFA.getCachedUsers().getIfPresent(event.getPlayer().getUniqueId());

        if (user == null || event.getInventory().getHolder() == null || !event.getInventory().getHolder().getClass().equals(MenuHolder.class) || !event.getInventory().getName().equals(PerkPurchaseConfirmationMenu.getInventoryName()))
            return;

        userPerkHashMap.remove(user);
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

        if (!userPerkHashMap.containsKey(user))
            return;

        Perk perk = userPerkHashMap.get(user);

        if (clicked.getItemMeta() != null && clicked.getItemMeta().getDisplayName().equals(colorize("&c&lCancel"))) {
            player.openInventory(duplicateInventory(PerkShopMenu.getInventory()));
            return;
        }

        if (clicked.getItemMeta() == null || !clicked.getItemMeta().getDisplayName().equals(colorize("&a&lPurchase")))
            return;

        if (!canPurchasePerk(user, perk) || hasPerk(user, perk)) {
            player.openInventory(duplicateInventory(PerkShopMenu.getInventory()));
            return;
        }

        player.closeInventory();
        player.sendMessage(ConfigManager.getPerkPurchaseMessage(perk.getName(), perk.getPrice() == null ? 0 : perk.getPrice()));
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
        user.getStats().deductCoins(perk.getPrice());

        if (perk.getPermissions() != null && !perk.getPermissions().isEmpty()) {

            for (String permission : perk.getPermissions()) {
                String command = perk.getExpire() != null && perk.getExpire() > 0 ?
                        "lp user " + player.getName() + " permission settemp " + permission + " true " + TimeManager.formatTimeWithoutSpace(perk.getExpire()) :
                        "lp user " + player.getName() + " permission set " + permission;

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }

        }

    }

    private static boolean hasPerk(User user, @NotNull Perk perk) {
        if (perk.getPermissions() == null || perk.getPermissions().isEmpty())
            return true;

        if (user.getPlayer() == null)
            return false;

        for (String permission : perk.getPermissions()) {
            if (!user.getPlayer().hasPermission(permission))
                return false;
        }

        return true;
    }

    private static boolean canPurchasePerk(@NotNull User user, Perk perk) {
        Player player = user.getPlayer();
        if (player == null)
            return false;

        if (perk.getLevel() != null && perk.getLevel() > user.getStats().getLevel())
            return false;

        return perk.getPrice() == null || user.getStats().getCoins() >= perk.getPrice();
    }
}

package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.KitManager;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import me.zxoir.pickmcffa.utils.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/12/2022
 */
public class TempKitShopMenu implements Listener {
    @Getter
    private static final String inventoryName = colorize("&7Kit Shop");
    @Getter
    private static final ConcurrentHashMap<Integer, Kit> kitSlots = new ConcurrentHashMap<>();
    @Getter
    private static Inventory inventory;

    public static void loadMenu() {
        inventory = Bukkit.createInventory(new MenuHolder(), 27, inventoryName);

        for (int i = 0; i < 27; i++) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            getInventory().setItem(i, new ItemStackBuilder(glass).withName("&a").build());
        }

        Kit speedKit = KitManager.getSpeedKit();
        Kit tankKit = KitManager.getTankKit();
        Kit strengthKit = KitManager.getStrengthKit();
        ItemStack speedKitItem = new ItemStackBuilder(speedKit.getIcon().clone())
                .withLore("&7" + speedKit.getDescription())
                .withLore("")
                .withLore("&7Price: &e" + (speedKit.getPrice() == null || speedKit.getPrice() <= 0 ? "Free" : speedKit.getPrice()))
                .withLore("&7Level required: &e" + (speedKit.getLevel() == null || speedKit.getLevel() <= 0 ? "0" : speedKit.getLevel()))
                .withLore("&7Expires after: &e" + (speedKit.getExpire() == null || speedKit.getExpire() == 0 ? "Never" : TimeManager.formatTime(speedKit.getExpire())))
                .withLore("")
                .build();

        ItemStack tankKitItem = new ItemStackBuilder(tankKit.getIcon().clone())
                .withLore("&7" + tankKit.getDescription())
                .withLore("")
                .withLore("&7Price: &e" + (tankKit.getPrice() == null || tankKit.getPrice() <= 0 ? "Free" : tankKit.getPrice()))
                .withLore("&7Level required: &e" + (tankKit.getLevel() == null || tankKit.getLevel() <= 0 ? "0" : tankKit.getLevel()))
                .withLore("&7Expires after: &e" + (tankKit.getExpire() == null || tankKit.getExpire() == 0 ? "Never" : TimeManager.formatTime(tankKit.getExpire())))
                .withLore("")
                .build();

        ItemStack strengthKitItem = new ItemStackBuilder(strengthKit.getIcon().clone())
                .withLore("&7" + strengthKit.getDescription())
                .withLore("")
                .withLore("&7Price: &e" + (strengthKit.getPrice() == null || strengthKit.getPrice() <= 0 ? "Free" : strengthKit.getPrice()))
                .withLore("&7Level required: &e" + (strengthKit.getLevel() == null || strengthKit.getLevel() <= 0 ? "0" : strengthKit.getLevel()))
                .withLore("&7Expires after: &e" + (strengthKit.getExpire() == null || strengthKit.getExpire() == 0 ? "Never" : TimeManager.formatTime(strengthKit.getExpire())))
                .withLore("")
                .build();

        inventory.setItem(11, speedKitItem);
        kitSlots.put(11, speedKit);
        inventory.setItem(13, tankKitItem);
        kitSlots.put(13, tankKit);
        inventory.setItem(15, strengthKitItem);
        kitSlots.put(15, strengthKit);
    }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() == null || !inventory.getHolder().getClass().equals(MenuHolder.class))
            return;

        if (!inventory.getName().equalsIgnoreCase(inventoryName))
            return;

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        for (Integer i : kitSlots.keySet()) {
            ItemStack itemStack = inventory.getItem(i);
            Kit kit = kitSlots.get(i);

            String lore = "&7⩥ &eClick to purchase";
            if (hasKit(user, kit))
                lore = "&7⩥ &cYou already own this Kit";
            else if (!canPurchaseKit(user, kit))
                lore = "&7⩥ &cYou can't purchase this Kit";

            new ItemStackBuilder(itemStack).withLore(lore).build();
        }
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

        if (!kitSlots.containsKey(event.getSlot()))
            return;

        Kit kit = kitSlots.get(event.getSlot());

        if (!canPurchaseKit(user, kit) || hasKit(user, kit))
            return;

        player.openInventory(KitPurchaseConfirmationMenu.getInventory(user, kit));
    }

    private boolean hasKit(User user, @NotNull Kit kit) {
        if (kit.getPermissions() == null || kit.getPermissions().isEmpty())
            return true;

        if (user.getPlayer() == null)
            return false;

        for (String permission : kit.getPermissions()) {
            if (!user.getPlayer().hasPermission(permission))
                return false;
        }

        return true;
    }

    private boolean canPurchaseKit(@NotNull User user, Kit kit) {
        Player player = user.getPlayer();
        if (player == null)
            return false;

        if (kit.getLevel() != null && kit.getLevel() > user.getStats().getLevel())
            return false;

        return kit.getPrice() == null || user.getStats().getCoins() >= kit.getPrice();
    }
}

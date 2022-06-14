package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Perk;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.PerkManager;
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

import static me.zxoir.pickmcffa.managers.PerkManager.canPurchasePerk;
import static me.zxoir.pickmcffa.managers.PerkManager.hasPerk;
import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/12/2022
 */
public class PerkShopMenu implements Listener {
    @Getter
    private static final String inventoryName = colorize("&7Perk Shop");
    @Getter
    private static final ConcurrentHashMap<Integer, Perk> perkSlots = new ConcurrentHashMap<>();
    @Getter
    private static Inventory inventory;

    public static void loadMenu() {
        inventory = Bukkit.createInventory(new MenuHolder(), 27, inventoryName);

        for (int i = 0; i < 27; i++) {
            ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
            getInventory().setItem(i, new ItemStackBuilder(glass).withName("&a").build());
        }

        Perk explosionPerk = PerkManager.getExplosionPerk();
        Perk absorptionPerk = PerkManager.getAbsorptionPerk();
        Perk speedPerk = PerkManager.getSpeedPerk();

        ItemStack explosionPerkItem = new ItemStackBuilder(explosionPerk.getIcon().clone())
                .withLore("&7" + explosionPerk.getDescription())
                .withLore("")
                .withLore("&7Price: &e" + (explosionPerk.getPrice() == null || explosionPerk.getPrice() <= 0 ? "Free" : explosionPerk.getPrice()))
                .withLore("&7Level required: &e" + (explosionPerk.getLevel() == null || explosionPerk.getLevel() <= 0 ? "0" : explosionPerk.getLevel()))
                .withLore("&7Expires after: &e" + (explosionPerk.getExpire() == null || explosionPerk.getExpire() == 0 ? "Never" : TimeManager.formatTime(explosionPerk.getExpire())))
                .withLore("")
                .build();

        ItemStack absorptionPerkItem = new ItemStackBuilder(absorptionPerk.getIcon().clone())
                .withLore("&7" + absorptionPerk.getDescription())
                .withLore("")
                .withLore("&7Price: &e" + (absorptionPerk.getPrice() == null || absorptionPerk.getPrice() <= 0 ? "Free" : absorptionPerk.getPrice()))
                .withLore("&7Level required: &e" + (absorptionPerk.getLevel() == null || absorptionPerk.getLevel() <= 0 ? "0" : absorptionPerk.getLevel()))
                .withLore("&7Expires after: &e" + (absorptionPerk.getExpire() == null || absorptionPerk.getExpire() == 0 ? "Never" : TimeManager.formatTime(absorptionPerk.getExpire())))
                .withLore("")
                .build();

        ItemStack speedPerkItem = new ItemStackBuilder(speedPerk.getIcon().clone())
                .withLore("&7" + speedPerk.getDescription())
                .withLore("")
                .withLore("&7Price: &e" + (speedPerk.getPrice() == null || speedPerk.getPrice() <= 0 ? "Free" : speedPerk.getPrice()))
                .withLore("&7Level required: &e" + (speedPerk.getLevel() == null || speedPerk.getLevel() <= 0 ? "0" : speedPerk.getLevel()))
                .withLore("&7Expires after: &e" + (speedPerk.getExpire() == null || speedPerk.getExpire() == 0 ? "Never" : TimeManager.formatTime(speedPerk.getExpire())))
                .withLore("")
                .build();

        inventory.setItem(11, explosionPerkItem);
        perkSlots.put(11, explosionPerk);
        inventory.setItem(13, absorptionPerkItem);
        perkSlots.put(13, absorptionPerk);
        inventory.setItem(15, speedPerkItem);
        perkSlots.put(15, speedPerk);
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

        for (Integer i : perkSlots.keySet()) {
            ItemStack itemStack = inventory.getItem(i);
            Perk perk = perkSlots.get(i);

            String lore = "&7⩥ &eClick to purchase";
            if (hasPerk(user, perk))
                lore = "&7⩥ &cYou already own this Perk";
            else if (!canPurchasePerk(user, perk))
                lore = "&7⩥ &cYou can't purchase this Perk";

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

        if (!perkSlots.containsKey(event.getSlot()))
            return;

        Perk perk = perkSlots.get(event.getSlot());

        if (!canPurchasePerk(user, perk) || hasPerk(user, perk))
            return;

        player.openInventory(PerkPurchaseConfirmationMenu.getInventory(user, perk));
    }
}

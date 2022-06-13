package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.Perk;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.KitManager;
import me.zxoir.pickmcffa.managers.PerkManager;
import me.zxoir.pickmcffa.perks.SpeedPerk;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
public class PerkMenu implements Listener {
    @Getter
    private static final ConcurrentHashMap<Integer, Perk> perkSlots = new ConcurrentHashMap<>();
    @Getter
    private static final String inventoryName = colorize("&7Select a Perk");
    @Getter
    private static Inventory inventory;

    public static void loadMenu() {
        inventory = Bukkit.createInventory(new MenuHolder(), 27, inventoryName);

        setBoarders();

        inventory.setItem(11, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Perk").build());
        inventory.setItem(13, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Perk").build());
        inventory.setItem(15, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Perk").build());
    }

    private static void setBoarders() {
        ItemStack glass = new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).resetFlags().withName(colorize("&a")).build();
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, glass);
        }
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

        if (hasPerk(user, PerkManager.getExplosionPerk())) {
            inventory.setItem(11, PerkManager.getExplosionPerk().getIcon().clone());
            perkSlots.put(11, PerkManager.getExplosionPerk());
        }

        if (hasPerk(user, PerkManager.getAbsorptionPerk())) {
            inventory.setItem(13, PerkManager.getAbsorptionPerk().getIcon().clone());
            perkSlots.put(13, PerkManager.getAbsorptionPerk());
        }


        if (hasPerk(user, PerkManager.getSpeedPerk())) {
            inventory.setItem(15, PerkManager.getSpeedPerk().getIcon().clone());
            perkSlots.put(15, PerkManager.getSpeedPerk());
        }

        for (Integer i : perkSlots.keySet()) {
            Perk perk = perkSlots.get(i);
            ItemStack perkItem = inventory.getItem(i);
            if (perkItem == null || perkItem.getType().equals(Material.STAINED_GLASS_PANE))
                continue;

            boolean hasPermission = hasPerk(user, perk);
            boolean equippedPerk = user.getSelectedPerk() != null && user.getSelectedPerk().equals(perk);

            perkItem = new ItemStackBuilder(perkItem.clone())
                    .clearLore()
                    .withLore(equippedPerk ? "&7⩥ &cThis Perk is already selected" : (hasPermission ? "&7⩥ &eClick to equip!" : "&7⩥ &cYou don't have access to this Perk"))
                    .build();

            inventory.setItem(i, perkItem);
        }
    }

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() == null || !inventory.getHolder().getClass().equals(MenuHolder.class))
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

        if (user.getSelectedPerk() != null && user.getSelectedPerk().equals(perk)) {
            player.sendMessage(ConfigManager.getSamePerkError());
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10, 2);
            return;
        }

        if (!hasPerk(user, perk)) {
            player.sendMessage(ConfigManager.getNoPerkAccessError());
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10, 2);
        }

        user.setSelectedPerk(perk);
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
        user.save();
    }

    private boolean hasPerk(User user, @NotNull Perk perk) {
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
}

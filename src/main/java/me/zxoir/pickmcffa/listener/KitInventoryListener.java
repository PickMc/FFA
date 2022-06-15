package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.menus.KitInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/15/2022
 */
public class KitInventoryListener implements Listener {

    @EventHandler
    public void saveKitInventory(@NotNull InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        Inventory inventory = event.getInventory();

        if (user == null)
            return;

        if (!inventory.getHolder().getClass().equals(KitInventoryHolder.class))
            return;

        KitInventoryHolder holder = (KitInventoryHolder) inventory.getHolder();
        Kit kit = holder.getKit();
        user.getSavedInventories().put(kit, inventory.getContents());
        user.save();
        player.sendMessage(colorize("&a&lKit layout saved!"));
    }

    @EventHandler
    public void onInventoryChange(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Inventory clickedInventory = event.getClickedInventory();

        if (inventory == null || clickedInventory == null)
            return;

        if (!inventory.getHolder().getClass().equals(KitInventoryHolder.class))
            return;

        KitInventoryHolder holder = (KitInventoryHolder) inventory.getHolder();

        if (clickedInventory.getHolder().equals(holder))
            return;

        event.setCancelled(true);
    }
}

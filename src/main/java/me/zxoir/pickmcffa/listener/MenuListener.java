package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.menus.MenuHolder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/26/2022
 */
public class MenuListener implements Listener {

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || event.getClickedInventory() == null || event.getClickedInventory().getHolder() == null || !event.getClickedInventory().getHolder().getClass().equals(MenuHolder.class))
            return;

        event.setCancelled(true);
    }
}

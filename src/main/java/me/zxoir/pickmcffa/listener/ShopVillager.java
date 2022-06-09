package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.menus.ShopMenu;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/9/2022
 */
public class ShopVillager implements Listener {
    @EventHandler
    public void onInteractVillager(@NotNull PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager))
            return;

        Villager villager = (Villager) event.getRightClicked();
        if (villager.getCustomName() == null || !villager.getCustomName().equals(ConfigManager.getShopVillagerName()))
            return;

        event.setCancelled(true);
        event.getPlayer().openInventory(ShopMenu.getInventory());
    }

    @EventHandler
    public void onVillagerOpen(@NotNull InventoryOpenEvent event) {
        if (event.getInventory().getType().equals(InventoryType.MERCHANT)) event.setCancelled(true);
    }
}
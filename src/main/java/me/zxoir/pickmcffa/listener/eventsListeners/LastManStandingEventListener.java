package me.zxoir.pickmcffa.listener.eventsListeners;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.EventsManager;
import me.zxoir.pickmcffa.menus.KitMenu;
import me.zxoir.pickmcffa.menus.MenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/21/2022
 */
public class LastManStandingEventListener implements Listener {

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        if (user.getSelectedKit() != null)
            return;

        if (!EventsManager.isEventActive() || EventsManager.getCurrentEventType() != EventsManager.EventType.LAST_MAN_STANDING)
            return;

        if (inventory.getHolder() == null || !inventory.getHolder().getClass().equals(MenuHolder.class))
            return;

        if (!inventory.getName().equalsIgnoreCase(KitMenu.getInventoryName()))
            return;

        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> player.openInventory(event.getInventory()), 1);
        player.sendMessage(colorize("&c&lYou must choose a Kit for this Event!"));
    }

}

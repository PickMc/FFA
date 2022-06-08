package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.KitManager;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/26/2022
 */
public class KitMenu implements Listener {
    @Getter
    private static final HashMap<Integer, Kit> kitSlots = new HashMap<>();
    @Getter
    private static Inventory inventory;

    @EventHandler
    public void onClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (!inventory.getHolder().getClass().equals(MenuHolder.class))
            return;

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        event.setCancelled(true);

        if (!kitSlots.containsKey(event.getSlot()))
            return;

        Kit kit = kitSlots.get(event.getSlot());

        if (user.getSelectedKit() != null && user.getSelectedKit().equals(kit)) {
            player.sendMessage(ConfigManager.getSameKitError());
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10, 2);
            return;
        }

        user.setSelectedKit(kit);
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
    }

    public static void loadMenu() {
        inventory = Bukkit.createInventory(new MenuHolder(), 27, colorize("&7Select a Kit"));

        inventory.setItem(2, KitManager.getDefaultKit().getIcon());
        kitSlots.put(2, KitManager.getDefaultKit());

        setBoarders();
    }

    private static void setBoarders() {
        ItemStack glass = new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).resetFlags().withName(colorize("&a")).build();

        int[] kitPositions = {2, 6, 10, 12, 13, 14, 16};

        for (int i = 0; i < 27; i++) {

            if (ArrayUtils.contains(kitPositions, i))
                continue;

            inventory.setItem(i, glass);
        }
    }
}

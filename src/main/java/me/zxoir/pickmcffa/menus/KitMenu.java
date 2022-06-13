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
 * @since 3/26/2022
 */
public class KitMenu implements Listener {
    @Getter
    private static final ConcurrentHashMap<Integer, Kit> kitSlots = new ConcurrentHashMap<>();
    @Getter
    private static final String inventoryName = colorize("&7Select a Kit");
    @Getter
    private static Inventory inventory;

    public static void loadMenu() {
        inventory = Bukkit.createInventory(new MenuHolder(), 27, inventoryName);

        inventory.setItem(10, KitManager.getDefaultKit().getIcon().clone());
        kitSlots.put(10, KitManager.getDefaultKit());

        inventory.setItem(2, KitManager.getInfluencerKit().getIcon().clone());
        kitSlots.put(2, KitManager.getInfluencerKit());

        inventory.setItem(6, KitManager.getPremiumKit().getIcon().clone());
        kitSlots.put(6, KitManager.getPremiumKit());

        inventory.setItem(16, KitManager.getPremiumPlusKit().getIcon().clone());
        kitSlots.put(16, KitManager.getPremiumPlusKit());

        inventory.setItem(12, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Kit").build());
        inventory.setItem(13, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Kit").build());
        inventory.setItem(14, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Kit").build());

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

        if (hasKit(user, KitManager.getSpeedKit())) {
            inventory.setItem(12, KitManager.getSpeedKit().getIcon().clone());
            kitSlots.put(12, KitManager.getSpeedKit());
        }

        if (player.hasPermission("kit.tank")) {
            inventory.setItem(13, KitManager.getTankKit().getIcon().clone());
            kitSlots.put(13, KitManager.getTankKit());
        }


        if (player.hasPermission("kit.strength")) {
            inventory.setItem(14, KitManager.getStrengthKit().getIcon().clone());
            kitSlots.put(14, KitManager.getStrengthKit());
        }

        for (Integer i : kitSlots.keySet()) {
            Kit kit = kitSlots.get(i);
            ItemStack kitItem = inventory.getItem(i);
            if (kitItem == null || kitItem.getType().equals(Material.STAINED_GLASS_PANE))
                continue;

            boolean hasPermission = hasKit(user, kit);
            boolean equippedKit = user.getSelectedKit() != null && user.getSelectedKit().equals(kit);

            kitItem = new ItemStackBuilder(kitItem.clone())
                    .clearLore()
                    .withLore(equippedKit ? "&7⩥ &cThis Kit is already selected" : (hasPermission ? "&7⩥ &eClick to equip!" : "&7⩥ &cYou don't have access to this Kit"))
                    .withLore("&8Right click to edit layout (SOON)")
                    .build();

            inventory.setItem(i, kitItem);
        }
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

        if (!kitSlots.containsKey(event.getSlot()))
            return;

        Kit kit = kitSlots.get(event.getSlot());

        if (user.getSelectedKit() != null && user.getSelectedKit().equals(kit)) {
            player.sendMessage(ConfigManager.getSameKitError());
            player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10, 2);
            return;
        }

        if (kit.getPermissions() != null && !kit.getPermissions().isEmpty()) {

            for (String permission : kit.getPermissions()) {

                if (!player.hasPermission(permission)) {
                    player.sendMessage(ConfigManager.getNoKitAccessError());
                    player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10, 2);
                    return;
                }

            }

        }

        boolean isPremiumPlus = player.hasPermission("group.premiumplus");
        boolean isPremium = player.hasPermission("group.premium");
        boolean isInfluencer = player.hasPermission("group.influencer");

        if (isPremiumPlus || isPremium || isInfluencer) {

            if (kit.getItems().length > 0) {

                for (ItemStack itemStack : kit.getItems()) {

                    if (itemStack == null || !itemStack.getType().equals(Material.SNOW_BALL))
                        continue;

                    if (isPremiumPlus)
                        itemStack.setAmount(25);
                    else if (isPremium)
                        itemStack.setAmount(20);
                    else
                        itemStack.setAmount(18);

                }

            }

        }

        user.setSelectedKit(null);
        user.setSelectedKit(kit);
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
    }
}

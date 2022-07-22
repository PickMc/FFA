package me.zxoir.pickmcffa.menus;

import lombok.Getter;
import lombok.SneakyThrows;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.listener.KillStreakListener;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.KitManager;
import me.zxoir.pickmcffa.utils.ItemDeserializer;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import me.zxoir.pickmcffa.utils.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static me.zxoir.pickmcffa.managers.KitManager.*;
import static me.zxoir.pickmcffa.utils.Utils.*;

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
    private static final String serializedRandomHead = "rO0ABXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFwdAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFibGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmplY3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAAAAR0AAI9PXQABHR5cGV0AAZkYW1hZ2V0AARtZXRhdXEAfgAGAAAABHQAHm9yZy5idWtraXQuaW52ZW50b3J5Lkl0ZW1TdGFja3QAClNLVUxMX0lURU1zcgAPamF2YS5sYW5nLlNob3J0aE03EzRg2lICAAFTAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsCAAB4cAADc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQADVF1ZXN0aW9uIE1hcmt0AWRINHNJQUFBQUFBQUFBRTJPeTJxRFFCaEcveFlLVnZvWTNRcGVvdFpscWNhTVpMUXhSaDEzWHNib09LYkJhSWcrVlIreExydjhPT2ZBSndLSThIYnNKczYvaDUrNjVWU0FaMVRCdTI2YXF2SlI2cEpxV3JtMEtkVkNzdkthU21wVlZCdkZvcWFsbVNLSWEzU2x3OWpTMnlzSUkzMk0wMEJ2SWdBOENmQVM1M3lpOEV0blQ4N1NScTVTajVjek10WWRIV1VlSUhZMTBTV2VpeTlrb0g3bHUwOWpQMXYvWEgzTUU1MFR6V3V5eTJFcStsamVheUdudTFBcCs5TTlTM3ptUncwajdLUVF0bTF3dE9WK2Y5Z0VMcDR6Tyt4dzRxaDRJU3BaMEJMWVp5MXpZNDRaV2JBZHRwbnJ5RGp5bU84NkNvN09zNStRUnhCMWJaMHExdm9lL2dDRXo0aVlHQUVBQUE9PQ==";
    @Getter
    private static Inventory inventory;

    @SneakyThrows
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


        ItemStack randomHead = ItemDeserializer.itemStackFromBase64(serializedRandomHead);
        inventory.setItem(22, new ItemStackBuilder(randomHead).withName("&9&lRandom Kit").withLore("&7⩥ &eClick to equip!").build());
        inventory.setItem(12, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Kit").build());
        inventory.setItem(13, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Kit").build());
        inventory.setItem(14, new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).withName("&c&lYou don't have access to this Kit").build());

        setBoarders();
    }

    private static void setBoarders() {
        ItemStack glass = new ItemStackBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7)).resetFlags().withName(colorize("&a")).build();

        int[] kitPositions = {2, 6, 10, 12, 13, 14, 16, 22};

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

        if (hasKit(user, KitManager.getTankKit())) {
            inventory.setItem(13, KitManager.getTankKit().getIcon().clone());
            kitSlots.put(13, KitManager.getTankKit());
        }


        if (hasKit(user, KitManager.getStrengthKit())) {
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
                    .withLore("&8Right click to edit layout")
                    .build();

            inventory.setItem(i, kitItem);
        }
    }

    @SneakyThrows
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

        if (event.getSlot() == 22) {
            Kit randomKit = getRandomKit(user);

            setSnowballAmount(user, randomKit);

            user.setSelectedKit(null);

            if (user.getSavedInventories().containsKey(randomKit))
                user.setSelectedKit(randomKit, user.getSavedInventories().get(randomKit));
            else
                user.setSelectedKit(randomKit);

            player.closeInventory();
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
            Utils.sendActionText(player, colorize("&9Equipped &a&l" + randomKit.getIcon().getItemMeta().getDisplayName()));

            KillStreakListener.KillStreakDebuff(user);
            return;
        }

        if (!kitSlots.containsKey(event.getSlot()))
            return;

        Kit kit = kitSlots.get(event.getSlot());

        if (event.getClick().equals(ClickType.RIGHT) && !EventsManager.isEventActive()) {
            Inventory kitInventory = duplicateInventory(Bukkit.createInventory(new KitInventoryHolder(kit), 36, kit.getIcon().getItemMeta().getDisplayName()));
            kitInventory.setContents(kit.getItems());
            player.openInventory(kitInventory);
            return;
        }

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

        setSnowballAmount(user, kit);

        user.setSelectedKit(null);

        if (user.getSavedInventories().containsKey(kit))
            user.setSelectedKit(kit, user.getSavedInventories().get(kit));
        else
            user.setSelectedKit(kit);

        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);

        KillStreakListener.KillStreakDebuff(user);
    }

    private Kit getRandomKit(User user) {
        List<Kit> kits = new ArrayList<>();

        kits.add(getDefaultKit());

        if (KitManager.hasKit(user, getPremiumPlusKit()))
            kits.add(getPremiumPlusKit());

        if (KitManager.hasKit(user, getPremiumKit()))
            kits.add(getPremiumKit());

        if (KitManager.hasKit(user, getInfluencerKit()))
            kits.add(getInfluencerKit());

        if (KitManager.hasKit(user, getTankKit()))
            kits.add(getTankKit());

        if (KitManager.hasKit(user, getSpeedKit()))
            kits.add(getSpeedKit());

        if (KitManager.hasKit(user, getStrengthKit()))
            kits.add(getStrengthKit());

        return kits.get(getRANDOM().nextInt(kits.size()));
    }

    private void setSnowballAmount(User user, Kit kit) {
        boolean isPremiumPlus = KitManager.hasKit(user, getPremiumPlusKit());
        boolean isPremium = KitManager.hasKit(user, getPremiumKit());
        boolean isInfluencer = KitManager.hasKit(user, getInfluencerKit());

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
    }
}

package me.zxoir.pickmcffa.kits;

import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/19/2022
 */
public class DefaultKit extends Kit {
    private static final ItemStack helmet = new ItemStackBuilder(Material.IRON_HELMET).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
    private static final ItemStack chestplate = new ItemStackBuilder(Material.IRON_CHESTPLATE).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack leggings = new ItemStackBuilder(Material.IRON_LEGGINGS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
    private static final ItemStack boots = new ItemStackBuilder(Material.IRON_BOOTS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
    private static final ItemStack axe = new ItemStackBuilder(Material.STONE_AXE).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack bow = new ItemStackBuilder(Material.BOW).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack arrows = new ItemStack(Material.ARROW, 5);
    private static final ItemStack snowballs = new ItemStack(Material.SNOW_BALL, 16);

    private static final ItemStack[] armour = {boots, leggings, chestplate, helmet};
    private static final ItemStack[] items = {axe, bow, snowballs, arrows};

    public DefaultKit() {
        super("Default",
                "Default Kit", null,
                null,
                new ItemStackBuilder(Material.WOOD_AXE).withName("&8&lDefault Kit").resetFlags().build(),
                null,
                items,
                armour
        );
    }

    @Override
    public void killAction(User killed, @NotNull User killer) {
        //killed.getPlayer().sendMessage("You died bruv!");
        killer.getPlayer().sendMessage("Good job! You achieved a kill.");
    }
}

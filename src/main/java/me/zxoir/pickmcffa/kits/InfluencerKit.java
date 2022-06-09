package me.zxoir.pickmcffa.kits;

import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/9/2022
 */
public class InfluencerKit extends Kit {
    private static final ItemStack helmet = new ItemStackBuilder(Material.IRON_HELMET).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack chestplate = new ItemStackBuilder(Material.IRON_CHESTPLATE).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack leggings = new ItemStackBuilder(Material.IRON_LEGGINGS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build();
    private static final ItemStack boots = new ItemStackBuilder(Material.IRON_BOOTS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack axe = new ItemStackBuilder(Material.WOOD_AXE).withEnchantment(Enchantment.DAMAGE_ALL, 1).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack bow = new ItemStackBuilder(Material.BOW).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack arrows = new ItemStack(Material.ARROW, 5);
    private static final ItemStack snowballs = new ItemStack(Material.SNOW_BALL, 18);

    private static final ItemStack[] armour = {boots, leggings, chestplate, helmet};
    private static final ItemStack[] items = {axe, bow, snowballs, arrows};

    public InfluencerKit() {
        super("Influencer", "Influencer Kit", null, null, new ItemStackBuilder(Material.STONE_AXE).withName("&5&lInfluencer Kit").resetFlags().build(), null, items, armour, "group.influencer");
    }

    public void killAction(@Nullable User killed, @NotNull User killer) {

    }
}

package me.zxoir.pickmcffa.limitedkits;

import me.zxoir.pickmcffa.customclasses.Effect;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/9/2022
 */
public class StrengthKit extends Kit {
    private static final ItemStack helmet =  new ItemStackBuilder(Material.LEATHER_HELMET).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack chestplate = new ItemStackBuilder(Material.CHAINMAIL_CHESTPLATE).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack leggings = new ItemStackBuilder(Material.LEATHER_LEGGINGS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack boots = new ItemStackBuilder(Material.CHAINMAIL_BOOTS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack axe = new ItemStackBuilder(Material.IRON_AXE).withEnchantment(Enchantment.DAMAGE_ALL, 1).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack bow = new ItemStackBuilder(Material.BOW).withEnchantment(Enchantment.ARROW_DAMAGE, 1).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack arrows = new ItemStack(Material.ARROW, 5);
    private static final ItemStack snowballs = new ItemStack(Material.SNOW_BALL, 16);

    private static final ItemStack[] armour = {boots, leggings, chestplate, helmet};
    private static final ItemStack[] items = {axe, bow, snowballs, arrows};

    public StrengthKit() {
        super(
                "Strength",
                "Strength Kit",
                new BigDecimal(100),
                5,
                new ItemStackBuilder(new ItemStack(Material.POTION, 1, (short) 8201)).withName("&6&o&lStrength Kit").resetFlags().build(),
                null,
                items,
                armour,
                "kit.strength"
        );
    }

    @Override
    public void killAction(User killed, @NotNull User killer) {

    }
}

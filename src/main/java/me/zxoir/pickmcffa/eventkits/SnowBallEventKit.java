package me.zxoir.pickmcffa.eventkits;

import me.zxoir.pickmcffa.customclasses.Effect;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/19/2022
 */
public class SnowBallEventKit extends Kit {
    private static final ItemStack boots = new ItemStackBuilder(Material.DIAMOND_BOOTS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack shovel = new ItemStackBuilder(Material.DIAMOND_SPADE).withName("&a&lSnowball Gun").withLore("&7Right click to shoot snowballs!").isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();

    private static final ItemStack[] armour = {boots, null, null, null};
    private static final ItemStack[] items = {shovel};

    public SnowBallEventKit() {
        super(
                "SnowBall",
                "Snowball Event Kit",
                null,
                null,
                null,
                new ItemStackBuilder(Material.SNOW_BALL).withName("&6&o&lSnowball Kit").resetFlags().build(),
                new HashSet<>(Collections.singletonList(new Effect(PotionEffectType.SPEED, 1))),
                items,
                armour
        );
    }

    @Override
    public void killAction(User killed, @NotNull User killer) {

    }
}

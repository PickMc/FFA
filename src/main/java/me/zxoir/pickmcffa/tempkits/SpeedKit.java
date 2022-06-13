package me.zxoir.pickmcffa.tempkits;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Effect;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.KitManager;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/9/2022
 */
public class SpeedKit extends Kit {
    private static final ItemStack helmet = new ItemStackBuilder(Material.CHAINMAIL_HELMET).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack chestplate = new ItemStackBuilder(Material.CHAINMAIL_CHESTPLATE).withEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack leggings = new ItemStackBuilder(Material.CHAINMAIL_LEGGINGS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack boots = new ItemStackBuilder(Material.CHAINMAIL_BOOTS).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack axe = new ItemStackBuilder(Material.WOOD_SWORD).isUnbreakable(true).withFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    private static final ItemStack snowballs = new ItemStack(Material.SNOW_BALL, 16);

    private static final ItemStack[] armour = {boots, leggings, chestplate, helmet};
    private static final ItemStack[] items = {axe, snowballs};

    public SpeedKit() {
        super(
                "Speed",
                "Speed Kit",
                ConfigManager.getSpeedKitPrice(),
                ConfigManager.getSpeedKitLevel(),
                ConfigManager.getSpeedKitExpire(),
                new ItemStackBuilder(new ItemStack(Material.POTION, 1, (short) 8194)).withName("&6&o&lSpeed Kit").resetFlags().build(),
                new HashSet<>(Collections.singletonList(new Effect(PotionEffectType.SPEED, 0))),
                items,
                armour,
                "kit.speed"
        );
    }

    @Override
    public void killAction(@NotNull User killed, @NotNull User killer) {
        PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, ConfigManager.getSpeedKitEffectDuration() * 20, 1);
        killer.getPlayer().addPotionEffect(potionEffect, true);

        if (KitManager.getTempPotionTasks().containsKey(killer)) {
            KitManager.getTempPotionTasks().get(killer).cancel();
        }

        PotionEffect originEffect = new PotionEffect(PotionEffectType.SPEED, 1000000000, 0);
        BukkitTask task = new BukkitRunnable() {

            @Override
            public void run() {
                killer.getPlayer().addPotionEffect(originEffect, true);
                KitManager.getTempPotionTasks().remove(killer);
            }

        }.runTaskLater(PickMcFFA.getInstance(), 120);

        KitManager.getTempPotionTasks().put(killer, task);

    }
}

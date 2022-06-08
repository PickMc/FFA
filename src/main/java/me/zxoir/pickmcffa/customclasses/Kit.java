package me.zxoir.pickmcffa.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/19/2022
 */

@Getter
public abstract class Kit {
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @Nullable
    private final BigDecimal price;
    @Nullable
    private final Duration expireTime;
    @NotNull
    private final ItemStack[] items;
    @NotNull
    private final ItemStack[] armour;
    @NotNull
    private final ItemStack icon;
    @Nullable
    private final List<String> permissions;
    @Nullable
    private final HashSet<PotionEffect> permanentPotions;

    public Kit(@NotNull String name, @NotNull String description, @Nullable BigDecimal price, @Nullable Duration expireTime, @NotNull ItemStack icon, @Nullable List<String> permissions, @Nullable HashSet<PotionEffect> permanentPotions) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.expireTime = expireTime;
        this.icon = icon;
        this.permissions = permissions == null ? new ArrayList<>() : permissions;
        this.permanentPotions = permanentPotions == null ? new HashSet<>() : permanentPotions;
        this.items = new ItemStack[]{};
        this.armour = new ItemStack[]{};
    }

    public Kit(@NotNull String name, @NotNull String description, @Nullable BigDecimal price, @Nullable Duration expireTime, @NotNull ItemStack icon, @Nullable List<String> permissions, @Nullable HashSet<PotionEffect> permanentPotions, @NotNull ItemStack[] items, @NotNull ItemStack[] armour) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.expireTime = expireTime;
        this.icon = icon;
        this.permissions = permissions == null ? new ArrayList<>() : permissions;
        this.permanentPotions = permanentPotions == null ? new HashSet<>() : permanentPotions;
        this.items = items;
        this.armour = armour;
    }

    public abstract void killAction(@Nullable User killed, User killer);
}

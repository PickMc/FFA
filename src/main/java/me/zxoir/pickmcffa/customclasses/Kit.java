package me.zxoir.pickmcffa.customclasses;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final Integer price;
    @Nullable
    private final Integer level;
    @NotNull
    private final ItemStack[] items;
    @NotNull
    private final ItemStack[] armour;
    @NotNull
    private final ItemStack icon;
    @Nullable
    private final List<String> permissions;
    @Nullable
    private final HashSet<Effect> permanentPotions;

    public Kit(@NotNull String name, @NotNull String description, @Nullable Integer price, @Nullable Integer level, @NotNull ItemStack icon, @Nullable HashSet<Effect> permanentPotions, @Nullable String... permissions) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.level = level;
        this.icon = icon;
        this.permissions = permissions == null ? new ArrayList<>() : Arrays.asList(permissions);
        this.permanentPotions = permanentPotions == null ? new HashSet<>() : permanentPotions;
        this.items = new ItemStack[]{};
        this.armour = new ItemStack[]{};
    }

    public Kit(@NotNull String name, @NotNull String description, @Nullable Integer price, @Nullable Integer level, @NotNull ItemStack icon, @Nullable HashSet<Effect> permanentPotions, @NotNull ItemStack[] items, @NotNull ItemStack[] armour, @Nullable String... permissions) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.level = level;
        this.icon = icon;
        this.permissions = permissions == null ? new ArrayList<>() : Arrays.asList(permissions);
        this.permanentPotions = permanentPotions == null ? new HashSet<>() : permanentPotions;
        this.items = items;
        this.armour = armour;
    }

    public abstract void killAction(User killed, @NotNull User killer);
}

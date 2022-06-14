package me.zxoir.pickmcffa.customclasses;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/9/2022
 */

@Getter
public abstract class Perk {
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @Nullable
    private final Integer price;
    @Nullable
    private final Integer level;
    @Nullable
    private final Long expire;
    @NotNull
    private final ItemStack icon;
    @Nullable
    private final List<String> permissions;
    private final Random random = new Random();

    protected Perk(@NotNull String name, @NotNull String description, @Nullable Integer price, @Nullable Integer level, @Nullable Long expire, @NotNull ItemStack icon, @Nullable String... permissions) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.level = level;
        this.expire = expire;
        this.icon = icon;
        this.permissions = permissions == null ? new ArrayList<>() : Arrays.asList(permissions);
    }

    public abstract void killAction(@NotNull User killed, @NotNull User killer);

    public abstract void hitAction(@NotNull User killed, @NotNull User killer);
}

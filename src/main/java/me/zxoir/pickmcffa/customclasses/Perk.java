package me.zxoir.pickmcffa.customclasses;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final BigDecimal price;
    @Nullable
    private final Integer level;
    @NotNull
    private final ItemStack icon;
    @Nullable
    private final List<String> permissions;

    protected Perk(@NotNull String name, @NotNull String description, @Nullable BigDecimal price, @Nullable Integer level, @NotNull ItemStack icon, @Nullable String... permissions) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.level = level;
        this.icon = icon;
        this.permissions = permissions == null ? new ArrayList<>() : Arrays.asList(permissions);
    }

    public abstract void killAction(User killed, @NotNull User killer);

    public abstract void hitAction(@NotNull User killed, @NotNull User killer);
}

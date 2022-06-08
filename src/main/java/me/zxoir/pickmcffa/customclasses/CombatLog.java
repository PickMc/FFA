package me.zxoir.pickmcffa.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/8/2022
 */

@AllArgsConstructor
@Getter
@Setter
public class CombatLog {
    @Nullable
    User lastHit;
    @Nullable
    BukkitTask bukkitTask;
    User user;
}

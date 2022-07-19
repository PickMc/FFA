package me.zxoir.pickmcffa.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/22/2022
 */

@AllArgsConstructor
@Getter
@Setter
public class DelayedPotionEffect {
    PotionEffect potionEffect;
    BukkitTask bukkitTask;
}

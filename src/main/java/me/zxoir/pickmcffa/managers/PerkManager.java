package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.perks.ExplosionPerk;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/10/2022
 */
public class PerkManager {
    @Getter
    private static final ExplosionPerk explosionPerk = new ExplosionPerk();
}

package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.perks.AbsorptionPerk;
import me.zxoir.pickmcffa.perks.ExplosionPerk;
import me.zxoir.pickmcffa.perks.SpeedPerk;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/10/2022
 */
public class PerkManager {
    @Getter
    private static final ExplosionPerk explosionPerk = new ExplosionPerk();

    @Getter
    private static final AbsorptionPerk absorptionPerk = new AbsorptionPerk();

    @Getter
    private static final SpeedPerk speedPerk = new SpeedPerk();
}

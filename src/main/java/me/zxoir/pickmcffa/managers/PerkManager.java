package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.customclasses.Perk;
import me.zxoir.pickmcffa.perks.AbsorptionPerk;
import me.zxoir.pickmcffa.perks.ExplosionPerk;
import me.zxoir.pickmcffa.perks.SpeedPerk;
import org.jetbrains.annotations.Nullable;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/10/2022
 */
public class PerkManager {
    @Getter
    private static ExplosionPerk explosionPerk = new ExplosionPerk();

    @Getter
    private static AbsorptionPerk absorptionPerk = new AbsorptionPerk();

    @Getter
    private static SpeedPerk speedPerk = new SpeedPerk();

    @Nullable
    public static Perk valueOf(@Nullable String value) {
        if (value == null)
            return null;
        else if (value.equalsIgnoreCase("explosion"))
            return explosionPerk;
        else if (value.equalsIgnoreCase("absorption"))
            return absorptionPerk;
        else if (value.equalsIgnoreCase("speed"))
            return speedPerk;
        else return null;
    }

    public static void reloadPerks() {
        explosionPerk = new ExplosionPerk();
        absorptionPerk = new AbsorptionPerk();
        speedPerk = new SpeedPerk();
    }
}

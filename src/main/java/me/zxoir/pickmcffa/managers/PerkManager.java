package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.customclasses.Perk;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.perks.AbsorptionPerk;
import me.zxoir.pickmcffa.perks.ExplosionPerk;
import me.zxoir.pickmcffa.perks.SpeedPerk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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

    public static boolean hasPerk(User user, @NotNull Perk perk) {
        if (perk.getPermissions() == null || perk.getPermissions().isEmpty())
            return true;

        if (user == null || user.getPlayer() == null)
            return false;

        for (String permission : perk.getPermissions()) {
            if (!user.getPlayer().hasPermission(permission))
                return false;
        }

        return true;
    }

    public static boolean canPurchasePerk(@NotNull User user, @NotNull Perk perk) {
        Player player = user.getPlayer();
        if (player == null)
            return false;

        if (perk.getLevel() != null && perk.getLevel() > user.getStats().getLevel())
            return false;

        return perk.getPrice() == null || user.getStats().getCoins() >= perk.getPrice();
    }

    public static void reloadPerks() {
        explosionPerk = new ExplosionPerk();
        absorptionPerk = new AbsorptionPerk();
        speedPerk = new SpeedPerk();
    }
}

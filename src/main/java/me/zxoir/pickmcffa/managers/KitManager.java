package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.kits.DefaultKit;
import me.zxoir.pickmcffa.kits.InfluencerKit;
import me.zxoir.pickmcffa.kits.PremiumKit;
import me.zxoir.pickmcffa.kits.PremiumPlusKit;
import me.zxoir.pickmcffa.tempkits.SpeedKit;
import me.zxoir.pickmcffa.tempkits.StrengthKit;
import me.zxoir.pickmcffa.tempkits.TankKit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/22/2022
 */
public class KitManager {
    @Getter
    private static final HashMap<User, BukkitTask> tempPotionTasks = new HashMap<>();
    @Getter
    private static Kit defaultKit = new DefaultKit();
    @Getter
    private static Kit influencerKit = new InfluencerKit();
    @Getter
    private static Kit premiumKit = new PremiumKit();
    @Getter
    private static Kit premiumPlusKit = new PremiumPlusKit();
    @Getter
    private static Kit speedKit = new SpeedKit();
    @Getter
    private static Kit strengthKit = new StrengthKit();
    @Getter
    private static Kit tankKit = new TankKit();

    @Nullable
    public static Kit valueOf(@Nullable String value) {
        if (value == null)
            return null;
        else if (value.equalsIgnoreCase("default"))
            return defaultKit;
        else if (value.equalsIgnoreCase("influencer"))
            return influencerKit;
        else if (value.equalsIgnoreCase("premium"))
            return premiumKit;
        else if (value.equalsIgnoreCase("premiumPlus"))
            return premiumPlusKit;
        else if (value.equalsIgnoreCase("speed"))
            return speedKit;
        else if (value.equalsIgnoreCase("strength"))
            return strengthKit;
        else if (value.equalsIgnoreCase("tank"))
            return tankKit;

        else return null;
    }

    public static boolean hasKit(User user, @NotNull Kit kit) {
        if (kit.getPermissions() == null || kit.getPermissions().isEmpty())
            return true;

        if (user == null || user.getPlayer() == null)
            return false;

        for (String permission : kit.getPermissions()) {
            if (!user.getPlayer().hasPermission(permission))
                return false;
        }

        return true;
    }

    public static boolean canPurchaseKit(@NotNull User user, Kit kit) {
        Player player = user.getPlayer();
        if (player == null)
            return false;

        if (kit.getLevel() != null && kit.getLevel() > user.getStats().getLevel())
            return false;

        return kit.getPrice() == null || user.getStats().getCoins() >= kit.getPrice();
    }

    public static void reloadKits() {
        defaultKit = new DefaultKit();

        influencerKit = new InfluencerKit();

        premiumKit = new PremiumKit();

        premiumPlusKit = new PremiumPlusKit();

        speedKit = new SpeedKit();

        strengthKit = new StrengthKit();

        tankKit = new TankKit();
    }
}

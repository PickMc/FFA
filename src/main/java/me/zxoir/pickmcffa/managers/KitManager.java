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
import org.bukkit.scheduler.BukkitTask;

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

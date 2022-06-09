package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.kits.DefaultKit;
import me.zxoir.pickmcffa.kits.InfluencerKit;
import me.zxoir.pickmcffa.kits.PremiumKit;
import me.zxoir.pickmcffa.kits.PremiumPlusKit;
import me.zxoir.pickmcffa.limitedkits.SpeedKit;
import me.zxoir.pickmcffa.limitedkits.StrengthKit;
import me.zxoir.pickmcffa.limitedkits.TankKit;
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
    private static final Kit defaultKit = new DefaultKit();

    @Getter
    private static final Kit influencerKit = new InfluencerKit();

    @Getter
    private static final Kit premiumKit = new PremiumKit();

    @Getter
    private static final Kit premiumPlusKit = new PremiumPlusKit();

    @Getter
    private static final Kit speedKit = new SpeedKit();

    @Getter
    private static final Kit strengthKit = new StrengthKit();

    @Getter
    private static final Kit tankKit = new TankKit();

    @Getter
    private static final HashMap<User, BukkitTask> tempPotionTasks = new HashMap<>();
}

package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.kits.DefaultKit;
import me.zxoir.pickmcffa.kits.InfluencerKit;
import me.zxoir.pickmcffa.kits.PremiumKit;
import me.zxoir.pickmcffa.kits.PremiumPlusKit;

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
}

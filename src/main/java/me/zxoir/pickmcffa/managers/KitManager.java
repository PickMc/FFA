package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.kits.DefaultKit;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/22/2022
 */
public class KitManager {
    @Getter
    private static final Kit defaultKit = new DefaultKit();
}

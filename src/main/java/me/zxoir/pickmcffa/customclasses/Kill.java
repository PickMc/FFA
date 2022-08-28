package me.zxoir.pickmcffa.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2022
 */

@AllArgsConstructor
@Getter
public class Kill {
    @Nullable
    UUID killed;
    @Nullable
    Date date;
}

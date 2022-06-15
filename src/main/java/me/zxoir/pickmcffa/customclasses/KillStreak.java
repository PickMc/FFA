package me.zxoir.pickmcffa.customclasses;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/15/2022
 */

@Getter
public class KillStreak {
    @NotNull
    User user;
    int count;
    int sameUserCount;
    User user2;
    int sameUserCount2;

    public KillStreak(@NotNull User user) {
        this.user = user;
        this.count = 0;
        this.sameUserCount = 0;
        this.sameUserCount2 = 0;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setSameUserCount(int sameUserCount) {
        this.sameUserCount = sameUserCount;
    }

    public void setUser(@NotNull User user) {
        if (user.equals(user2)) {
            sameUserCount2++;
            return;
        }

        user2 = this.user;
        sameUserCount2 = this.sameUserCount;
        this.user = user;
        sameUserCount = 1;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public void setSameUserCount2(int sameUserCount2) {
        this.sameUserCount2 = sameUserCount2;
    }
}

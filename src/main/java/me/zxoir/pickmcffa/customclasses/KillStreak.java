package me.zxoir.pickmcffa.customclasses;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/15/2022
 */

public class KillStreak {
    @NotNull
    User user;
    AtomicInteger count = new AtomicInteger(0);
    AtomicInteger sameUserCount = new AtomicInteger(0);
    User user2;
    AtomicInteger sameUserCount2 = new AtomicInteger(0);

    public KillStreak(@NotNull User user, int count) {
        this.user = user;
        this.count.set(count);
    }

    public void setCount(int count) {
        this.count.set(count);
    }

    public void setSameUserCount(int sameUserCount) {
        this.sameUserCount.set(sameUserCount);
    }

    public void setUser(@NotNull User user) {
        if (user.equals(user2)) {
            sameUserCount2.set(sameUserCount2.get() + 1);
            return;
        }

        user2 = this.user;
        sameUserCount2.set(this.sameUserCount.get());
        this.user = user;
        sameUserCount.set(1);
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public void setSameUserCount2(int sameUserCount2) {
        this.sameUserCount2.set(sameUserCount2);
    }

    @NotNull
    public User getUser() {
        return user;
    }

    public User getUser2() {
        return user2;
    }

    public int getCount() {
        return count.get();
    }

    public int getSameUserCount() {
        return sameUserCount.get();
    }

    public int getSameUserCount2() {
        return sameUserCount2.get();
    }
}

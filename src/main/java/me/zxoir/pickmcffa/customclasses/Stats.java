package me.zxoir.pickmcffa.customclasses;

import lombok.AllArgsConstructor;
import me.zxoir.pickmcffa.managers.UserManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/19/2022
 */

@AllArgsConstructor
public class Stats implements Serializable {
    @NotNull
    UUID uuid;
    AtomicInteger xp;
    AtomicInteger level;
    AtomicInteger coins;
    AtomicInteger maxKillStreaks;
    AtomicInteger killsStreak;
    List<Kill> kills;
    AtomicInteger deaths;
    AtomicInteger eventsWon;

    public Stats(@NotNull UUID uuid) {
        this.uuid = uuid;
        xp = new AtomicInteger(0);
        level = new AtomicInteger(1);
        coins = new AtomicInteger(0);
        maxKillStreaks = new AtomicInteger(0);
        killsStreak = new AtomicInteger(0);
        kills = Collections.synchronizedList(new ArrayList<>());
        deaths = new AtomicInteger(0);
        eventsWon = new AtomicInteger(0);
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    public int getCoins() {
        return coins.get();
    }

    public int getDeaths() {
        return deaths.get();
    }

    public int getEventsWon() {
        return eventsWon.get();
    }

    public int getKillsStreak() {
        return killsStreak.get();
    }

    public int getLevel() {
        return level.get();
    }

    public int getMaxKillStreaks() {
        return maxKillStreaks.get();
    }

    public int getXp() {
        return xp.get();
    }

    public List<Kill> getKills() {
        return kills;
    }

    public void setLevel(int level) {
        this.level.set(level);
    }

    public void setDeaths(int deaths) {
        this.deaths.set(deaths);
    }

    public void setKillsStreak(int killsStreak) {
        this.killsStreak.set(killsStreak);
    }

    public void setMaxKillStreaks(int maxKillStreaks) {
        this.maxKillStreaks.set(maxKillStreaks);
    }

    public void setCoins(int coins) {
        this.coins.set(coins);
    }

    public void setXp(int xp) {
        this.xp.set(xp);
    }

    public void addCoins(int coins) {
        this.coins.set(this.coins.get() + coins);
    }

    public int addCoins(int minCoin, int maxCoin) {
        int randomCoin = Utils.getRANDOM().nextInt(maxCoin - minCoin) + minCoin;

        Player player = Bukkit.getPlayer(uuid);
        Double coinBoost = UserManager.getCoinBoost(Bukkit.getPlayer(uuid));
        if (player != null && coinBoost != null)
            randomCoin = (int) (randomCoin * coinBoost);

        if (Booster.getGlobalCoin() != null && coinBoost == null)
            randomCoin = (int) (randomCoin * Booster.getGlobalCoin());

        this.coins.set(this.coins.get() + randomCoin);
        return randomCoin;
    }

    public void deductCoins(int coins) {
        this.coins.set(Math.max(0, this.coins.get() - coins));
    }

    public int deductCoins(int minCoin, int maxCoin) {
        int randomCoin = Utils.getRANDOM().nextInt(maxCoin - minCoin) + minCoin;
        this.coins.set(Math.max(0, this.coins.get() - randomCoin));
        return randomCoin;
    }

    public void addXp(int xp) {
        this.xp.set(this.xp.get() + xp);

        if (this.xp.get() >= getLevelUpXp())
            UserManager.levelUp(uuid);
    }

    public int addXp(int minXp, int maxXp) {
        int randomXp = ThreadLocalRandom.current().nextInt(maxXp - minXp) + minXp;

        Player player = Bukkit.getPlayer(uuid);
        Double xpBoost = UserManager.getXpBoost(Bukkit.getPlayer(uuid));
        if (player != null && xpBoost != null)
            randomXp = (int) (randomXp * xpBoost);

        if (Booster.getGlobalXP() != null && xpBoost == null)
            randomXp = (int) (randomXp * Booster.getGlobalXP());

        this.xp.set(this.xp.get() + randomXp);

        if (this.xp.get() >= getLevelUpXp())
            UserManager.levelUp(uuid);

        return randomXp;
    }

    public void setEventsWon(int eventsWon) {
        this.eventsWon.set(eventsWon);
    }

    public int getLevelUpXp() {
        if (level.get() >= 25)
            return 12500;
        return level.get() * 500;
    }

    public int getLevelUpXp(int level) {
        if (level >= 25)
            return 12500;
        return level * 500;
    }
}
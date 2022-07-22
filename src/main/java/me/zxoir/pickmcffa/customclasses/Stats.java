package me.zxoir.pickmcffa.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Synchronized;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.managers.UserManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/19/2022
 */

@AllArgsConstructor
@Getter(onMethod_={@Synchronized})
public class Stats {
    @NotNull
    UUID uuid;
    int xp;
    int level;
    int coins;
    int maxKillStreaks;
    int killsStreak;
    List<Kill> kills;
    int deaths;
    int eventsWon;

    public Stats(@NotNull UUID uuid) {
        this.uuid = uuid;
        xp = 0;
        level = 1;
        coins = 0;
        maxKillStreaks = 0;
        killsStreak = 0;
        kills = Collections.synchronizedList(new ArrayList<>());
        deaths = 0;
        eventsWon = 0;
    }

    public synchronized void setLevel(int level) {
        this.level = level;
    }

    public synchronized void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public synchronized void setKillsStreak(int killsStreak) {
        this.killsStreak = killsStreak;
    }

    public synchronized void setMaxKillStreaks(int maxKillStreaks) {
        this.maxKillStreaks = maxKillStreaks;
    }

    public synchronized void setCoins(int coins) {
        this.coins = coins;
    }

    public synchronized void setXp(int xp) {
        this.xp = xp;
    }

    public synchronized void addCoins(int coins) {
        this.coins = this.coins + coins;
    }

    public synchronized int addCoins(int minCoin, int maxCoin) {
        int randomCoin = Utils.getRANDOM().nextInt(maxCoin - minCoin) + minCoin;

        Player player = Bukkit.getPlayer(uuid);
        Double coinBoost = UserManager.getCoinBoost(Bukkit.getPlayer(uuid));
        if (player != null && coinBoost != null)
            randomCoin = (int) (randomCoin * coinBoost);

        if (Booster.getGlobalCoin() != null && coinBoost == null)
            randomCoin = (int) (randomCoin * Booster.getGlobalCoin());

        this.coins = this.coins + randomCoin;
        return randomCoin;
    }

    public synchronized void deductCoins(int coins) {
        this.coins = Math.max(0, this.coins - coins);
    }

    public synchronized int deductCoins(int minCoin, int maxCoin) {
        int randomCoin = Utils.getRANDOM().nextInt(maxCoin - minCoin) + minCoin;
        this.coins = Math.max(0, this.coins - randomCoin);
        return randomCoin;
    }

    public synchronized void addXp(int xp) {
        this.xp += xp;

        if (this.xp >= getLevelUpXp())
            UserManager.levelUp(uuid);
    }

    public synchronized int addXp(int minXp, int maxXp) {
        int randomXp = Utils.getRANDOM().nextInt(maxXp - minXp) + minXp;

        Player player = Bukkit.getPlayer(uuid);
        Double xpBoost = UserManager.getXpBoost(Bukkit.getPlayer(uuid));
        if (player != null && xpBoost != null)
            randomXp = (int) (randomXp * xpBoost);

        if (Booster.getGlobalXP() != null && xpBoost == null)
            randomXp = (int) (randomXp * Booster.getGlobalXP());

        this.xp += randomXp;

        if (this.xp >= getLevelUpXp())
            UserManager.levelUp(uuid);

        return randomXp;
    }

    public synchronized void setEventsWon(int eventsWon) {
        this.eventsWon = eventsWon;
    }

    public synchronized int getLevelUpXp() {
        if (level >= 25)
            return 12500;
        return level * 500;
    }

    public synchronized int getLevelUpXp(int level) {
        if (level >= 25)
            return 12500;
        return level * 500;
    }
}
package me.zxoir.pickmcffa.customclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.zxoir.pickmcffa.managers.UserManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/19/2022
 */

@AllArgsConstructor
@Getter
public class Stats {
    @NotNull
    UUID uuid;
    String firstJoinDate;
    int xp;
    int level;
    int coins;
    int maxKillStreaks;
    int killsStreak;
    int kills;
    int deaths;

    public Stats(@NotNull UUID uuid) {
        this.uuid = uuid;
        firstJoinDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").format(new Date());
        xp = 0;
        level = 1;
        coins = 0;
        maxKillStreaks = 0;
        killsStreak = 0;
        kills = 0;
        deaths = 0;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setKillsStreak(int killsStreak) {
        this.killsStreak = killsStreak;
    }

    public void setMaxKillStreaks(int maxKillStreaks) {
        this.maxKillStreaks = maxKillStreaks;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addCoins(int coins) {
        this.coins = this.coins + coins;
    }

    public int addCoins(int minCoin, int maxCoin) {
        int randomCoin = Utils.getRANDOM().nextInt(maxCoin - minCoin) + minCoin;

        Player player = Bukkit.getPlayer(uuid);
        Integer coinBoost = UserManager.getCoinBoost(Bukkit.getPlayer(uuid));
        if (player != null && coinBoost != null)
            randomCoin = randomCoin*coinBoost;

        if (Booster.getGlobalCoin() != null && coinBoost == null)
            randomCoin = randomCoin*Booster.getGlobalCoin();

        this.coins = this.coins + randomCoin;
        return randomCoin;
    }

    public void deductCoins(int coins) {
        this.coins = Math.max(0, this.coins - coins);
    }

    public int deductCoins(int minCoin, int maxCoin) {
        int randomCoin = Utils.getRANDOM().nextInt(maxCoin - minCoin) + minCoin;
        this.coins = Math.max(0, this.coins - randomCoin);
        return randomCoin;
    }

    public void addXp(int xp) {
        this.xp += xp;

        if (this.xp >= getLevelUpXp())
            UserManager.levelUp(uuid);
    }

    public int addXp(int minXp, int maxXp) {
        int randomXp = Utils.getRANDOM().nextInt(maxXp - minXp) + minXp;

        Player player = Bukkit.getPlayer(uuid);
        Integer xpBoost = UserManager.getXpBoost(Bukkit.getPlayer(uuid));
        if (player != null && xpBoost != null)
            randomXp = randomXp*xpBoost;

        if (Booster.getGlobalXP() != null && xpBoost == null)
            randomXp = randomXp*Booster.getGlobalXP();

        this.xp += randomXp;

        if (this.xp >= getLevelUpXp())
            UserManager.levelUp(uuid);

        return randomXp;
    }

    public int getLevelUpXp() {
        if (level >= 25)
            return 12500;
        return level * 500;
    }

    public int getLevelUpXp(int level) {
        if (level >= 25)
            return 12500;
        return level * 500;
    }
}
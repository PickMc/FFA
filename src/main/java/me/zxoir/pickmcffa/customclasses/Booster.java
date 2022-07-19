package me.zxoir.pickmcffa.customclasses;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/16/2022
 */
public class Booster {
    @Getter
    private static Double globalXP;
    @Getter
    private static Double globalCoin;

    public static void setGlobalCoin(double globalCoin2, long duration) {
        globalCoin = globalCoin2;

        Bukkit.broadcastMessage(colorize("&9&l" + globalCoin2 + "x COIN BOOSTER ACTIVATED!"));

        new BukkitRunnable() {

            @Override
            public void run() {
                globalCoin = null;

                Bukkit.broadcastMessage(colorize("&c&lCOIN BOOSTER DEACTIVATED!"));
            }

        }.runTaskLater(PickMcFFA.getInstance(), duration);
    }

    public static void setGlobalXP(double globalXP2, long duration) {
        globalXP = globalXP2;

        Bukkit.broadcastMessage(colorize("&9&l" + globalXP + "x XP BOOSTER ACTIVATED!"));

        new BukkitRunnable() {

            @Override
            public void run() {
                globalXP = null;

                Bukkit.broadcastMessage(colorize("&c&lXP BOOSTER DEACTIVATED!"));
            }

        }.runTaskLater(PickMcFFA.getInstance(), duration);
    }
}
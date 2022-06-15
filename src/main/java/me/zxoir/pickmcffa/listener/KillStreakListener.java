package me.zxoir.pickmcffa.listener;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.KillStreak;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/15/2022
 */
public class KillStreakListener implements Listener {
    @Getter
    private static final HashMap<User, KillStreak> killStreak = new HashMap<>();

    @EventHandler
    public void onKill(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer == null)
            return;

        User playerUser = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        User killerUser = PickMcFFA.getCachedUsers().getIfPresent(killer.getUniqueId());

        if (playerUser == null)
            return;

        if (killStreak.containsKey(playerUser)) {
            Stats stats = playerUser.getStats();
            KillStreak playerKillStreak = killStreak.get(playerUser);

            // New Max Kill Streak Reached
            if (playerKillStreak.getCount() > stats.getMaxKillStreaks()) {
                stats.setMaxKillStreaks(playerKillStreak.getCount());
            }

            stats.setKillsStreak(0);
            killStreak.remove(playerUser);
        }

        if (killerUser == null)
            return;

        if (!killStreak.containsKey(killerUser))
            killStreak.put(killerUser, new KillStreak(playerUser));

        KillStreak killerStreak = killStreak.get(killerUser);

        // If the player kills a new player other than the last two kills
        if (!killerStreak.getUser().equals(playerUser) && (killerStreak.getUser2() != null && killerStreak.getUser2().equals(playerUser))) {
            killerStreak.setUser2(null);
            killerStreak.setSameUserCount2(0);
            killerStreak.setSameUserCount(1);
        }

        if (killerStreak.getSameUserCount() == 4 || killerStreak.getSameUserCount2() == 4) {
            Utils.sendActionText(killer, "&c&lYou are Kill Farming!");
            return;
        }

        if (killerStreak.getUser().equals(playerUser))
            killerStreak.setSameUserCount(killerStreak.getSameUserCount() + 1);
        else
            killerStreak.setUser(playerUser);

        killerStreak.setCount(killerStreak.getCount() + 1);
        killerUser.getStats().setKillsStreak(killerStreak.getCount());
    }
}

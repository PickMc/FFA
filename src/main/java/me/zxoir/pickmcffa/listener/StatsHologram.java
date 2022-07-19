package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.commands.StatsCommand;
import me.zxoir.pickmcffa.customclasses.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2022
 */
public class StatsHologram implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        if (user == null)
            return;

        if (StatsCommand.getStatsLocation() == null) return;

        StatsCommand.spawnStatsHologram(player, user.getStats());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (StatsCommand.getHolograms().containsKey(player)) {
            StatsCommand.getHolograms().get(player).delete();
        }

        StatsCommand.getHolograms().remove(player);
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (StatsCommand.getHolograms().containsKey(player)) {
            StatsCommand.getHolograms().get(player).delete();
        }

        StatsCommand.getHolograms().remove(player);
    }
}

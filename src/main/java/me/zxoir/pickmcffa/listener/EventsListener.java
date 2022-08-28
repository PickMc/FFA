package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.EventsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/19/2022
 */
public class EventsListener implements Listener {

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        EventsManager.getPlayersAlive().remove(event.getPlayer());
        EventsManager.getToRemoveKit().remove(event.getPlayer());

        if (EventsManager.isEventActive() && EventsManager.getPlayersAlive().contains(player))
            EventsManager.eliminate(player, null);
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        EventsManager.getPlayersAlive().remove(event.getPlayer());
        EventsManager.getToRemoveKit().remove(event.getPlayer());

        if (EventsManager.isEventActive() && EventsManager.getPlayersAlive().contains(player))
            EventsManager.eliminate(player, null);
    }

    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (EventsManager.getToRemoveKit().contains(player)) {
            User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

            if (user == null) {
                EventsManager.getToRemoveKit().remove(player);
                return;
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(PickMcFFA.getInstance(), () -> {
                if (EventsManager.getToRemoveKit().contains(player)) {
                    user.setSelectedKit(null);
                    EventsManager.getToRemoveKit().remove(player);
                }
            }, 10);
        }
    }
}

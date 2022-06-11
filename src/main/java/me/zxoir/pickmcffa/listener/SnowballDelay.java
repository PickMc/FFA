package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/26/2022
 */
public class SnowballDelay implements Listener {
    private final HashMap<Player, Instant> snowBallCooldowns = new HashMap<>();

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        snowBallCooldowns.remove(event.getPlayer());
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        snowBallCooldowns.remove(event.getPlayer());
    }

    @EventHandler
    public void onSnowball(@NotNull ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();

        // If the type is not snow, return
        if (entity == null || !entity.getType().equals(EntityType.SNOWBALL))
            return;

        // If the shooter is not a player, return
        if (entity.getShooter() == null || !(entity.getShooter() instanceof Player))
            return;

        Player shooter = (Player) entity.getShooter();

        // If the player is not in the hashmap, add him
        if (!snowBallCooldowns.containsKey(shooter)) {
            snowBallCooldowns.put(shooter, Instant.now().plusMillis(100));
            return;
        }

        // If the cool down is over, add it back
        Instant instant = snowBallCooldowns.get(shooter);
        if (Instant.now().isAfter(instant)) {
            snowBallCooldowns.put(shooter, Instant.now().plusMillis(100));
            return;
        }

        if (ConfigManager.isDebug())
            Utils.sendActionText(shooter, "&c&lERROR > &8Snowball Cancelled"); // For debug purposes

        event.setCancelled(true);
    }
}

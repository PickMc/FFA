package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/26/2022
 */
public class LaunchPad implements Listener {
    private final ArrayList<UUID> inAir = new ArrayList<>();

    @EventHandler
    public void onLaunch(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block clickedBlock = event.getClickedBlock();

        // If the action is not physical, return
        if (!action.equals(Action.PHYSICAL)) return;

        // If the player didn't activate a pressure plate, return
        if (isNotPressurePlate(clickedBlock)) return;

        event.setCancelled(true);
        Vector vector = player.getEyeLocation().getDirection().multiply(3D).setX(0.1D).setY(1.5D);
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 3, 10);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PickMcFFA.getInstance(), () -> {
            player.setVelocity(vector);

            // If the player isn't already in the list
            if (!inAir.contains(player.getUniqueId()))
                inAir.add(player.getUniqueId());
        }, 1L);
    }

    @EventHandler
    public void checkGround(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // If the player is not in air after pressure plate, return
        if (!inAir.contains(player.getUniqueId())) return;

        // If the player is in air
        if (player.getVelocity().getY() > 0) return;

        // If the player is in air
        Block relativeBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (relativeBlock == null || relativeBlock.getType() == Material.AIR) return;

        inAir.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerDamage(@NotNull EntityDamageEvent event) {
        // If the entity is not a player, return
        if (!(event.getEntity() instanceof Player) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
        Player player = (Player) event.getEntity();

        // If the player is not in air after launch pad, return
        if (!inAir.contains(player.getUniqueId())) return;

        event.setCancelled(true);
    }

    private boolean isNotPressurePlate(@NotNull Block clickedBlock) {
        return !(clickedBlock.getType().equals(Material.STONE_PLATE) || clickedBlock.getType().equals(Material.GOLD_PLATE) || clickedBlock.getType().equals(Material.IRON_PLATE));
    }
}

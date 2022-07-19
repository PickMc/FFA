package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/9/2022
 */
public class TempFireListener implements Listener {

    @EventHandler
    public void onBlockBurn(@NotNull BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockIgnite(@NotNull BlockIgniteEvent event) {
        if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onFirePlace(@NotNull BlockPlaceEvent event) {

        if (!event.getItemInHand().getType().equals(Material.FLINT_AND_STEEL))
            return;

        if (!event.getBlockAgainst().getType().equals(Material.GRASS) && !event.getBlockAgainst().getType().equals(Material.DIRT)) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> {
            if (event.getBlock() == null || !event.getBlock().getType().equals(Material.FIRE))
                return;

            event.getBlock().setType(Material.AIR);
        }, ConfigManager.getFireTickSpeed() * 20L);
    }
}

package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/19/2022
 */
public class KillActionListener implements Listener {

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer == null)
            return;

        User playerUser = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        User killerUser = PickMcFFA.getCachedUsers().getIfPresent(killer.getUniqueId());

        if (playerUser == null) {
            player.kickPlayer(ConfigManager.getFailedProfileSave());
        }

        if (killerUser == null) {
            killer.kickPlayer(ConfigManager.getFailedProfileSave());
            return;
        }

        if (killerUser.getSelectedKit() != null)
            killerUser.getSelectedKit().killAction(playerUser, killerUser);

        boolean isPremiumPlus = player.hasPermission("group.premiumplus");
        boolean isPremium = player.hasPermission("group.premium");
        boolean isInfluencer = player.hasPermission("group.influencer");
        ItemStack snowballs = getSnowballs(player.getInventory());

        if (isPremiumPlus) {

            killer.setHealth(Math.min(20, killer.getHealth() + 12));
            if (snowballs == null)
                player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 24));
            else
                snowballs.setAmount(24);
            player.getInventory().addItem(new ItemStack(Material.ARROW, 2));

        } else if (isPremium) {
            killer.setHealth(Math.min(20, killer.getHealth() + 10));
            if (snowballs == null)
                player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 20));
            else
                snowballs.setAmount(20);
            player.getInventory().addItem(new ItemStack(Material.ARROW, 2));
        } else if (isInfluencer) {
            killer.setHealth(Math.min(20, killer.getHealth() + 10));
            if (snowballs == null)
                player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 20));
            else
                snowballs.setAmount(20);
            player.getInventory().addItem(new ItemStack(Material.ARROW, 2));
        } else {
            killer.setHealth(Math.min(20, killer.getHealth() + 8));
            if (snowballs == null)
                player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
            else
                snowballs.setAmount(16);
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        }

        event.getDrops().clear();
    }

    @Nullable
    private ItemStack getSnowballs(PlayerInventory inventory) {
        boolean snowballsFound = false;
        ItemStack snowballs = null;

        for (ItemStack itemStack : inventory.getContents()) {

            if (itemStack != null && itemStack.getType().equals(Material.SNOW_BALL)) {

                if (!snowballsFound) {
                    snowballsFound = true;
                    snowballs = itemStack;
                } else inventory.remove(itemStack);

            }

        }

        return snowballs;
    }
}

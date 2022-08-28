package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.EventsManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer;

        if (player.getKiller() == null) {

            Entity lastDamage = player.getLastDamageCause().getEntity();

            if (lastDamage == null || !lastDamage.getType().equals(EntityType.PLAYER))
                return;

            killer = (Player) lastDamage;

        } else
            killer = player.getKiller();

        event.getDrops().clear();

        if (player.equals(killer))
            return;

        Bukkit.getScheduler().runTaskAsynchronously(PickMcFFA.getInstance(), () -> {

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
                Utils.runTaskSync(() -> killerUser.getSelectedKit().killAction(playerUser, killerUser));

            boolean isPremiumPlus = killer.hasPermission("group.pickplus");
            boolean isPremium = killer.hasPermission("group.pick");
            boolean isInfluencer = killer.hasPermission("group.influencer");
            ItemStack snowballs = getSnowballs(killer.getInventory());

            if (isPremiumPlus) {

                Utils.runTaskSync(() -> killer.setHealth(Math.min(20, killer.getHealth() + 12)));

                if (EventsManager.isEventActive())
                    return;
                if (snowballs == null)
                    Utils.runTaskSync(() -> killer.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 24)));
                else
                    Utils.runTaskSync(() -> snowballs.setAmount(24));

                if (killer.getInventory().contains(Material.BOW) && !killer.getInventory().contains(Material.ARROW, 32))
                    Utils.runTaskSync(() -> killer.getInventory().addItem(new ItemStack(Material.ARROW, 2)));

            } else if (isPremium) {
                Utils.runTaskSync(() -> killer.setHealth(Math.min(20, killer.getHealth() + 10)));

                if (EventsManager.isEventActive())
                    return;
                if (snowballs == null)
                    Utils.runTaskSync(() -> killer.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 20)));
                else
                    Utils.runTaskSync(() -> snowballs.setAmount(20));

                if (killer.getInventory().contains(Material.BOW) && !killer.getInventory().contains(Material.ARROW, 32))
                    Utils.runTaskSync(() -> killer.getInventory().addItem(new ItemStack(Material.ARROW, 2)));
            } else if (isInfluencer) {
                Utils.runTaskSync(() -> killer.setHealth(Math.min(20, killer.getHealth() + 10)));

                if (EventsManager.isEventActive())
                    return;
                if (snowballs == null)
                    Utils.runTaskSync(() -> killer.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 20)));
                else
                    Utils.runTaskSync(() -> snowballs.setAmount(20));

                if (killer.getInventory().contains(Material.BOW) && !killer.getInventory().contains(Material.ARROW, 32))
                    Utils.runTaskSync(() -> killer.getInventory().addItem(new ItemStack(Material.ARROW, 2)));
            } else {
                Utils.runTaskSync(() -> killer.setHealth(Math.min(20, killer.getHealth() + 8)));

                if (EventsManager.isEventActive())
                    return;
                if (snowballs == null)
                    Utils.runTaskSync(() -> killer.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16)));
                else
                    Utils.runTaskSync(() -> snowballs.setAmount(16));

                if (killer.getInventory().contains(Material.BOW) && !killer.getInventory().contains(Material.ARROW, 32))
                    Utils.runTaskSync(() -> killer.getInventory().addItem(new ItemStack(Material.ARROW, 1)));
            }

        });
    }

    @Nullable
    private ItemStack getSnowballs(@NotNull PlayerInventory inventory) {
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

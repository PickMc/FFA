package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/10/2022
 */
public class PerkListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
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

        if (killerUser.getSelectedPerk() != null)
            killerUser.getSelectedPerk().killAction(playerUser, killerUser);
    }

    @EventHandler
    public void onHit(@NotNull EntityDamageByEntityEvent event) {
        // If the entity or the damager is not a player, return
        if (!event.getEntity().getType().equals(EntityType.PLAYER) || !event.getDamager().getType().equals(EntityType.PLAYER) || event.getEntity().equals(event.getDamager()))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        // If the damage is cancelled, return
        if (event.isCancelled() || event.getFinalDamage() == 0)
            return;

        User playerUser = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        User damagerUser = PickMcFFA.getCachedUsers().getIfPresent(damager.getUniqueId());

        if (playerUser == null) {
            player.kickPlayer(ConfigManager.getFailedProfileSave());
            return;
        }

        if (damagerUser == null) {
            damager.kickPlayer(ConfigManager.getFailedProfileSave());
            return;
        }

        if (damagerUser.getSelectedPerk() != null)
            damagerUser.getSelectedPerk().hitAction(playerUser, damagerUser);
    }

}

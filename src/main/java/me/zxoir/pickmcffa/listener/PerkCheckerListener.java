package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Perk;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.PerkManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/14/2022
 */
public class PerkCheckerListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        Perk perk = user.getSelectedPerk();
        if (perk == null)
            return;

        if (PerkManager.hasPerk(user, perk))
            return;

        user.setSelectedPerk(null);
        player.sendMessage(ConfigManager.getPerkExpireMessage(perk.getName()));
    }

    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        Perk perk = user.getSelectedPerk();
        if (perk == null)
            return;

        if (PerkManager.hasPerk(user, perk))
            return;

        user.setSelectedPerk(null);
        player.sendMessage(ConfigManager.getPerkExpireMessage(perk.getName()));
    }

    @EventHandler
    public void onFallDamage(@NotNull EntityDamageEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER) || !event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            return;

        /* Checking if the player has a Perk selected */
        Player player = (Player) event.getEntity();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        Perk perk = user.getSelectedPerk();

        if (perk == null)
            return;

        if (PerkManager.hasPerk(user, perk))
            return;

        user.setSelectedPerk(null);
        player.sendMessage(ConfigManager.getPerkExpireMessage(perk.getName()));
    }
}

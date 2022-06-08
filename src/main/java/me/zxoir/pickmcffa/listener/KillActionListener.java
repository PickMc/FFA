package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

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

        event.getDrops().clear();
    }
}

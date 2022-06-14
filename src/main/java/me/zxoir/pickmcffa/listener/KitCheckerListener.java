package me.zxoir.pickmcffa.listener;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Kit;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.KitManager;
import org.bukkit.Location;
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
public class KitCheckerListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        Kit kit = user.getSelectedKit();
        if (kit == null)
            return;

        if (KitManager.hasKit(user, kit))
            return;

        user.setSelectedKit(null);
        player.sendMessage(ConfigManager.getKitExpireMessage(kit.getName()));
    }

    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        Kit kit = user.getSelectedKit();
        if (kit == null)
            return;

        if (KitManager.hasKit(user, kit))
            return;

        user.setSelectedKit(null);
        player.sendMessage(ConfigManager.getKitExpireMessage(kit.getName()));
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

        Kit kit = user.getSelectedKit();

        if (kit == null)
            return;

        if (KitManager.hasKit(user, kit))
            return;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(player.getLocation());
        Location spawn = new Location(player.getWorld(), 1.5, 73, 75.5, -180, 0);

        // If the player is in a pvp area, teleport to Spawn
        if (set.testState(localPlayer, DefaultFlag.PVP))
            player.teleport(spawn);

        user.setSelectedKit(null);
        player.sendMessage(ConfigManager.getKitExpireMessage(kit.getName()));
    }

    /* Figured this might be annoying, keeping it just in case
    @EventHandler
    public void onDeathOrKill(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user != null) {
            Kit kit = user.getSelectedKit();
            if (kit != null && !KitManager.hasKit(user, kit))
                user.setSelectedKit(null);
        }

        if (killer == null)
            return;

        User killerUser = PickMcFFA.getCachedUsers().getIfPresent(killer.getUniqueId());
        if (killerUser == null)
            return;

        Kit kit = killerUser.getSelectedKit();
        if (kit == null)
            return;

        if (KitManager.hasKit(killerUser, kit))
            return;

        killerUser.setSelectedKit(null);
    }*/
}

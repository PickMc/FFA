package me.zxoir.pickmcffa.listener;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.database.UsersDBManager;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/20/2022
 */
public class GeneralListener implements Listener {

    /* Register new User */
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (PickMcFFA.getCachedUsers().asMap().containsKey(player.getUniqueId())) {
            PickMcFFA.getCachedUsers().asMap().get(player.getUniqueId()).setSelectedKit(null);
            return;
        }

        User user = new User(player);
        UsersDBManager.saveToDB(user);
        PickMcFFA.getCachedUsers().put(player.getUniqueId(), user);
    }

    /* Save progress on log out */
    @EventHandler
    public void onLeave(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        user.setSelectedKit(null);
        user.save();
    }

    /* Save progress when kicked */
    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        user.setSelectedKit(null);
        user.save();
    }

    /* Registering stats */
    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        event.setDeathMessage(null);

        if (killer == null)
            return;

        User playerUser = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        User killerUser = PickMcFFA.getCachedUsers().getIfPresent(killer.getUniqueId());

        if (playerUser == null)
            player.kickPlayer(ConfigManager.getFailedProfileSave());
        else {
            Stats stats = playerUser.getStats();
            stats.setDeaths(stats.getDeaths() + 1);
            int deductedCoins = stats.deductCoins(5, 10);
            playerUser.save();
            player.sendMessage(ConfigManager.getKilledMessage(killer.getName(), player.getName(), deductedCoins));
            Utils.sendActionText(player, ConfigManager.getKilledActionbar(killer.getName(), player.getName(), deductedCoins));
        }

        if (killerUser == null)
            killer.kickPlayer(ConfigManager.getFailedProfileSave());
        else {
            Stats stats = killerUser.getStats();
            stats.setKills(stats.getKills() + 1);
            int gainedCoins = stats.addCoins(15, 20);
            int xpGained = stats.addXp(50, 150);
            killerUser.save();
            killer.sendMessage(ConfigManager.getKillMessage(killer.getName(), player.getName(), gainedCoins, xpGained));
            Utils.sendActionText(killer, ConfigManager.getKillActionbar(killer.getName(), player.getName(), gainedCoins, xpGained));
        }
    }

    /* Regive Kit on respawn */
    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> user.setSelectedKit(user.getSelectedKit()), 1);
    }

    /* Remove Hunger */
    @EventHandler
    public void onHungerLoss(@NotNull FoodLevelChangeEvent event) {
        // If the entity is not a player, return
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;

        event.setFoodLevel(20);
    }

    /* Remove fall damage */
    @EventHandler
    public void onFallDamage(@NotNull EntityDamageEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER) || !event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            return;
        event.setCancelled(true);

        /* Checking if the player has a kit selected */
        Player player = (Player) event.getEntity();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        if (user.getSelectedKit() != null)
            return;

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(player.getLocation());

        // If the player is in a non pvp area, return
        if (!set.testState(localPlayer, DefaultFlag.PVP))
            return;

        Location location = new Location(player.getWorld(), 1.5, 73, 75.5, -180, 0);
        player.teleport(location);
        Utils.sendActionText(player, ConfigManager.getNoKitError());
        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 2, 10);
    }
}

package me.zxoir.pickmcffa.listener;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.CombatLog;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/8/2022
 */
public class CombatLogListener implements Listener {
    @Getter
    private static final ConcurrentHashMap<User, CombatLog> combatLogs = new ConcurrentHashMap<>();

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        if (!combatLogs.containsKey(user))
            return;

        CombatLog combatLog = combatLogs.get(user);
        if (combatLog.getBukkitTask() != null)
            combatLog.getBukkitTask().cancel();

        player.setHealth(0);
        User killerUser = combatLogs.get(user).getLastHit();

        if (killerUser != null) {
            combatLogs.remove(user);
            return;
        }

        // Checking if anyone has hit the player
        runAsync(() -> {

            for (CombatLog log : combatLogs.values()) {
                User lastHit = log.getUser();

                if (log.getLastHit() != null && log.getLastHit().equals(user)) {
                    Stats stats = user.getStats();
                    stats.setDeaths(user.getStats().getDeaths() + 1);
                    stats.deductCoins(5, 10);
                    user.save();

                    stats = lastHit.getStats();
                    stats.setKills(stats.getKills() + 1);
                    int gainedCoins = stats.addCoins(15, 20);
                    int xpGained = stats.addXp(50, 150);
                    lastHit.save();

                    if (lastHit.getPlayer() == null || !lastHit.getOfflinePlayer().isOnline()) {
                        combatLogs.remove(user);
                        return;
                    }

                    Utils.runTaskSync(() -> lastHit.getPlayer().sendMessage(ConfigManager.getKillMessage(lastHit.getPlayer().getName(), player.getName(), gainedCoins, xpGained)));
                    Utils.runTaskSync(() -> Utils.sendActionText(lastHit.getPlayer(), ConfigManager.getKillActionbar(lastHit.getPlayer().getName(), player.getName(), gainedCoins, xpGained)));
                    return;
                }

            }
            combatLogs.remove(user);

        });
    }

    @EventHandler
    public void onQuit(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        if (!combatLogs.containsKey(user))
            return;

        CombatLog combatLog = combatLogs.get(user);
        if (combatLog.getBukkitTask() != null)
            combatLog.getBukkitTask().cancel();

        player.setHealth(0);
        User killerUser = combatLogs.get(user).getLastHit();

        if (killerUser != null) {
            combatLogs.remove(user);
            return;
        }

        // Checking if anyone has hit the player
        runAsync(() -> {

            for (CombatLog log : combatLogs.values()) {
                User lastHit = log.getUser();

                if (log.getLastHit() != null && log.getLastHit().equals(user)) {
                    Stats stats = user.getStats();
                    stats.setDeaths(user.getStats().getDeaths() + 1);
                    stats.deductCoins(5, 10);
                    user.save();

                    stats = lastHit.getStats();
                    stats.setKills(stats.getKills() + 1);
                    int gainedCoins = stats.addCoins(15, 20);
                    int xpGained = stats.addXp(50, 150);
                    lastHit.save();

                    if (lastHit.getPlayer() == null || !lastHit.getOfflinePlayer().isOnline()) {
                        combatLogs.remove(user);
                        return;
                    }

                    Utils.runTaskSync(() -> lastHit.getPlayer().sendMessage(ConfigManager.getKillMessage(lastHit.getPlayer().getName(), player.getName(), gainedCoins, xpGained)));
                    Utils.runTaskSync(() -> Utils.sendActionText(lastHit.getPlayer(), ConfigManager.getKillActionbar(lastHit.getPlayer().getName(), player.getName(), gainedCoins, xpGained)));
                    break;
                }

            }
            combatLogs.remove(user);

        });
    }

    @EventHandler
    public void onShoot(@NotNull EntityDamageByEntityEvent event) {
        // Checking if a player got shot by a bow
        if (!event.getDamager().getType().equals(EntityType.ARROW) || !event.getEntity().getType().equals(EntityType.PLAYER) || !(((Arrow) event.getDamager()).getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

        // If the player hits himself, return
        if (player.equals(damager))
            return;

        // If the damage is cancelled, return
        if (event.isCancelled() || event.getFinalDamage() == 0)
            return;

        startCombatLog(player, damager);
    }

    @EventHandler
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        combatLogs.remove(user);
    }

    @EventHandler
    public void onSnowball(@NotNull EntityDamageByEntityEvent event) {
        // Checking if a player got shot by a bow
        if (!event.getDamager().getType().equals(EntityType.SNOWBALL) || !event.getEntity().getType().equals(EntityType.PLAYER) || !(((Snowball) event.getDamager()).getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) ((Snowball) event.getDamager()).getShooter();

        // If the player hits himself, return
        if (player.equals(damager))
            return;

        // If the damage is cancelled, return
        if (event.isCancelled())
            return;

        startCombatLog(player, damager);
    }

    @EventHandler
    public void onDamage(@NotNull EntityDamageByEntityEvent event) {
        // If the entity or the damager is not a player, return
        if (!event.getEntity().getType().equals(EntityType.PLAYER) || !event.getDamager().getType().equals(EntityType.PLAYER) || event.getEntity().equals(event.getDamager()))
            return;

        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        // If the damage is cancelled, return
        if (event.isCancelled() || event.getFinalDamage() == 0)
            return;

        startCombatLog(player, damager);
    }

    private void startCombatLog(@NotNull Player player, @NotNull Player damager) {
        User playerUser = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        User damageUser = PickMcFFA.getCachedUsers().getIfPresent(damager.getUniqueId());

        if (playerUser == null || damageUser == null)
            return;

        CombatLog playerCombatlog = combatLogs.get(playerUser);
        if (combatLogs.containsKey(playerUser) && playerCombatlog.getBukkitTask() != null)
            playerCombatlog.getBukkitTask().cancel();

        CombatLog damagerCombatlog = combatLogs.get(damageUser);
        if (combatLogs.containsKey(damageUser) && damagerCombatlog.getBukkitTask() != null)
            damagerCombatlog.getBukkitTask().cancel();

        combatLogs.put(playerUser, new CombatLog(damageUser, timerTask(player, playerUser, damager.getName()), playerUser));

        if (combatLogs.containsKey(damageUser))
            combatLogs.put(damageUser, new CombatLog(damagerCombatlog.getLastHit(), timerTask(damager, damageUser, player.getName()), damageUser));
        else
            combatLogs.put(damageUser, new CombatLog(playerUser, timerTask(damager, damageUser, player.getName()), damageUser));

        if (!ConfigManager.getCombatMessage(damager.getName()).isEmpty())
            player.sendMessage(ConfigManager.getCombatMessage(damager.getName()));

        if (!ConfigManager.getCombatMessage(damager.getName()).isEmpty())
            damager.sendMessage(ConfigManager.getCombatMessage(player.getName()));

        if (ConfigManager.isCombatCountdown())
            return;

        if (!ConfigManager.getCombatActionbar(damager.getName()).isEmpty())
            Utils.sendActionText(player, ConfigManager.getCombatActionbar(damager.getName()));

        if (!ConfigManager.getCombatActionbar(damager.getName()).isEmpty())
            Utils.sendActionText(damager, ConfigManager.getCombatActionbar(player.getName()));
    }

    @Nullable
    private BukkitTask timerTask(Player player, User user, String damagerName) {
        return ConfigManager.isCombatCountdown() ? new BukkitRunnable() {
            long time = ConfigManager.getCombatTime();

            @Override
            public void run() {

                if (!player.isOnline() || !combatLogs.containsKey(user)) {
                    this.cancel();
                    return;
                }

                if (time <= 0) {
                    Utils.runTaskSync(() -> Utils.sendActionText(player, ConfigManager.getCombatlogFinishMessage(damagerName)));
                    combatLogs.remove(user);
                    this.cancel();
                    return;
                }

                Utils.runTaskSync(() -> Utils.sendActionText(player, ConfigManager.getCombatActionbar(damagerName, time + 1)));
                time--;
            }

        }.runTaskTimerAsynchronously(PickMcFFA.getInstance(), 0, 20L) : null;
    }

}
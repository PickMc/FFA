package me.zxoir.pickmcffa.listener;

import me.neznamy.tab.shared.TAB;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.commands.StatsCommand;
import me.zxoir.pickmcffa.customclasses.Kill;
import me.zxoir.pickmcffa.customclasses.KillStreak;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.database.UsersDBManager;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.EventsManager;
import me.zxoir.pickmcffa.menus.KitInventoryHolder;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.runAsync;
import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/20/2022
 */
public class GeneralListener implements Listener {

    public GeneralListener() {
        World world = Bukkit.getWorld("world");
        world.setDifficulty(Difficulty.NORMAL);
        world.setTime(1000);
        world.setThundering(false);
        world.setStorm(false);
        world.setThunderDuration(1);

        setWorldTimer(world);
    }

    private void setWorldTimer(World world) {
        Bukkit.getScheduler().runTaskTimer(PickMcFFA.getInstance(), () -> world.setTime(1000), 0, 6000L);
    }

    @EventHandler
    public void onWeatherChange(@NotNull WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
        event.getWorld().setTime(1000);
        event.getWorld().setThundering(false);
        event.getWorld().setThunderDuration(1);
    }

    @EventHandler
    public void arrowEvent(@NotNull ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            arrow.remove();
        }
    }

    @EventHandler
    public void onCraft(@NotNull PrepareItemCraftEvent event) {
        event.getInventory().setResult(new ItemStack(Material.AIR));
    }

    /* Register new User */
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(colorize("&a+ &7| &a" + player.getName()));

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        if (user != null) {
            user.setSelectedKit(null);
            return;
        }

        user = new User(player);
        UsersDBManager.saveToDB(user);
        PickMcFFA.getCachedUsers().put(player.getUniqueId(), user);
    }

    /* Save progress on log out */
    @EventHandler
    public void onLeave(@NotNull PlayerQuitEvent event) {
        Player player = event.getPlayer();

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        event.setQuitMessage(colorize("&c- &7| &c" + player.getName()));

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

    @EventHandler
    public void onThrow(@NotNull PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        InventoryView openInventory = player.getOpenInventory();

        if (openInventory != null && openInventory.getTopInventory() != null && openInventory.getTopInventory().getHolder() != null && openInventory.getTopInventory().getHolder().getClass().equals(KitInventoryHolder.class))
            return;

        event.setCancelled(true);
    }

    /* Disable Snowball/Bow/FlintnSteel in Spawn */
    @EventHandler
    public void onSnowball(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();

        if (Utils.isInPvpArea(player))
            return;

        if (!itemStack.getType().equals(Material.SNOW_BALL) && !itemStack.getType().equals(Material.BOW) && !itemStack.getType().equals(Material.FLINT_AND_STEEL))
            return;

        event.setCancelled(true);
        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), player::updateInventory, 1);
    }

    /* Insta kill Player when enterting void */
    @EventHandler
    public void onVoidDeath(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            ((Player) entity).setHealth(0);
        }
    }

    /* Registering stats */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer;

        event.setDeathMessage(null);

        if (player.getKiller() == null) {

            Entity lastDamage = player.getLastDamageCause().getEntity();

            if (lastDamage == null || !lastDamage.getType().equals(EntityType.PLAYER)) {

                if (EventsManager.isEventActive())
                    EventsManager.eliminate(player, null);

                return;
            }

            killer = (Player) lastDamage;

        } else
            killer = player.getKiller();

        if (player.equals(killer))
            return;

        try {
            runAsync(() -> {

                User playerUser = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
                User killerUser = PickMcFFA.getCachedUsers().getIfPresent(killer.getUniqueId());

                if (EventsManager.isEventActive() && EventsManager.getPlayersAlive().contains(player))
                    EventsManager.eliminate(player, killer);

                if (EventsManager.isEventActive())
                    return;

                if (playerUser == null)
                    Utils.runTaskSync(() -> player.kickPlayer(ConfigManager.getFailedProfileSave()));
                else {
                    Stats stats = playerUser.getStats();
                    stats.setDeaths(stats.getDeaths() + 1);
                    int deductedCoins = stats.deductCoins(5, 10);
                    playerUser.save();
                    StatsCommand.refreshPlayerHologram(player);
                    ScoreboardListener.updateScoreBoard(player);
                    Utils.runTaskSync(() -> player.sendMessage(ConfigManager.getKilledMessage(killer.getName(), player.getName(), deductedCoins, killer.getHealth())));
                    Utils.sendActionText(player, ConfigManager.getKilledActionbar(killer.getName(), player.getName(), deductedCoins));
                }

                if (killerUser == null)
                    Utils.runTaskSync(() -> killer.kickPlayer(ConfigManager.getFailedProfileSave()));
                else {
                    if (KillStreakListener.getKillStreak().containsKey(killerUser)) {
                        KillStreak killStreak = KillStreakListener.getKillStreak().get(killerUser);
                        if (killStreak.getSameUserCount() == 4 || killStreak.getSameUserCount2() == 4)
                            return;
                    }

                    Stats stats = killerUser.getStats();
                    Kill kill = new Kill(player.getUniqueId(), new Date());
                    stats.getKills().add(kill);
                    int gainedCoins;
                    int xpGained;
                    if (KillStreakListener.getBountyList().containsKey(player.getUniqueId())) {
                        gainedCoins = stats.addCoins(45, 60);
                        xpGained = stats.addXp(150, 450);
                        KillStreakListener.getBountyList().get(player.getUniqueId()).cancel();
                        KillStreakListener.getBountyList().remove(player.getUniqueId());
                        TAB.getInstance().getTeamManager().resetPrefix(TAB.getInstance().getPlayer(player.getUniqueId()));
                    } else {
                        gainedCoins = stats.addCoins(15, 20);
                        xpGained = stats.addXp(50, 150);
                    }
                    killerUser.save();
                    StatsCommand.refreshPlayerHologram(killer);
                    ScoreboardListener.updateScoreBoard(killer);
                    Utils.runTaskSync(() -> killer.sendMessage(ConfigManager.getKillMessage(killer.getName(), player.getName(), gainedCoins, xpGained)));
                    Utils.sendActionText(killer, ConfigManager.getKillActionbar(killer.getName(), player.getName(), gainedCoins, xpGained));
                }

            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            player.sendMessage("A bug has occured! Please DM Zxoir on Discord about this bug!");
        }

    }

    /* Show player hearts on Arrow Hit */
    @EventHandler
    public void onArrowHit(@NotNull EntityDamageByEntityEvent event) {
        if (event.getFinalDamage() <= 0 || event.isCancelled())
            return;

        if (event.getDamager() instanceof Arrow) {
            Arrow sn = (Arrow) event.getDamager();
            if (sn.getShooter() instanceof Player && event.getEntity() instanceof Player) {
                Player shooter = (Player) sn.getShooter();
                Player player = (Player) event.getEntity();

                if (shooter.equals(player))
                    return;

                shooter.sendMessage(ConfigManager.getArrowHitMessage(player.getName(), player.getHealth()));
            }
        }

    }

    /* Regive Kit on respawn */
    @EventHandler
    public void onRespawn(@NotNull PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> {
            user.setSelectedKit(user.getSelectedKit());

            if (user.getSelectedKit() != null && user.getSavedInventories().containsKey(user.getSelectedKit()))
                user.setSelectedKit(user.getSelectedKit(), user.getSavedInventories().get(user.getSelectedKit()));
            else
                user.setSelectedKit(user.getSelectedKit());

        }, 1);
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

        // If the player is in a non pvp area, return
        if (!Utils.isInPvpArea(player))
            return;

        if (EventsManager.isEventActive())
            return;

        Location location = new Location(player.getWorld(), 1.5, 73, 75.5, -180, 0);
        player.teleport(location);
        Utils.sendActionText(player, ConfigManager.getNoKitError());
        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 2, 10);
    }
}

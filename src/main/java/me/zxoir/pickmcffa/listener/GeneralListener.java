package me.zxoir.pickmcffa.listener;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import me.neznamy.tab.shared.TAB;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.commands.StatsCommand;
import me.zxoir.pickmcffa.customclasses.Kill;
import me.zxoir.pickmcffa.customclasses.KillStreak;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.database.UsersDBManager;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.menus.KitInventoryHolder;
import me.zxoir.pickmcffa.utils.ItemDeserializer;
import me.zxoir.pickmcffa.utils.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

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

    /* Register new User */
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(colorize("&a+ &7| &a" + player.getName()));

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

    /*@EventHandler
    public void onChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (event.getMessage().equalsIgnoreCase("pot")) {
            if (player.getActivePotionEffects().isEmpty())
                return;

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.sendMessage(effect.getDuration() + "");
            }
        }

        if (event.getMessage().equalsIgnoreCase("arrows")) {
            player.sendMessage(player.getInventory().contains(Material.ARROW, 32) + "");
        }

        if (event.getMessage().equalsIgnoreCase("tnt")) {
            player.getInventory().addItem(KillStreakListener.getTntItem());
        }

        if (event.getMessage().equalsIgnoreCase("testi")) {
            BukkitTask task = new BukkitRunnable() {

                public void run() {
                    PacketPlayOutWorldParticles packet =
                            new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(),
                                    0.5f, 0.5f, 0.5f, 5, 60);

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                    }
                }
            }.runTaskTimerAsynchronously(PickMcFFA.getInstance(), 0, 5);

            Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), task::cancel, 20 * 10);
        }

        if (event.getMessage().equalsIgnoreCase("serialize")) {
            try {
                ItemStack itemStack = ItemDeserializer.itemStackFromBase64("rO0ABXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFwdAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFibGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmplY3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAAAAR0AAI9PXQABHR5cGV0AAZkYW1hZ2V0AARtZXRhdXEAfgAGAAAABHQAHm9yZy5idWtraXQuaW52ZW50b3J5Lkl0ZW1TdGFja3QAClNLVUxMX0lURU1zcgAPamF2YS5sYW5nLlNob3J0aE03EzRg2lICAAFTAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsCAAB4cAADc3EAfgAAc3EAfgADdXEAfgAGAAAABHEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJuYWx1cQB+AAYAAAAEdAAISXRlbU1ldGF0AAVTS1VMTHQADVF1ZXN0aW9uIE1hcmt0AWRINHNJQUFBQUFBQUFBRTJPeTJxRFFCaEcveFlLVnZvWTNRcGVvdFpscWNhTVpMUXhSaDEzWHNib09LYkJhSWcrVlIreExydjhPT2ZBSndLSThIYnNKczYvaDUrNjVWU0FaMVRCdTI2YXF2SlI2cEpxV3JtMEtkVkNzdkthU21wVlZCdkZvcWFsbVNLSWEzU2x3OWpTMnlzSUkzMk0wMEJ2SWdBOENmQVM1M3lpOEV0blQ4N1NScTVTajVjek10WWRIV1VlSUhZMTBTV2VpeTlrb0g3bHUwOWpQMXYvWEgzTUU1MFR6V3V5eTJFcStsamVheUdudTFBcCs5TTlTM3ptUncwajdLUVF0bTF3dE9WK2Y5Z0VMcDR6Tyt4dzRxaDRJU3BaMEJMWVp5MXpZNDRaV2JBZHRwbnJ5RGp5bU84NkNvN09zNStRUnhCMWJaMHExdm9lL2dDRXo0aVlHQUVBQUE9PQ==");
                player.getInventory().addItem(itemStack);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (event.getMessage().equalsIgnoreCase("deserialize")) {
            if (player.getItemInHand() != null) {
                ItemStack itemStack = player.getItemInHand();
                String deserializedItem = ItemDeserializer.itemStackToBase64(itemStack);
                PickMcFFA.getDataFile().getConfig().set("Text", deserializedItem.replaceAll("\\s+", ""));
                PickMcFFA.getDataFile().saveConfig();
            }
        }
    }*/

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
            StatsCommand.refreshPlayerHologram(player);
            ScoreboardListener.updateScoreBoard(player);
            player.sendMessage(ConfigManager.getKilledMessage(killer.getName(), player.getName(), deductedCoins, killer.getHealth()));
            Utils.sendActionText(player, ConfigManager.getKilledActionbar(killer.getName(), player.getName(), deductedCoins));
        }

        if (killerUser == null)
            killer.kickPlayer(ConfigManager.getFailedProfileSave());
        else {
            if (KillStreakListener.getKillStreak().containsKey(killerUser)) {
                KillStreak killStreak = KillStreakListener.getKillStreak().get(killerUser);
                if (killStreak.getSameUserCount() == 4 || killStreak.getSameUserCount2() == 4)
                    return;
            }

            Stats stats = killerUser.getStats();
            Kill kill = new Kill(player.getUniqueId(), new Date());
            stats.getKills().add(kill);
            int gainedCoins = stats.addCoins(15, 20);
            int xpGained = stats.addXp(50, 150);
            if (KillStreakListener.getBountyList().containsKey(player.getUniqueId())) {
                gainedCoins = stats.addCoins(45, 60);
                xpGained = stats.addXp(150, 450);
                KillStreakListener.getBountyList().get(player.getUniqueId()).cancel();
                KillStreakListener.getBountyList().remove(player.getUniqueId());
                TAB.getInstance().getTeamManager().resetPrefix(TAB.getInstance().getPlayer(player.getUniqueId()));
            }
            killerUser.save();
            StatsCommand.refreshPlayerHologram(killer);
            ScoreboardListener.updateScoreBoard(killer);
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

        Location location = new Location(player.getWorld(), 1.5, 73, 75.5, -180, 0);
        player.teleport(location);
        Utils.sendActionText(player, ConfigManager.getNoKitError());
        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 2, 10);
    }
}

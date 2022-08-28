package me.zxoir.pickmcffa.commands;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import me.zxoir.pickmcffa.utils.LocationAdapter;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/12/2022
 */
public class StatsCommand implements CommandExecutor {
    @Getter
    private static final HashMap<Player, Hologram> holograms = new HashMap<>();
    @Getter
    @Setter
    private static Location statsLocation;
    Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();

    public static void spawnStatsHologram(Player player, Stats stats) {
        Utils.runTaskSync(() -> {
            if (statsLocation == null) return;

            Hologram hologram = HologramsAPI.createHologram(PickMcFFA.getInstance(), statsLocation);
            VisibilityManager visibilityManager = hologram.getVisibilityManager();
            visibilityManager.showTo(player);
            visibilityManager.setVisibleByDefault(false);

            hologram.appendTextLine(colorize("&bStats for &b&l" + player.getName()));
            hologram.appendTextLine(colorize("&aCoins: &7" + stats.getCoins()));
            hologram.appendTextLine(colorize("&aKills: &7" + stats.getKills().size()));
            hologram.appendTextLine(colorize("&aDeaths: &7" + stats.getDeaths()));
            hologram.appendTextLine(colorize("&aKillStreak: &7" + stats.getKillsStreak()));
            hologram.appendTextLine(colorize("&aBest KillStreak: &7" + stats.getMaxKillStreaks()));
            hologram.appendItemLine(new ItemStackBuilder(Material.DIAMOND_SWORD).withEnchantment(Enchantment.ARROW_DAMAGE, 4).build());

            holograms.put(player, hologram);
        });
    }

    public static void refreshPlayerHologram(Player player) {
        if (!holograms.containsKey(player)) return;

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        if (user == null)
            return;

        Hologram hologram = holograms.get(player);
        Utils.runTaskSync(() -> {
            hologram.delete();
            spawnStatsHologram(player, user.getStats());
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        if (user == null)
            return true;

        if (args.length == 1 && args[0].equalsIgnoreCase("holo") && player.hasPermission("staff.admin")) {
            statsLocation = player.getLocation();
            PickMcFFA.getDataFile().getConfig().set("holo", gson.toJson(player.getLocation(), Location.class));
            PickMcFFA.getDataFile().saveConfig();

            Collection<Hologram> holograms = HologramsAPI.getHolograms(PickMcFFA.getInstance());
            if (!holograms.isEmpty())
                holograms.forEach(Hologram::delete);

            for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                User onlineUser = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
                if (onlineUser == null)
                    continue;

                spawnStatsHologram(online, onlineUser.getStats());
            }

            return true;
        }

        if (args.length == 1) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            User targetUser = PickMcFFA.getCachedUsers().getIfPresent(offlinePlayer.getUniqueId());

            if (targetUser != null) {
                player.sendMessage(getStatsMessage(targetUser.getStats()));
                player.sendMessage("");
                return true;
            }

        }

        Stats stats = user.getStats();
        player.sendMessage(getStatsMessage(stats));
        player.sendMessage("");
        return true;
    }

    @NotNull
    private String getStatsMessage(@NotNull Stats stats) {

        return colorize("\n&7Name &8» &e" + Bukkit.getOfflinePlayer(stats.getUuid()).getName() + "\n" +
                "&7Level: &8» &e" + stats.getLevel() + "\n" +
                "&7Coins: &8» &e" + stats.getCoins() + "\n" +
                "&7Events Won: &8» &e" + stats.getEventsWon() + "\n" +
                "&7Kills: &8» &e" + stats.getKills().size() + "\n" +
                "&7Deaths: &8» &e" + stats.getDeaths() + "\n" +
                "&7KillStreak: &8» &e" + stats.getKillsStreak() + "\n" +
                "&7Best KillStreak: &8» &e" + stats.getMaxKillStreaks());
    }
}
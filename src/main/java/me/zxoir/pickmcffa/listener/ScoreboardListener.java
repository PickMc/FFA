package me.zxoir.pickmcffa.listener;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/6/2022
 */
public class ScoreboardListener implements Listener {
    Integer MAX_CHAR;

    private static void updateTeam(Scoreboard scoreboard, String team, @NotNull String text) {
        if (text.length() <= 16) {
            scoreboard.getTeam(team).setPrefix(text);
            return;
        }

        if (text.length() > 32) {
            text = text.substring(0, 32);
        }

        scoreboard.getTeam(team).setPrefix(text.substring(0, 16));
        scoreboard.getTeam(team).setSuffix(text.substring(16));
    }

    public static void updateScoreBoard(@NotNull Player player) {
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        if (user == null)
            return;

        Stats stats = user.getStats();

        Scoreboard board = player.getScoreboard();
        //board.getTeam("onlineCounter").setPrefix(ChatColor.DARK_RED + "0" + ChatColor.RED + "/" + ChatColor.DARK_RED + Bukkit.getMaxPlayers());
        updateTeam(board, "online", colorize("&7» Online: &e" + Bukkit.getOnlinePlayers().size() + " &l/&r&e " + Bukkit.getMaxPlayers()));
        updateTeam(board, "level", colorize("&7Level: &e" + stats.getLevel()));
        updateTeam(board, "coins", colorize("&7Coins: &e" + stats.getCoins()));
        updateTeam(board, "kills", colorize("&7Kills: &e" + stats.getKills().size()));
        updateTeam(board, "deaths", colorize("&7Deaths: &e" + stats.getDeaths()));
        updateTeam(board, "killstreak", colorize("&7Killstreak: &e" + stats.getKillsStreak()));
        updateTeam(board, "bestKillstreak", colorize("&7Best Killstreak: &e" + stats.getMaxKillStreaks()));
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        setScoreBoard(event.getPlayer());
        Bukkit.getOnlinePlayers().forEach(ScoreboardListener::updateScoreBoard);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(ScoreboardListener::updateScoreBoard), 1);
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Bukkit.getOnlinePlayers().forEach(ScoreboardListener::updateScoreBoard);
    }

    public void setScoreBoard(@NotNull Player player) {
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        if (user == null)
            return;

        Stats stats = user.getStats();
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective(colorize("&3&lFFA"), "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        final CachedMetaData metaData = PickMcFFA.getLuckPerms().getPlayerAdapter(Player.class).getMetaData(player);
        final String group = metaData.getPrimaryGroup();
        String rank = metaData.getPrefix() != null ? (group != null ? StringUtils.substringBefore(metaData.getPrefix(), " ") : "") : "";

        String chatColor = group != null ? StringUtils.substringAfter(metaData.getPrefix() != null ? metaData.getPrefix() : "", "| ") : "";

        MAX_CHAR = 0;

        addTeam(board, obj, " ", "space", 13, 1);
        addTeam(board, obj, colorize("&7» IGN: &8" + chatColor + player.getName()), "ign", 12, 2);
        addTeam(board, obj, colorize("&7» Rank: &8" + rank), "rank", 11, 3);
        addTeam(board, obj, colorize("&7» Online: &e" + Bukkit.getOnlinePlayers().size() + " &l/&r&e " + Bukkit.getMaxPlayers()), "online", 10, 4);
        addTeam(board, obj, "  ", "space2", 9, 5);
        addTeam(board, obj, colorize("&7Level: &e" + stats.getLevel()), "level", 8, 'e');
        addTeam(board, obj, colorize("&7Coins: &e" + stats.getCoins()), "coins", 7, 0);
        addTeam(board, obj, colorize("&7Kills: &e" + stats.getKills().size()), "kills", 6, 6);
        addTeam(board, obj, colorize("&7Deaths: &e" + stats.getDeaths()), "deaths", 5, 7);
        addTeam(board, obj, colorize("&7Killstreak: &e" + stats.getKillsStreak()), "killstreak", 4, 8);
        addTeam(board, obj, colorize("&7Best Killstreak: &e" + stats.getMaxKillStreaks()), "bestKillstreak", 3, 9);
        addTeam(board, obj, "   ", "space3", 2, 'b');
        addTeam(board, obj, colorize("&8&m---------------------"), "line", 1, 'c');
        addTeam(board, obj, "    " + StringUtils.center(colorize("&3Pick-Mc.com"), MAX_CHAR), "ip", 0, 'd');

        player.setScoreboard(board);
    }

    private void addTeam(@NotNull Scoreboard scoreboard, Objective obj, @NotNull String text, String teamName, int i, int id) {
        Team team = scoreboard.registerNewTeam(teamName);

        if (text.length() > 16)
            team.addEntry(colorize("&" + id + ChatColor.getLastColors(text.substring(0, 16))));
        else
            team.addEntry(colorize("&" + id + ChatColor.getLastColors(text)));

        // The text doesn't have more than 16 characters, so we are fine
        if (text.length() <= 16) {
            team.setPrefix(text);
            obj.getScore(colorize("&" + id + ChatColor.getLastColors(text))).setScore(i);
            return;
        }

        // If the text actually goes above 32, cut it to 32 to prevent kicks and errors
        if (text.length() > 32) {
            text = text.substring(0, 32);
        }

        // Set the prefix to the first 16 characters
        team.setPrefix(text.substring(0, 16));

        // Now use the last 16 characters and put them into the suffix
        team.setSuffix(text.substring(16));

        obj.getScore(colorize("&" + id + ChatColor.getLastColors(text.substring(0, 16)))).setScore(i);

        if (MAX_CHAR == null || ChatColor.stripColor(text).length() > MAX_CHAR)
            MAX_CHAR = ChatColor.stripColor(text).length();
    }

    private void addTeam(@NotNull Scoreboard scoreboard, Objective obj, String text, String teamName, int i, char id) {
        Team team = scoreboard.registerNewTeam(teamName);

        team.addEntry(colorize("&" + id + ChatColor.getLastColors(text)));

        // The text doesn't have more than 16 characters, so we are fine
        if (text.length() <= 16) {
            team.setPrefix(text);
            obj.getScore(colorize("&" + id + ChatColor.getLastColors(text))).setScore(i);
            return;
        }

        // If the text actually goes above 32, cut it to 32 to prevent kicks and errors
        if (text.length() > 32) {
            text = text.substring(0, 32);
        }

        // Set the prefix to the first 16 characters
        team.setPrefix(text.substring(0, 16));

        // Now use the last 16 characters and put them into the suffix
        team.setSuffix(text.substring(16));

        obj.getScore(colorize("&" + id + ChatColor.getLastColors(text))).setScore(i);

        if (MAX_CHAR == null || text.length() > MAX_CHAR)
            MAX_CHAR = text.length();
    }
}

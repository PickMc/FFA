package me.zxoir.pickmcffa.commands;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/12/2022
 */
public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender)
            return true;

        Player player = (Player) sender;
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        if (user == null)
            return true;

        Stats stats = user.getStats();
        player.sendMessage("XP: " + stats.getXp());
        player.sendMessage("Level: " + stats.getLevel());
        player.sendMessage("Coins: " + stats.getCoins());
        player.sendMessage("Max Kill Streaks: " + stats.getMaxKillStreaks());
        player.sendMessage("Kill Streak: " + stats.getKillsStreak());
        player.sendMessage("Kills: " + stats.getKills());
        player.sendMessage("Deaths: " + stats.getDeaths());
        return true;
    }
}
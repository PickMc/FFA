package me.zxoir.pickmcffa.commands.topstats;

import me.zxoir.pickmcffa.managers.StatsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2022
 */
public class TopLevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append(colorize("\n&e&lTop 10 players with the most level:\n"));
        HashMap<String, Integer> sortedMap = StatsManager.getTopLevel();
        String[] top10 = (String[]) sortedMap.keySet().toArray((Object[]) new String[0]);
        for (int i = 0; i < 10 &&
                i < top10.length; i++)
            builder.append(colorize("&8#" + (i + 1) + " &c" + top10[i] + " &7- &b" + sortedMap.get(top10[i]) + "&a \n&a"));
        builder.append(colorize("\n&a"));
        sender.sendMessage(builder.toString());

        return true;
    }

}

package me.zxoir.pickmcffa.commands;

import lombok.SneakyThrows;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Booster;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.UserManager;
import me.zxoir.pickmcffa.utils.TimeManager;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static me.zxoir.pickmcffa.utils.Utils.colorize;
import static me.zxoir.pickmcffa.utils.Utils.isInteger;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/10/2022
 */
public class MainCommand implements CommandExecutor {

    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage(ConfigManager.getInvalidPermission());
            return true;
        }

        if (args.length < 1)
            return true;

        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("personalboost")) {

                if (args[1].equalsIgnoreCase("xp")) {
                    if (!isInteger(args[2])) {
                        sender.sendMessage("You must enter a numerical value!");
                        return true;
                    }

                    int value = Integer.parseInt(args[2]);
                    if (value <= 1 || value > 10) {
                        sender.sendMessage("Value must be more than 1!");
                        return true;
                    }

                    if (TimeManager.toMilliSecond(args[3]) == null) {
                        sender.sendMessage("Incorrect Time Format!");
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[4]);
                    if (player == null || !player.hasPlayedBefore()) {
                        sender.sendMessage("That player has never played before.");
                        return true;
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission settemp boost.xp." + value + " true " + args[3]);
                    sender.sendMessage("XP Boost given");
                }

                if (args[1].equalsIgnoreCase("coin")) {
                    if (!isInteger(args[2])) {
                        sender.sendMessage("You must enter a numerical value!");
                        return true;
                    }

                    int value = Integer.parseInt(args[2]);
                    if (value <= 1 || value > 10) {
                        sender.sendMessage("Value must be more than 1!");
                        return true;
                    }

                    if (TimeManager.toMilliSecond(args[3]) == null) {
                        sender.sendMessage("Incorrect Time Format!");
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[4]);
                    if (player == null || !player.hasPlayedBefore()) {
                        sender.sendMessage("That player has never played before.");
                        return true;
                    }

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " permission settemp boost.coin." + value + " true " + args[3]);
                    sender.sendMessage("Coin Boost given");
                }

            }
        }

        if (args.length == 4) {

            if (args[0].equalsIgnoreCase("globalboost")) {

                if (args[1].equalsIgnoreCase("xp")) {
                    if (!isInteger(args[2])) {
                        sender.sendMessage("You must enter a numerical value!");
                        return true;
                    }

                    int value = Integer.parseInt(args[2]);
                    if (value <= 1 || value > 10) {
                        sender.sendMessage("Value must be more than 1!");
                        return true;
                    }

                    Long duration = TimeManager.toMilliSecond(args[3]);
                    if (duration == null) {
                        sender.sendMessage("Incorrect Time Format!");
                        return true;
                    }

                    duration = duration / 1000;

                    Booster.setGlobalXP(value, duration * 20);

                }

                if (args[1].equalsIgnoreCase("coin")) {
                    if (!isInteger(args[2])) {
                        sender.sendMessage("You must enter a numerical value!");
                        return true;
                    }

                    int value = Integer.parseInt(args[2]);
                    if (value <= 1 || value > 10) {
                        sender.sendMessage("Value must be more than 1!");
                        return true;
                    }

                    Long duration = TimeManager.toMilliSecond(args[3]);
                    if (duration == null) {
                        sender.sendMessage("Incorrect Time Format!");
                        return true;
                    }

                    duration = duration / 1000;

                    Booster.setGlobalCoin(value, duration * 20);

                }

            }

        }

        if (args[0].equalsIgnoreCase("reload")) {
            ConfigManager.reloadConfig();
            sender.sendMessage(colorize("&a&lConfig's reloaded successfully"));
            return true;
        }

        if (args[0].equalsIgnoreCase("boostlist")) {

            if (PickMcFFA.getCachedUsers().size() <= 0) {
                sender.sendMessage("None.");
                return true;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("List of boosters:");
            for (User user : PickMcFFA.getCachedUsers().asMap().values()) {
                if (user == null)
                    continue;

                Integer xpBoost = UserManager.getXpBoost(user.getOfflinePlayer());
                Integer coinBoost = UserManager.getCoinBoost(user.getOfflinePlayer());

                if (xpBoost == null && coinBoost == null)
                    continue;

                if (xpBoost != null)
                    builder.append("\n").append(user.getOfflinePlayer().getName()).append(" - ").append("XP ").append(xpBoost).append("x");
                if (coinBoost != null)
                    builder.append("\n").append(user.getOfflinePlayer().getName()).append(" - ").append("Coin ").append(coinBoost).append("x");
            }

            if (builder.length() <= 17)
                sender.sendMessage("None.");
            else
                sender.sendMessage(builder.toString());
        }

        return true;
    }
}
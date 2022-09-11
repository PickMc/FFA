package me.zxoir.pickmcffa.commands;

import lombok.SneakyThrows;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Booster;
import me.zxoir.pickmcffa.customclasses.Kill;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.UserManager;
import me.zxoir.pickmcffa.utils.TimeManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static java.util.concurrent.CompletableFuture.runAsync;
import static me.zxoir.pickmcffa.utils.Utils.colorize;
import static me.zxoir.pickmcffa.utils.Utils.isDouble;

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
                    if (!isDouble(args[2])) {
                        sender.sendMessage("You must enter a numerical value!");
                        return true;
                    }

                    double value = Double.parseDouble(args[2]);
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
                    if (!isDouble(args[2])) {
                        sender.sendMessage("You must enter a numerical value!");
                        return true;
                    }

                    double value = Double.parseDouble(args[2]);
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

        // ffa addkills name amount
        if (args.length == 3) {

            if (args[0].equalsIgnoreCase("addkills") && Utils.isInteger(args[2])) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                int amount = Integer.parseInt(args[2]);

                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage("Invalid user");
                    return true;
                }

                User user = PickMcFFA.getCachedUsers().getIfPresent(target.getUniqueId());

                if (user == null) {
                    sender.sendMessage("Invalid user");
                    return true;
                }

                long start = System.currentTimeMillis();
                runAsync(() -> {
                    Kill kill = new Kill(null, null);
                    for (int i = 0; i < amount; i++) {
                        user.getStats().getKills().add(kill);
                    }
                    user.save();
                }).get();
                sender.sendMessage("Successfully added " + amount + " kills to " + target.getName() + "'s Stats Asynchronously. " + (System.currentTimeMillis() - start) + "ms");

                return true;
            }

        }

        if (args.length == 4) {

            if (args[0].equalsIgnoreCase("globalboost")) {

                if (args[1].equalsIgnoreCase("xp")) {
                    if (!isDouble(args[2])) {
                        sender.sendMessage("You must enter a numerical value!");
                        return true;
                    }

                    double value = Double.parseDouble(args[2]);
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
                    sender.sendMessage("Globalboost activated");

                    return true;
                }

                if (args[1].equalsIgnoreCase("coin")) {
                    if (!isDouble(args[2])) {
                        sender.sendMessage("You must enter a numerical value!");
                        return true;
                    }

                    double value = Double.parseDouble(args[2]);
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
                    sender.sendMessage("Globalboost activated");

                    return true;
                }

            }

        }

        if (args[0].equalsIgnoreCase("reload")) {
            ConfigManager.reloadConfig();
            sender.sendMessage(colorize("&a&lConfig's reloaded successfully"));
            return true;
        }

        if (args[0].equalsIgnoreCase("globalboost")) {
            if (Booster.getGlobalXP() != null && Booster.getGlobalCoin() != null)
                sender.sendMessage("XP: " + Booster.getGlobalXP() + " Coin: " + Booster.getGlobalCoin());
            else if (Booster.getGlobalXP() != null)
                sender.sendMessage("XP: " + Booster.getGlobalXP());
            else if (Booster.getGlobalCoin() != null)
                sender.sendMessage("Coin: " + Booster.getGlobalCoin());
            else
                sender.sendMessage("No global boost.");

            return true;
        }

        if (args[0].equalsIgnoreCase("boostlist")) {

            if (PickMcFFA.getCachedUsers().size() <= 0) {
                sender.sendMessage("None.");
                return true;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("List of online boosters:");
            for (Player player : Bukkit.getOnlinePlayers()) {

                Double xpBoost = UserManager.getXpBoost(player);
                Double coinBoost = UserManager.getCoinBoost(player);

                if (xpBoost == null && coinBoost == null)
                    continue;

                if (xpBoost != null)
                    builder.append("\n").append(player.getName()).append(" - ").append("XP ").append(xpBoost).append("x");
                if (coinBoost != null)
                    builder.append("\n").append(player.getName()).append(" - ").append("Coin ").append(coinBoost).append("x");
            }

            if (builder.length() <= 24)
                sender.sendMessage("None.");
            else
                sender.sendMessage(builder.toString());
        }

        return true;
    }
}
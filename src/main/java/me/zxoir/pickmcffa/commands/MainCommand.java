package me.zxoir.pickmcffa.commands;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.PerkManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/10/2022
 */
public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage(ConfigManager.getInvalidPermission());
            return true;
        }

        if (args.length < 1)
            return true;

        if (args[0].equalsIgnoreCase("reload")) {
            ConfigManager.reloadConfig();
            sender.sendMessage(colorize("&a&lConfig's reloaded successfully"));
            return true;
        }

        if (args[0].equalsIgnoreCase("explosion") && sender instanceof Player) {
            User playerUser = PickMcFFA.getCachedUsers().getIfPresent(((Player) sender).getUniqueId());
            if (playerUser != null)
                playerUser.setSelectedPerk(PerkManager.getExplosionPerk());

            sender.sendMessage("Activated");
            return true;
        }

        if (args[0].equalsIgnoreCase("absorption") && sender instanceof Player) {
            User playerUser = PickMcFFA.getCachedUsers().getIfPresent(((Player) sender).getUniqueId());

            if (playerUser != null)
                playerUser.setSelectedPerk(PerkManager.getAbsorptionPerk());

            sender.sendMessage("Activated");
            return true;
        }

        if (args[0].equalsIgnoreCase("speed") && sender instanceof Player) {
            User playerUser = PickMcFFA.getCachedUsers().getIfPresent(((Player) sender).getUniqueId());

            if (playerUser != null)
                playerUser.setSelectedPerk(PerkManager.getSpeedPerk());

            sender.sendMessage("Activated");
            return true;
        }

        return true;
    }
}
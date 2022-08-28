package me.zxoir.pickmcffa.commands;

import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.EventsManager;
import me.zxoir.pickmcffa.menus.KitMenu;
import me.zxoir.pickmcffa.utils.Utils;
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
 * @since 3/24/2022
 */
public class KitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (EventsManager.isEventActive())
            return true;

        // If the player is in a pvp area, return
        if (Utils.isInPvpArea(player)) {
            player.sendMessage(ConfigManager.getKitOutsideSpawnError());
            return true;
        }

        player.openInventory(Utils.duplicateInventory(KitMenu.getInventory()));

        return true;
    }
}
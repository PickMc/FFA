package me.zxoir.pickmcffa.commands;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.menus.PerkMenu;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/12/2022
 */
public class PerkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // If the player is in a pvp area, return
        if (Utils.isInPvpArea(player)) {
            player.sendMessage(ConfigManager.getPerkOutsideSpawnError());
            return true;
        }

        player.openInventory(Utils.duplicateInventory(PerkMenu.getInventory()));

        return true;
    }
}

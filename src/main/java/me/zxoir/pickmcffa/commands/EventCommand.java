package me.zxoir.pickmcffa.commands;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.EventsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/19/2022
 */
public class EventCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!sender.hasPermission("staff.admin")) {
            sender.sendMessage(ConfigManager.getInvalidPermission());
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("start")) {
                EventsManager.EventType eventType;
                try {
                    eventType = EventsManager.EventType.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage("No event type with that name.");
                    return true;
                }

                EventsManager.startEvent(eventType);
                player.sendMessage("Started event");
            }

        }

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("test")) {
                player.sendMessage(player.getItemInHand().getType().name());
            }

            if (args[0].equalsIgnoreCase("start")) {
                if (EventsManager.isEventActive()) {
                    player.sendMessage("There's an event active.");
                    return true;
                }

                EventsManager.startEvent(null);
                player.sendMessage("Started event");
            }

            if (args[0].equalsIgnoreCase("stop")) {

                if (!EventsManager.isEventActive()) {
                    player.sendMessage("There's no event active.");
                    return true;
                }

                EventsManager.endEvent(null);
                player.sendMessage("Ended event");
            }

            if (args[0].equalsIgnoreCase("setBarrier")) {
                WorldEditPlugin worldEdit = PickMcFFA.getWorldEditPlugin();
                Selection selection = worldEdit.getSelection(player);

                if (selection == null) {
                    player.sendMessage("Select a region!");
                    return true;
                }

                EventsManager.setBarrierWall(selection);
                player.sendMessage("Selection Saved!");

                if (selection.getWorld() == null) {
                    player.sendMessage("world name is null");
                    return true;
                }

                PickMcFFA.getDataFile().getConfig().set("BarrierWall.world", selection.getWorld().getName());
                PickMcFFA.getDataFile().getConfig().set("BarrierWall.minX", selection.getMinimumPoint().getX());
                PickMcFFA.getDataFile().getConfig().set("BarrierWall.minY", selection.getMinimumPoint().getY());
                PickMcFFA.getDataFile().getConfig().set("BarrierWall.minZ", selection.getMinimumPoint().getZ());
                PickMcFFA.getDataFile().getConfig().set("BarrierWall.maxX", selection.getMaximumPoint().getX());
                PickMcFFA.getDataFile().getConfig().set("BarrierWall.maxY", selection.getMaximumPoint().getY());
                PickMcFFA.getDataFile().getConfig().set("BarrierWall.maxZ", selection.getMaximumPoint().getZ());
                PickMcFFA.getDataFile().saveConfig();
            }

            if (args[0].equalsIgnoreCase("setTeleportPoint")) {
                EventsManager.setTeleportPoint(player.getLocation());
                player.sendMessage("Teleport point saved!");

                PickMcFFA.getDataFile().getConfig().set("Teleport.world", player.getWorld().getName());
                PickMcFFA.getDataFile().getConfig().set("Teleport.x", player.getLocation().getX());
                PickMcFFA.getDataFile().getConfig().set("Teleport.y", player.getLocation().getY());
                PickMcFFA.getDataFile().getConfig().set("Teleport.z", player.getLocation().getZ());
                PickMcFFA.getDataFile().getConfig().set("Teleport.pitch", player.getLocation().getPitch());
                PickMcFFA.getDataFile().getConfig().set("Teleport.yaw", player.getLocation().getYaw());
                PickMcFFA.getDataFile().saveConfig();
            }

        }

        return true;
    }

}
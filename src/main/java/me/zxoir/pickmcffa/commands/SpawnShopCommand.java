package me.zxoir.pickmcffa.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.EntityNPC;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

public class SpawnShopCommand implements CommandExecutor {
    @Getter
    @Setter
    private static Villager shopNPC;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) return true;
        Player player = (Player) sender;

        if (!player.isOp()) { // TODO: Change to permission check
            player.sendMessage(ConfigManager.getInvalidPermission());
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
            if (shopNPC == null) {
                player.sendMessage(colorize("&c&lERROR > &8You must spawn a NPC first."));
                return true;
            }

            shopNPC.remove();
            shopNPC = null;
            PickMcFFA.getDataFile().getConfig().set("shop", null);
            PickMcFFA.getDataFile().saveConfig();
            player.sendMessage(colorize("&aNPC removed."));
            return true;
        }

        if (shopNPC != null) {
            player.sendMessage(colorize("&cThere's an existing NPC. To remove use &e/spawnshop remove"));
            return true;
        }

        shopNPC = EntityNPC.spawn(player.getLocation(), ConfigManager.getShopVillagerName());
        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
        PickMcFFA.getDataFile().getConfig().set("shop", gson.toJson(player.getLocation(), Location.class));
        PickMcFFA.getDataFile().saveConfig();
        player.sendMessage(colorize("&aNPC spawned."));
        return true;
    }
}
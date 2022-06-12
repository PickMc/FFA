package me.zxoir.pickmcffa.utils;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/23/2022
 */
public class Utils {
    @Getter
    private static final Random RANDOM = new Random();

    @NotNull
    @Contract("_ -> new")
    public static String colorize(String arg) {
        return ChatColor.translateAlternateColorCodes('&', arg);
    }

    public static void sendCombatActionText(Player player, String message) {
        runAsync(() -> {
            PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(colorize(message)), (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        });
    }

    public static Inventory duplicateInventory(@NotNull Inventory inventory) {
        Inventory duplicate = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), inventory.getName());
        duplicate.setContents(inventory.getContents());
        return duplicate;
    }

    public static void sendActionText(Player player, String message) {
        runAsync(() -> {
            User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

            if (user == null)
                return;

            user.setActionbar(true);
            Bukkit.getScheduler().runTaskLaterAsynchronously(PickMcFFA.getInstance(), () -> user.setActionbar(false), 60);

            PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(colorize(message)), (byte) 2);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        });
    }

    public static void runTaskSync(Task task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTask(PickMcFFA.getInstance());
    }

    public interface Task {
        void execute();
    }

}

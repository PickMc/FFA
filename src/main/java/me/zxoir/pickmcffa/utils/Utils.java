package me.zxoir.pickmcffa.utils;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.Random;

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

    public static void sendActionText(Player player, String message){
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(colorize(message)), (byte)2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public interface Task {
        void execute();
    }

    public static void runTaskSync(Task task) {
        new BukkitRunnable() {
            @Override
            public void run() {
                task.execute();
            }
        }.runTask(PickMcFFA.getInstance());
    }

}

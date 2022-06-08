package me.zxoir.pickmcffa.utils;

import me.zxoir.pickmcffa.PickMcFFA;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/24/2022
 */
public class Reflection {
    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception ex) {
            PickMcFFA.getFfaLogger().fatal(ex);
        }
    }

    public Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException ex) {
            PickMcFFA.getFfaLogger().fatal(ex);
        }
        return null;
    }
}

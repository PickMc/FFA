package me.zxoir.pickmcffa.utils;

import me.zxoir.pickmcffa.PickMcFFA;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/24/2022
 */
public class Title extends Reflection {

    public void send(Player player, int fadeInTime, int showTime, int fadeOutTime, String title, String subtitle) {
        try {
            Object chatResetTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + colorize(title) + "\"}");
            Constructor<?> resetConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object resetPacket = resetConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("RESET").get(null), chatResetTitle,
                    fadeInTime, showTime, fadeOutTime);

            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + colorize(title) + "\"}");
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    fadeInTime, showTime, fadeOutTime);

            Object chatsTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + colorize(subtitle) + "\"}");
            Constructor<?> stitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object spacket = stitleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    fadeInTime, showTime, fadeOutTime);

            sendPacket(player, resetPacket);
            sendPacket(player, packet);
            sendPacket(player, spacket);
        } catch (Exception ex) {
            PickMcFFA.getFfaLogger().fatal(ex);
        }
    }
}

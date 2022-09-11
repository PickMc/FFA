package me.zxoir.pickmcffa.utils;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.DelayedPotionEffect;
import me.zxoir.pickmcffa.customclasses.User;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/23/2022
 */
public class Utils {
    private static final ConcurrentHashMap<UUID, ConcurrentHashMap<PotionEffectType, DelayedPotionEffect>> delayedPotionEffect = new ConcurrentHashMap<>();

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

    @NotNull
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

    public static boolean isInteger(String string) {

        try {
            Integer.parseInt(string);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    public static boolean isDouble(String string) {

        try {
            Double.parseDouble(string);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    public static boolean isInPvpArea(Player player) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(player.getLocation());

        return set.testState(localPlayer, DefaultFlag.PVP);
    }

    /*private static boolean addPotion(@NotNull Player player, @NotNull PotionEffect potionEffect) {
        // If the player doesn't have the same potion effect then apply it
        if (!player.hasPotionEffect(potionEffect.getType()))
            player.addPotionEffect(potionEffect);

        PotionEffect activeEffect = null;

        for (PotionEffect effect : player.getActivePotionEffects()) {

            if (!effect.getType().equals(potionEffect.getType()))
                continue;

            activeEffect = effect;
            break;
        }

        if (activeEffect == null)
            return false;

        // If the player has the same potion effect
        if (activeEffect.getAmplifier() == potionEffect.getAmplifier()) {

            // The active PotionEffect duration is longer
            if (activeEffect.getDuration() >= potionEffect.getDuration())
                return false;

            player.removePotionEffect(potionEffect.getType());
            player.addPotionEffect(potionEffect, true);
            return true;
        }

        if (activeEffect.getAmplifier() > potionEffect.getAmplifier()) {

            if (activeEffect.getDuration() >= potionEffect.getDuration())
                return false;

            PotionEffect toBeReturned = new PotionEffect(potionEffect.getType(), potionEffect.getDuration() - activeEffect.getDuration(), potionEffect.getAmplifier());
            Bukkit.getScheduler().runTaskLaterAsynchronously(PickMcFFA.getInstance(), () -> player.addPotionEffect(toBeReturned, true), activeEffect.getDuration());
            return true;
        }

        // Potion Effect stronger than active effect

        if (potionEffect.getDuration() >= activeEffect.getDuration()) {
            player.addPotionEffect(potionEffect, true);
            return true;
        }

        PotionEffect toBeReturned = new PotionEffect(activeEffect.getType(), activeEffect.getDuration() - potionEffect.getDuration(), activeEffect.getAmplifier());
        Bukkit.getScheduler().runTaskLaterAsynchronously(PickMcFFA.getInstance(), () -> player.addPotionEffect(toBeReturned, true), potionEffect.getDuration());
        return true;
    }

    /**
     * @param player       Player
     * @param potionEffect Potion Effect
     * @return true: Potion activated/delayed | false: Potion returned/cancelled
     *//*
    public static boolean addPotionEffect(@NotNull Player player, @NotNull PotionEffect potionEffect) {
        if (delayedPotionEffect.containsKey(player.getUniqueId()) && delayedPotionEffect.get(player.getUniqueId()).containsKey(potionEffect.getType())) {
            ConcurrentHashMap<PotionEffectType, DelayedPotionEffect> delayedPotions = delayedPotionEffect.get(player.getUniqueId());
            PotionEffect delayedPotionEffect = delayedPotions.get(potionEffect.getType()).getPotionEffect();
            BukkitTask delayedTask = delayedPotions.get(potionEffect.getType()).getBukkitTask();
            PotionEffect activeEffect = null;

            for (PotionEffect effect : player.getActivePotionEffects()) {

                if (!effect.getType().equals(potionEffect.getType()))
                    continue;

                activeEffect = effect;
                break;
            }

            if (potionEffect.getAmplifier() == delayedPotionEffect.getAmplifier()) {

                if (potionEffect.getDuration() <= delayedPotionEffect.getDuration())
                    return false;

                delayedTask.cancel();
                delayedPotions.remove(potionEffect.getType());
                return addPotion(player, potionEffect);
            }

            if (potionEffect.getAmplifier() > delayedPotionEffect.getAmplifier()) {

                if (potionEffect.getDuration() >= delayedPotionEffect.getDuration()) {
                    delayedTask.cancel();
                    delayedPotions.remove(potionEffect.getType());
                    return addPotion(player, potionEffect);
                }

                delayedTask.cancel();
                delayedPotions.remove(potionEffect.getType());

                if (activeEffect != null && activeEffect.getAmplifier() >= delayedPotionEffect.getAmplifier())
                    return addPotion(player, potionEffect);

                player.removePotionEffect(potionEffect.getType());
                player.addPotionEffect(delayedPotionEffect);
                return addPotion(player, potionEffect);
            }

            return false;
        }

        return addPotion(player, potionEffect);
    }*/

    public interface Task {
        void execute();
    }

}

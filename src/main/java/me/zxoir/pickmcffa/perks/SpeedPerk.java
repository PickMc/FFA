package me.zxoir.pickmcffa.perks;

import me.zxoir.pickmcffa.customclasses.Perk;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import me.zxoir.pickmcffa.utils.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/11/2022
 */
public class SpeedPerk extends Perk {

    public SpeedPerk() {
        super("Speed", "Speed Perk", 100, 5, new ItemStackBuilder(new ItemStack(Material.POTION, 1, (short) 8194)).withName("&C&lSpeed Perk").resetFlags().build(), "perk.speed");
    }

    @Override
    public void killAction(User killed, @NotNull User killer) {
        float chance = getRandom().nextFloat();
        float speedChance = (float) ConfigManager.getSpeedChance() / 100;

        Player player = killer.getPlayer();

        if (chance >= speedChance)
            return;

        runAsync(() -> {
            PacketPlayOutWorldParticles packet =
                    new PacketPlayOutWorldParticles(EnumParticle.VILLAGER_ANGRY, true, (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(),
                            1.5F, 2.5F, 1.5F, 0, 120);

            for (Player online : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
            }
        });

        player.getWorld().playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, ConfigManager.getSpeedDuration() * 20, 0));

        if (!ConfigManager.getSpeedActivated().isEmpty())
            player.sendMessage(ConfigManager.getSpeedActivated());

        if (!ConfigManager.getSpeedActivatedActionbar().isEmpty())
            Utils.sendActionText(player, ConfigManager.getSpeedActivatedActionbar());
    }

    @Override
    public void hitAction(@NotNull User killed, @NotNull User killer) {

    }
}

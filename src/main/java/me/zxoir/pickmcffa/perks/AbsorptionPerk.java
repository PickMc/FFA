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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import static java.util.concurrent.CompletableFuture.runAsync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/10/2022
 */
public class AbsorptionPerk extends Perk {

    public AbsorptionPerk() {
        super("Absorption", ConfigManager.getAbsorptionChance() + "% chance to gain absorption after a kill", ConfigManager.getAbsorptionPrice(), ConfigManager.getAbsorptionLevel(), ConfigManager.getAbsorptionExpire(), new ItemStackBuilder(Material.GOLDEN_APPLE).withName("&c&lAbsorption Perk").resetFlags().build(), "perk.absorption");
    }

    @Override
    public void killAction(@NotNull User killed, @NotNull User killer) {
        float chance = getRandom().nextFloat();
        float absorptionChance = (float) ConfigManager.getAbsorptionChance() / 100;

        Player player = killer.getPlayer();

        if (chance >= absorptionChance)
            return;

        runAsync(() -> {
            PacketPlayOutWorldParticles packet =
                    new PacketPlayOutWorldParticles(EnumParticle.HEART, true, (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(),
                            1, 2, 1, 5, 60);

            for (Player online : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
            }
        });

        player.getWorld().playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, ConfigManager.getAbsorptionDuration() * 20, 0), true);

        if (!ConfigManager.getAbsorptionActivated().isEmpty())
            player.sendMessage(ConfigManager.getAbsorptionActivated());

        if (!ConfigManager.getAbsorptionActivatedActionbar().isEmpty())
            Utils.sendActionText(player, ConfigManager.getAbsorptionActivatedActionbar());
    }

    @Override
    public void hitAction(@NotNull User killed, @NotNull User killer) {

    }
}

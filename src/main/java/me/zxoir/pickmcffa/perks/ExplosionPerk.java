package me.zxoir.pickmcffa.perks;

import me.zxoir.pickmcffa.customclasses.Perk;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/9/2022
 */
public class ExplosionPerk extends Perk {

    public ExplosionPerk() {
        super("Explosion", ConfigManager.getExplosionChance() + "% chance for an explosion to spawn after a kill", ConfigManager.getExplosionPrice(), ConfigManager.getExplosionLevel(), ConfigManager.getExplosionExpire(), new ItemStackBuilder(Material.TNT).withName("&c&lExplosion Perk").resetFlags().build(), "perk.explosion");
    }

    @Override
    public void killAction(@NotNull User killed, @NotNull User killer) {
        float chance = getRandom().nextFloat();
        float explosionChance = (float) ConfigManager.getExplosionChance() / 100;

        Player player = killer.getPlayer();
        Player killedPlayer = killed.getPlayer();

        if (player == null || killedPlayer == null)
            return;

        if (killedPlayer.getLastDamage() <= 0)
            return;

        if (chance >= explosionChance)
            return;

        player.getWorld().playEffect(player.getLocation(), Effect.EXPLOSION_HUGE, 10);
        player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 1, 1);

        if (!ConfigManager.getExplosionActivated().isEmpty())
            player.sendMessage(ConfigManager.getExplosionActivated());

        if (!ConfigManager.getExplosionActivatedActionbar().isEmpty())
            Utils.sendActionText(player, ConfigManager.getExplosionActivatedActionbar());

        List<Entity> entities = killedPlayer.getNearbyEntities(ConfigManager.getExplosionRadius(), ConfigManager.getExplosionRadius(), ConfigManager.getExplosionRadius());
        if (entities == null || entities.isEmpty())
            return;

        for (Entity entity : entities) {

            if (entity == null || !entity.getType().equals(EntityType.PLAYER))
                continue;

            Player nearbyPlayer = (Player) entity;

            if (nearbyPlayer.equals(player) || nearbyPlayer.getHealth() <= 0 || nearbyPlayer.getUniqueId().equals(killed.getUuid()))
                continue;

            nearbyPlayer.damage(0, player);
            nearbyPlayer.setHealth(Math.max(0, nearbyPlayer.getHealth() - 5));

            if (!ConfigManager.getExplosionDamage(player.getName()).isEmpty())
                nearbyPlayer.sendMessage(ConfigManager.getExplosionDamage(player.getName()));

            if (!ConfigManager.getExplosionDamageActionbar(player.getName()).isEmpty())
                Utils.sendActionText(nearbyPlayer, ConfigManager.getExplosionActivatedActionbar());
        }
    }

    @Override
    public void hitAction(@NotNull User hit, @NotNull User hitter) {
    }
}

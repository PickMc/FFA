package me.zxoir.pickmcffa.customclasses;

import lombok.Getter;
import me.zxoir.pickmcffa.database.UsersDBManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/19/2022
 */
@Getter
public class User {
    @NotNull
    UUID uuid;
    @NotNull
    Stats stats;
    @Nullable
    Kit selectedKit;

    public User(@NotNull String uuid) {
        this.uuid = UUID.fromString(uuid);
        this.stats = new Stats(this.uuid);
    }

    public User(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.stats = new Stats(uuid);
    }

    public User(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        this.stats = new Stats(uuid);
    }

    public User(@NotNull OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.stats = new Stats(uuid);
    }

    public User(@NotNull String uuid, @NotNull Stats stats) {
        this.uuid = UUID.fromString(uuid);
        this.stats = stats;
    }

    public User(@NotNull UUID uuid, @NotNull Stats stats) {
        this.uuid = uuid;
        this.stats = stats;
    }

    public User(@NotNull Player player, @NotNull Stats stats) {
        this.uuid = player.getUniqueId();
        this.stats = stats;
    }

    public User(@NotNull OfflinePlayer player, @NotNull Stats stats) {
        this.uuid = player.getUniqueId();
        this.stats = stats;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public void setSelectedKit(@Nullable Kit selectedKit) {
        this.selectedKit = selectedKit;
        Player player = getPlayer();

        if (player == null)
            return;

        if (selectedKit == null) {
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.getInventory().clear();
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            return;
        }

        player.getInventory().setContents(selectedKit.getItems());
        player.getInventory().setArmorContents(selectedKit.getArmour());

        if (selectedKit.getPermanentPotions() != null && !selectedKit.getPermanentPotions().isEmpty()) {

            for (Effect effect : selectedKit.getPermanentPotions()) {
                PotionEffect potionEffect = new PotionEffect(effect.getPotionEffectType(), 1000000000, effect.getAmplifier());
                player.addPotionEffect(potionEffect, true);
            }

        }
    }

    public void save() {
        UsersDBManager.updateDB(this);
    }
}

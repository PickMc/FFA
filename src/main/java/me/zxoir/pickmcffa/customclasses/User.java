package me.zxoir.pickmcffa.customclasses;

import lombok.Getter;
import me.zxoir.pickmcffa.database.UsersDBManager;
import me.zxoir.pickmcffa.utils.ItemDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
    @Nullable
    Perk selectedPerk;
    boolean actionbar = false;
    @NotNull
    ConcurrentHashMap<Kit, ItemStack[]> savedInventories = new ConcurrentHashMap<>();

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

    public void setSelectedKit(@NotNull Kit selectedKit, @NotNull ItemStack[] itemStacks) {
        this.selectedKit = selectedKit;
        Player player = getPlayer();

        if (player == null)
            return;

        player.getInventory().setContents(itemStacks);
        player.getInventory().setArmorContents(selectedKit.getArmour());

        if (selectedKit.getPermanentPotions() != null && !selectedKit.getPermanentPotions().isEmpty()) {

            for (Effect effect : selectedKit.getPermanentPotions()) {
                PotionEffect potionEffect = new PotionEffect(effect.getPotionEffectType(), 1000000000, effect.getAmplifier());
                player.addPotionEffect(potionEffect, true);
            }

        }
    }

    public void setSelectedPerk(@Nullable Perk selectedPerk) {
        this.selectedPerk = selectedPerk;
    }

    public void setActionbar(boolean actionbar) {
        this.actionbar = actionbar;
    }

    public void setSavedInventories(@NotNull ConcurrentHashMap<Kit, ItemStack[]> savedInventories) {
        this.savedInventories = savedInventories;
    }

    public ConcurrentHashMap<String, String> getDeserializedSavedInventories() {
        ConcurrentHashMap<String, String> deserializedSavedInventories = new ConcurrentHashMap<>();

        for (Kit kit : savedInventories.keySet()) {
            deserializedSavedInventories.put(kit.getName(), ItemDeserializer.itemStackArrayToBase64(savedInventories.get(kit)));
        }

        return deserializedSavedInventories;
    }

    public void save() {
        UsersDBManager.updateDB(this);
    }
}

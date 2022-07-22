package me.zxoir.pickmcffa.customclasses;

import lombok.Getter;
import lombok.Synchronized;
import me.zxoir.pickmcffa.database.UsersDBManager;
import me.zxoir.pickmcffa.utils.ItemDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.zxoir.pickmcffa.utils.Utils.runTaskSync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/19/2022
 */
@Getter(onMethod_ = {@Synchronized})
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
    String firstJoinDate;

    public User(@NotNull String uuid) {
        this.uuid = UUID.fromString(uuid);
        this.stats = new Stats(this.uuid);
        firstJoinDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").format(new Date());
    }

    public User(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.stats = new Stats(uuid);
        firstJoinDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").format(new Date());
    }

    public User(@NotNull Player player) {
        this.uuid = player.getUniqueId();
        this.stats = new Stats(uuid);
        firstJoinDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").format(new Date());
    }

    public User(@NotNull OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.stats = new Stats(uuid);
        firstJoinDate = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa").format(new Date());
    }

    public User(@NotNull String uuid, @NotNull Stats stats, String firstJoinDate) {
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

    public synchronized void setSelectedKit(@Nullable Kit selectedKit) {
        this.selectedKit = selectedKit;
        Player player = getPlayer();

        if (player == null)
            return;

        ItemStack itemStack = getFlintAndSteel(player.getInventory());

        runTaskSync(() -> {
            if (selectedKit == null) {
                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);
                player.getInventory().clear();
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

                if (itemStack != null)
                    player.getInventory().addItem(itemStack);

                return;
            }

            player.getInventory().setContents(selectedKit.getItems());
            player.getInventory().setArmorContents(selectedKit.getArmour());

            if (itemStack != null)
                player.getInventory().addItem(itemStack);

            if (selectedKit.getPermanentPotions() != null && !selectedKit.getPermanentPotions().isEmpty()) {

                for (Effect effect : selectedKit.getPermanentPotions()) {
                    PotionEffect potionEffect = new PotionEffect(effect.getPotionEffectType(), 1000000000, effect.getAmplifier());
                    player.addPotionEffect(potionEffect, true);
                }

            }
        });
    }

    public synchronized void setSelectedKit(@NotNull Kit selectedKit, @NotNull ItemStack[] itemStacks) {
        this.selectedKit = selectedKit;
        Player player = getPlayer();

        if (player == null)
            return;

        ItemStack itemStack = getFlintAndSteel(player.getInventory());

        runTaskSync(() -> {
            player.getInventory().setContents(itemStacks);
            player.getInventory().setArmorContents(selectedKit.getArmour());

            if (itemStack != null)
                player.getInventory().addItem(itemStack);

            if (selectedKit.getPermanentPotions() != null && !selectedKit.getPermanentPotions().isEmpty()) {

                for (Effect effect : selectedKit.getPermanentPotions()) {
                    PotionEffect potionEffect = new PotionEffect(effect.getPotionEffectType(), 1000000000, effect.getAmplifier());
                    player.addPotionEffect(potionEffect, true);
                }

            }
        });
    }

    public synchronized void setSelectedPerk(@Nullable Perk selectedPerk) {
        this.selectedPerk = selectedPerk;
    }

    public synchronized void setActionbar(boolean actionbar) {
        this.actionbar = actionbar;
    }

    public synchronized void setSavedInventories(@NotNull ConcurrentHashMap<Kit, ItemStack[]> savedInventories) {
        this.savedInventories = savedInventories;
    }

    public synchronized ConcurrentHashMap<String, String> getDeserializedSavedInventories() {
        ConcurrentHashMap<String, String> deserializedSavedInventories = new ConcurrentHashMap<>();

        for (Kit kit : savedInventories.keySet()) {
            deserializedSavedInventories.put(kit.getName(), ItemDeserializer.itemStackArrayToBase64(savedInventories.get(kit)));
        }

        return deserializedSavedInventories;
    }

    @Nullable
    private ItemStack getFlintAndSteel(@NotNull PlayerInventory inventory) {
        boolean flintAndSteelFound = false;
        ItemStack flintAndSteel = null;
        short durability = 0;

        for (ItemStack itemStack : inventory.getContents()) {

            if (itemStack != null && itemStack.getType().equals(Material.FLINT_AND_STEEL)) {

                if (!flintAndSteelFound) {
                    flintAndSteelFound = true;
                    flintAndSteel = itemStack;
                    durability = itemStack.getDurability();
                } else {
                    runTaskSync(() -> inventory.remove(itemStack));
                    durability = (short) (durability - (itemStack.getDurability()));
                }

            }

        }

        if (flintAndSteelFound)
            flintAndSteel.setDurability(durability);

        return flintAndSteel;
    }

    public void save() {
        UsersDBManager.updateDB(this);
    }
}

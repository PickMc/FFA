package me.zxoir.pickmcffa.listener;

import lombok.Getter;
import me.neznamy.tab.shared.TAB;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.KillStreak;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.utils.ItemStackBuilder;
import me.zxoir.pickmcffa.utils.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 6/15/2022
 */
public class KillStreakListener implements Listener {
    @Getter
    private static final HashMap<User, KillStreak> killStreak = new HashMap<>();
    @Getter
    private static final HashMap<UUID, BukkitTask> bountyList = new HashMap<>();
    @Getter
    private static final ItemStack tntItem = new ItemStackBuilder(Material.TNT).withName("&e&lTNT").withLore("&7Place this and it will explode after 1 second!").resetFlags().build();

    @NotNull
    private static Material getLowerMaterial(@NotNull Material material) {
        if (material.name().toLowerCase().contains("diamond"))
            return Material.valueOf(material.name().toUpperCase(Locale.ROOT).replace("DIAMOND", "IRON"));

        else if (material.name().toLowerCase().contains("iron"))
            return Material.valueOf(material.name().toUpperCase(Locale.ROOT).replace("IRON", "STONE"));

        else if (material.name().toLowerCase().contains("stone"))
            return Material.valueOf(material.name().toUpperCase(Locale.ROOT).replace("STONE", "WOOD"));

        else if (material.equals(Material.WOOD_SWORD))
            return Material.WOOD_AXE;

        else if (material.equals(Material.WOOD_AXE))
            return Material.WOOD_SPADE;

        else return Material.WOOD_SPADE;
    }

    @Nullable
    private static ItemStack getWeapon(@NotNull ItemStack[] itemStacks) {
        if (itemStacks.length <= 0)
            return null;

        for (ItemStack itemStack : itemStacks) {

            if (itemStack == null)
                continue;

            if (!itemStack.getType().name().toLowerCase().contains("axe") && !itemStack.getType().name().toLowerCase().contains("sword") && !itemStack.getType().name().toLowerCase().contains("spade"))
                continue;

            return itemStack;

        }

        return null;
    }

    public static void killStreakReward(@NotNull Player player, int killStreak) {
        switch (killStreak) {

            case 3:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
                break;
            case 5:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!"));
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
                break;
            case 10:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!\n" +
                        "&7&l" + player.getName() + " &8has a Strength for 10 seconds!"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 0));
                break;
            case 15:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!"));
                player.sendMessage(colorize("&8You have received a &7&lTNT &8that will expire in &7&l15 Seconds&8! Use it quickly!"));
                player.getInventory().addItem(tntItem);
                Bukkit.getScheduler().scheduleSyncDelayedTask(PickMcFFA.getInstance(), () -> player.getInventory().remove(tntItem), 300);
                break;
            case 20:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!"));
                player.sendMessage(colorize("&8This is your final buff, enjoy it while it lasts!"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 1), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1), true);
                break;
            case 25:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!\n" +
                        "&7&l" + player.getName() + " &8has a Bounty and he's worth 3X XP and Coins!"));

                BukkitTask task = new BukkitRunnable() {

                    public void run() {

                        if (Bukkit.getPlayer(player.getUniqueId()) == null)
                            return;

                        PacketPlayOutWorldParticles packet =
                                new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(),
                                        0.5f, 0.5f, 0.5f, 5, 60);

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                        }

                    }
                }.runTaskTimerAsynchronously(PickMcFFA.getInstance(), 0, 5);

                bountyList.put(player.getUniqueId(), task);

                TAB.getInstance().getTeamManager().setPrefix(TAB.getInstance().getPlayer(player.getUniqueId()), "&4&lBOUNTY &c");
                break;
            case 30:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!\n" +
                        "&7&l" + player.getName() + " &8has a Bounty and he's worth 3X XP and Coins!"));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000000, 0));
                break;
            case 35:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!\n" +
                        "&7&l" + player.getName() + " &8has a Bounty and he's worth 3X XP and Coins!"));
                player.getInventory().setHelmet(null);
                break;
            case 40:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!\n" +
                        "&7&l" + player.getName() + " &8has a Bounty and he's worth 3X XP and Coins!"));
                ItemStack weapon = getWeapon(player.getInventory().getContents());
                if (weapon == null)
                    break;
                Material material = getLowerMaterial(weapon.getType());
                if (weapon.getType().equals(Material.WOOD_SPADE))
                    new ItemStackBuilder(weapon).clearEnchantments().withEnchantment(Enchantment.DAMAGE_ALL, 1).build();
                else new ItemStackBuilder(weapon).setType(material).build();
                break;
            case 45:
                Bukkit.broadcastMessage(colorize("&6&lKILLSTREAK > &a&l" + player.getName() + " &2has a " + killStreak + " killstreak!\n" +
                        "&7&l" + player.getName() + " &8has a Bounty and he's worth 3X XP and Coins!"));
                player.getInventory().setBoots(null);
                player.removePotionEffect(PotionEffectType.WEAKNESS);
                break;
            default:
                break;

        }
    }

    public static void KillStreakDebuff(@NotNull User user) {
        Player player = user.getPlayer();

        if (player == null)
            return;

        if (user.getStats().getKillsStreak() >= 30)
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000000, 0));

        if (user.getStats().getKillsStreak() >= 35)
            player.getInventory().setHelmet(null);

        if (user.getStats().getKillsStreak() >= 40) {
            ItemStack weapon = getWeapon(player.getInventory().getContents());

            if (weapon != null) {
                Material material = getLowerMaterial(weapon.getType());
                if (weapon.getType().equals(Material.WOOD_SPADE))
                    new ItemStackBuilder(weapon).clearEnchantments().withEnchantment(Enchantment.DAMAGE_ALL, 1).build();
                else new ItemStackBuilder(weapon).setType(material).build();
            }

        }

        if (user.getStats().getKillsStreak() >= 45) {
            player.getInventory().setBoots(null);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
        }
    }

    @EventHandler
    public void onPlace(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || itemStack == null || !itemStack.equals(tntItem) || event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR))
            return;

        Location location = event.getClickedBlock().getLocation();
        location.setY(event.getClickedBlock().getY() + 1);
        Block block = location.getBlock();
        if (!block.getType().equals(Material.AIR))
            return;

        event.setCancelled(true);
        player.getInventory().remove(tntItem);
        Entity primedTnt = block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
        ((TNTPrimed) primedTnt).setFuseTicks(20);
        ArmorStand hologram = createTntTimerStand(primedTnt.getLocation());
        new BukkitRunnable() {

            @Override
            public void run() {
                double timer = Double.parseDouble(hologram.getCustomName());
                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(2);

                if (timer <= 0) {
                    hologram.remove();
                    this.cancel();
                    return;
                }

                hologram.setCustomName(df.format(timer - 0.05) + "");
            }

        }.runTaskTimerAsynchronously(PickMcFFA.getInstance(), 0, 1);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        Player player = event.getPlayer();

        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        if (bountyList.containsKey(player.getUniqueId())) {
            TAB.getInstance().getTeamManager().setPrefix(TAB.getInstance().getPlayer(player.getUniqueId()), "&4&lBOUNTY &c");
        }

        if (user.getStats().getKillsStreak() >= 25 && !bountyList.containsKey(player.getUniqueId())) {
            BukkitTask task = new BukkitRunnable() {

                public void run() {

                    if (Bukkit.getPlayer(player.getUniqueId()) == null)
                        return;

                    PacketPlayOutWorldParticles packet =
                            new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(),
                                    0.5f, 0.5f, 0.5f, 5, 60);

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
                    }

                }

            }.runTaskTimerAsynchronously(PickMcFFA.getInstance(), 0, 5);

            bountyList.put(player.getUniqueId(), task);

            TAB.getInstance().getTeamManager().setPrefix(TAB.getInstance().getPlayer(player.getUniqueId()), "&4&lBOUNTY &c");
        }

    }

    @EventHandler
    public void onFall(@NotNull EntityDamageEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER) || !event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
            return;

        /* Checking if the player has a Perk selected */
        Player player = (Player) event.getEntity();
        User user = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());

        if (user == null)
            return;

        if (user.getStats().getKillsStreak() >= 30)
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1000000000, 0));

        if (user.getStats().getKillsStreak() >= 35)
            player.getInventory().setHelmet(null);

        if (user.getStats().getKillsStreak() >= 40) {
            ItemStack weapon = getWeapon(player.getInventory().getContents());

            if (weapon != null) {
                Material material = getLowerMaterial(weapon.getType());
                if (weapon.getType().equals(Material.WOOD_SPADE))
                    new ItemStackBuilder(weapon).clearEnchantments().withEnchantment(Enchantment.DAMAGE_ALL, 1).build();
                else new ItemStackBuilder(weapon).setType(material).build();
            }

        }

        if (user.getStats().getKillsStreak() >= 45) {
            player.getInventory().setBoots(null);
            player.removePotionEffect(PotionEffectType.WEAKNESS);
        }
    }

    @EventHandler
    public void onKill(@NotNull PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer == null)
            return;

        User playerUser = PickMcFFA.getCachedUsers().getIfPresent(player.getUniqueId());
        User killerUser = PickMcFFA.getCachedUsers().getIfPresent(killer.getUniqueId());

        if (playerUser == null)
            return;

        Stats stats = playerUser.getStats();
        stats.setKillsStreak(0);

        if (killStreak.containsKey(playerUser)) {
            KillStreak playerKillStreak = killStreak.get(playerUser);

            // New Max Kill Streak Reached
            if (playerKillStreak.getCount() > stats.getMaxKillStreaks()) {
                stats.setMaxKillStreaks(playerKillStreak.getCount());
                playerUser.save();
            }

            killStreak.remove(playerUser);
        }

        if (killerUser == null)
            return;

        if (!killStreak.containsKey(killerUser))
            killStreak.put(killerUser, new KillStreak(playerUser, killerUser.getStats().getKillsStreak()));

        KillStreak killerStreak = killStreak.get(killerUser);

        // If the player kills a new player other than the last two kills
        if (!killerStreak.getUser().equals(playerUser) && (killerStreak.getUser2() != null && killerStreak.getUser2().equals(playerUser))) {
            killerStreak.setUser2(null);
            killerStreak.setSameUserCount2(0);
            killerStreak.setSameUserCount(1);
        }

        if (killerStreak.getSameUserCount() == 4 || killerStreak.getSameUserCount2() == 4) {
            Utils.sendActionText(killer, "&c&lYou are Kill Farming!");
            return;
        }

        if (killerStreak.getUser().equals(playerUser))
            killerStreak.setSameUserCount(killerStreak.getSameUserCount() + 1);
        else
            killerStreak.setUser(playerUser);

        killerStreak.setCount(killerStreak.getCount() + 1);
        killerUser.getStats().setKillsStreak(killerStreak.getCount());
        killerUser.save();
        killStreakReward(killer, killerStreak.getCount());
    }

    @NotNull
    private ArmorStand createTntTimerStand(@NotNull Location location) {
        location.setY(location.getY() + 1);
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setSmall(true);
        hologram.setCustomName("1.0");
        hologram.setCustomNameVisible(true);
        hologram.setBasePlate(false);

        return hologram;
    }
}

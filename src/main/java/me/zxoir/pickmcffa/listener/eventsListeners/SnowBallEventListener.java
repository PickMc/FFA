package me.zxoir.pickmcffa.listener.eventsListeners;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.managers.EventsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/19/2022
 */
public class SnowBallEventListener implements Listener {
    //Set<Block> blocks = new HashSet<>();
    final static ConcurrentHashMap<Block, Boolean> blockHashMap = new ConcurrentHashMap<>();

    @EventHandler
    public void onShovel(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = player.getItemInHand();

        if (EventsManager.isEventActive() && EventsManager.getCurrentEventType().equals(EventsManager.EventType.SNOWBALL)) {
            if ((action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) && itemInHand.getType().equals(Material.DIAMOND_SPADE)) {

                Snowball snowball = event.getPlayer().launchProjectile(Snowball.class);
                player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                //snowball.setVelocity(snowball.getVelocity().multiply(5));\

            }
        }
    }

    @NotNull
    public static ArrayList<Block> getBlocks(@NotNull Block start, int radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++) {
            for (double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++) {
                for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++) {
                    Location loc = new Location(start.getWorld(), x, y, z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
    }

    private static boolean sendBlockChange(@NotNull Block block, Material material, byte data, int delay) {

        if (blockHashMap.containsKey(block))
            blockHashMap.put(block, false);

        if (block.getType().equals(Material.GRASS) || block.getType().equals(Material.DIRT)) {
            Block topBlock = block.getLocation().add(0, 1, 0).getBlock();
            Material topBlockMaterial = topBlock.getType();
            byte topBlockData = topBlock.getData();
            boolean validTopBlock = !topBlock.getType().equals(Material.AIR) && !topBlock.getType().equals(Material.PACKED_ICE) && (topBlock.getType().equals(Material.LONG_GRASS) || topBlock.getType().equals(Material.RED_ROSE));

            if (topBlock.getType().equals(Material.GRASS) || topBlock.getType().equals(Material.DIRT))
                return false;

            Bukkit.getScheduler().runTask(PickMcFFA.getInstance(), () -> {
                if (validTopBlock)
                    topBlock.setType(Material.AIR);
                block.setType(Material.PACKED_ICE);
            });

            //player.sendBlockChange(block.getLocation(), Material.PACKED_ICE, (byte) 0);
            if (!blockHashMap.containsKey(block))
                blockHashMap.put(block, true);


            if (validTopBlock)
                changeBlockLater(block, material, data, topBlockMaterial, topBlockData, delay);
            else
                changeBlockLater(block, material, data, delay);

            return true;
        }

        return false;
    }

    private static void changeBlockLater(@NotNull Block block, Material material, byte data, int delay) {
        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> {
            if (!blockHashMap.containsKey(block)) {

                if (block.getType().equals(Material.PACKED_ICE))
                    Bukkit.getScheduler().runTask(PickMcFFA.getInstance(), () -> {
                        block.setType(material);
                        block.setData(data);
                    });

                return;
            }

            if (!blockHashMap.get(block)) {
                changeBlockLater(block, material, data, delay);
                blockHashMap.put(block, true);
            } else {
                Bukkit.getScheduler().runTask(PickMcFFA.getInstance(), () -> {
                    block.setType(material);
                    block.setData(data);
                });
                blockHashMap.remove(block);
            }
        }, delay);
    }

    private static void changeBlockLater(@NotNull Block block, Material material, byte data, Material material2, byte data2, int delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(PickMcFFA.getInstance(), () -> {
            if (!blockHashMap.containsKey(block)) {

                if (block.getType().equals(Material.PACKED_ICE))
                    Bukkit.getScheduler().runTask(PickMcFFA.getInstance(), () -> {
                        block.setType(material);
                        block.setData(data);
                        Block topBlock = block.getLocation().add(0, 1, 0).getBlock();
                        topBlock.setType(material2);
                        topBlock.setData(data2);
                    });

                return;
            }

            if (!blockHashMap.get(block)) {
                changeBlockLater(block, material, data, material2, data2, delay);
                blockHashMap.put(block, true);
            } else {
                Bukkit.getScheduler().runTask(PickMcFFA.getInstance(), () -> {
                    block.setType(material);
                    block.setData(data);
                    Block topBlock = block.getLocation().add(0, 1, 0).getBlock();
                    topBlock.setType(material2);
                    topBlock.setData(data2);
                });
                blockHashMap.remove(block);
            }
        }, delay);
    }

    public static BukkitTask getMoveEvent() {
        return new BukkitRunnable() {

            @Override
            public void run() {

                for (Player player : EventsManager.getPlayersAlive()) {

                    if (EventsManager.isEventActive() && EventsManager.getCurrentEventType().equals(EventsManager.EventType.SNOWBALL)) {
                        Block block = player.getLocation().subtract(0, 1, 0).getBlock();

                        for (Block radiusBlock : getBlocks(block, 3)) {
                            if (radiusBlock == null || radiusBlock.getType().equals(Material.AIR))
                                continue;

                            sendBlockChange(radiusBlock, radiusBlock.getType(), radiusBlock.getData(), 10);
                        }

                    } else {
                        cancel();
                        return;
                    }

                }

            }

        }.runTaskTimerAsynchronously(PickMcFFA.getInstance(), 0, 2);
    }

    @EventHandler
    public void onSnowBallHit(@NotNull EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Snowball) {

            if (EventsManager.isEventActive() && EventsManager.getCurrentEventType().equals(EventsManager.EventType.SNOWBALL)) {

                Snowball sn = (Snowball) event.getDamager();
                if (sn.getShooter() instanceof Player && event.getEntity() instanceof Player) {
                    Player shooter = (Player) sn.getShooter();
                    Player player = (Player) event.getEntity();
                    event.setCancelled(true);

                    if (shooter.equals(player))
                        return;

                    EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, shooter, EntityDamageEvent.DamageCause.ENTITY_ATTACK, 0);
                    Bukkit.getPluginManager().callEvent(entityDamageByEntityEvent);
                    player.setLastDamageCause(entityDamageByEntityEvent);

                    player.damage(5);
                    player.setVelocity(shooter.getLocation().getDirection().multiply(2));
                }

            }

        }

    }

}

package me.zxoir.pickmcffa.menus;

import com.Zrips.CMI.CMI;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.Selection;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.listener.EventsListener;
import me.zxoir.pickmcffa.listener.eventsListeners.SnowBallEventListener;
import me.zxoir.pickmcffa.managers.KitManager;
import me.zxoir.pickmcffa.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static me.zxoir.pickmcffa.utils.Utils.colorize;
import static me.zxoir.pickmcffa.utils.Utils.runTaskSync;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/21/2022
 */
public class EventsManager {
    @Getter
    private static final List<Player> playersAlive = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private static final List<Player> toRemoveKit = Collections.synchronizedList(new ArrayList<>());
    private static final Random random = new Random();
    private static final int delayPerEvent = 72000;
    private static final int eventDuration = 12000;
    private static final Location spawnPoint = CMI.getInstance().getConfigManager().getFirstSpawnPoint();
    @Getter
    @Setter
    private static Selection barrierWall;
    @Getter
    @Setter
    private static Location teleportPoint;
    @Getter
    @Setter
    private static boolean isEventActive;
    @Getter
    @Setter
    private static EventsManager.EventType currentEventType;
    private static BukkitTask scheduledAnnounceTask;
    private static BukkitTask scheduledStartTask;
    private static BukkitTask endEventTask;
    private static BukkitTask eventMoveTask;
    private static BukkitTask countdownTask;
    private static boolean spawnedBarrierWall = false;

    private static void removeBarrierWall() {
        if (barrierWall == null)
            return;

        spawnedBarrierWall = false;

        LocalWorld localWorld = BukkitUtil.getLocalWorld(barrierWall.getWorld());
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
        try {
            editSession.setBlocks(barrierWall.getRegionSelector().getRegion(), new BaseBlock(BlockID.AIR));
        } catch (MaxChangedBlocksException | IncompleteRegionException e) {
            // As of the blocks are unlimited this should not be called
        }
    }

    private static void spawnBarrierWall() {
        if (barrierWall == null)
            return;

        spawnedBarrierWall = true;

        LocalWorld localWorld = BukkitUtil.getLocalWorld(barrierWall.getWorld());
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
        try {
            editSession.setBlocks(barrierWall.getRegionSelector().getRegion(), new BaseBlock(BlockID.BARRIER));
        } catch (MaxChangedBlocksException | IncompleteRegionException e) {
            // As of the blocks are unlimited this should not be called
        }
    }

    private static EventsManager.EventType getRandomEvent() {
        return EventsManager.EventType.values()[random.nextInt(EventsManager.EventType.values().length)];
    }

    private static BukkitTask getScheduledAnnounceTask() {
        return Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> {
            Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &9Event starting in 1 minute"));
            scheduledStartTask = getScheduledStartTask();
        }, delayPerEvent);
    }

    private static BukkitTask getScheduledStartTask() {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(PickMcFFA.getInstance(), () -> startEvent(null), 120);
    }

    private static BukkitTask getEndEventTask() {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(PickMcFFA.getInstance(), () -> endEvent(null), eventDuration);
    }

    private static BukkitTask getCountdownTask(int time) {
        runTaskSync(() -> teleportPoint.getWorld().setPVP(false));

        return new BukkitRunnable() {
            int timer = time;

            @Override
            public void run() {

                if (timer == 0) {
                    cancel();
                    Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &9Pvp is enabled!"));
                    runTaskSync(() -> teleportPoint.getWorld().setPVP(true));
                    return;
                }

                Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &9Pvp will enable in &9&l" + timer + " seconds"));

                timer--;
            }

        }.runTaskTimer(PickMcFFA.getInstance(), 0, 20);
    }

    private static void cancelSchedulers() {
        if (scheduledAnnounceTask != null)
            scheduledAnnounceTask.cancel();

        if (scheduledStartTask != null)
            scheduledStartTask.cancel();

        if (endEventTask != null)
            endEventTask.cancel();

        if (eventMoveTask != null)
            eventMoveTask.cancel();

        if (countdownTask != null)
            countdownTask.cancel();
    }

    public static void eliminate(@NotNull Player player, @Nullable Player killer) {
        playersAlive.remove(player);

        if (killer == null)
            runTaskSync(() -> Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &c&l" + player.getName() + " &chas been eliminated")));
        else {
            runTaskSync(() -> {
                Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &c&l" + player.getName() + " &chas been eliminated by &c&l" + killer.getName()));
                killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 1, 1);
            });
        }

        if (playersAlive.size() == 1)
            endEvent(playersAlive.get(0));
        else if (playersAlive.isEmpty())
            endEvent(null);

        toRemoveKit.add(player);
    }

    public static void startEvent(EventType eventType) {
        if (isEventActive)
            endEvent(null);

        if (barrierWall == null || teleportPoint == null || Bukkit.getOnlinePlayers().size() <= 1)
            return;

        isEventActive = true;
        currentEventType = eventType == null ? getRandomEvent() : eventType;
        endEventTask = getEndEventTask();

        switch (currentEventType) {

            case SNOWBALL:
                Bukkit.getScheduler().runTaskLaterAsynchronously(PickMcFFA.getInstance(), () -> eventMoveTask = SnowBallEventListener.getMoveEvent(), 20);
                runTaskSync(() -> Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &9Snowball event started!")));

                Bukkit.getOnlinePlayers().forEach(online -> {
                    runTaskSync(() -> {
                        online.teleport(teleportPoint);
                        online.setHealth(online.getMaxHealth());
                    });
                    playersAlive.add(online);
                    User user = PickMcFFA.getCachedUsers().getIfPresent(online.getUniqueId());
                    if (user != null) {
                        user.setSelectedKit(KitManager.getSnowballKit());
                    }
                });

                spawnBarrierWall();
                countdownTask = getCountdownTask(5);
                break;

            case LAST_MAN_STANDING:
                runTaskSync(() -> Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &9Last Man Standing event started!")));
                runTaskSync(() -> Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &9Choose a Kit!")));

                Bukkit.getOnlinePlayers().forEach(online -> {
                    User user = PickMcFFA.getCachedUsers().getIfPresent(online.getUniqueId());
                    if (user != null) {
                        user.setSelectedKit(null);
                    }

                    runTaskSync(() -> {
                        online.teleport(teleportPoint);
                        online.openInventory(Utils.duplicateInventory(KitMenu.getInventory()));
                        online.setHealth(online.getMaxHealth());
                    });

                    playersAlive.add(online);
                });

                spawnBarrierWall();
                countdownTask = getCountdownTask(10);
                break;
        }

    }

    public static void endEvent(@Nullable Player winner) {
        playersAlive.clear();
        cancelSchedulers();
        scheduledAnnounceTask = getScheduledAnnounceTask();
        isEventActive = false;
        currentEventType = null;

        if (spawnedBarrierWall)
            removeBarrierWall();

        Bukkit.getScheduler().runTaskLater(PickMcFFA.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(online -> {
            User user = PickMcFFA.getCachedUsers().getIfPresent(online.getUniqueId());
            runTaskSync(() -> {
                online.teleport(spawnPoint);
                online.setHealth(online.getMaxHealth());
            });

            if (user != null)
                user.setSelectedKit(null);
        }), 10);

        if (winner != null) {
            User user = PickMcFFA.getCachedUsers().getIfPresent(winner.getUniqueId());
            if (user == null)
                return;

            int coins = user.getStats().addCoins(100, 500);
            runTaskSync(() -> winner.sendMessage(colorize("&6+" + coins + " for winning the event")));
            user.getStats().setEventsWon(user.getStats().getEventsWon() + 1);
            runTaskSync(() -> Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &b&l" + winner.getName() + " &bhas won the event!")));
            return;
        }

        runTaskSync(() -> Bukkit.broadcastMessage(colorize("&a&lFFA Events &7> &bNo one won the event")));
    }

    /*private static void setIceMap() {
        LocalWorld localWorld = BukkitUtil.getLocalWorld(mapSelection.getWorld());
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
        try {
            Set<BaseBlock> baseBlocks = new HashSet<>();
            baseBlocks.add(new BaseBlock(BlockID.DIRT));
            baseBlocks.add(new BaseBlock(BlockID.GRASS));
            editSession.replaceBlocks(mapSelection.getRegionSelector().getRegion(), baseBlocks, new BaseBlock(BlockID.ICE));
        } catch (MaxChangedBlocksException | IncompleteRegionException e) {
            // As of the blocks are unlimited this should not be called
        }
    }

    private static void removeIce() {
        LocalWorld localWorld = BukkitUtil.getLocalWorld(mapSelection.getWorld());
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(localWorld, -1);
        editSession.undo(editSession);
    }*/

    public enum EventType {
        SNOWBALL,
        LAST_MAN_STANDING
        //TNT_TAG
    }
}

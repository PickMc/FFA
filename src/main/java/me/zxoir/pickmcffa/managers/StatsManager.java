package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.listener.LeaderboardHeads;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2022
 */
public class StatsManager {
    @Getter
    private static final List<Stats> cachedStats = new ArrayList<>();
    private static LinkedHashMap<String, Integer> topKills = new LinkedHashMap<>();
    private static LinkedHashMap<String, Integer> topKillStreak = new LinkedHashMap<>();
    private static LinkedHashMap<String, Integer> topDeaths = new LinkedHashMap<>();
    private static LinkedHashMap<String, Integer> topLevel = new LinkedHashMap<>();
    private static Instant lastCached;

    public static void cacheStats() {
        if (PickMcFFA.getCachedUsers().asMap().isEmpty())
            return;

        if (lastCached != null && Duration.between(lastCached, Instant.now()).toMinutes() < 5)
            return;

        lastCached = Instant.now();
        System.out.println("Cached Stats");
        PickMcFFA.getCachedUsers().asMap().values().forEach(user -> cachedStats.add(user.getStats()));

        cachedStats.forEach(stat -> topDeaths.put(Bukkit.getOfflinePlayer(stat.getUuid()).getName(), stat.getDeaths()));
        cachedStats.forEach(stat -> topKills.put(Bukkit.getOfflinePlayer(stat.getUuid()).getName(), stat.getKills().size()));
        cachedStats.forEach(stat -> topKillStreak.put(Bukkit.getOfflinePlayer(stat.getUuid()).getName(), stat.getMaxKillStreaks()));
        cachedStats.forEach(stat -> topLevel.put(Bukkit.getOfflinePlayer(stat.getUuid()).getName(), stat.getLevel()));
        topDeaths = sortByValue(topDeaths);
        topKills = sortByValue(topKills);
        topKillStreak = sortByValue(topKillStreak);
        topLevel = sortByValue(topLevel);

        LeaderboardHeads.refreshSigns();
    }

    private static LinkedHashMap<String, Integer> sortByValue(@NotNull LinkedHashMap<String, Integer> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, java.util.LinkedHashMap::new));
    }

    public static LinkedHashMap<String, Integer> getTopKills() {
        cacheStats();
        return topKills;
    }

    public static LinkedHashMap<String, Integer> getTopDeaths() {
        cacheStats();
        return topDeaths;
    }

    public static LinkedHashMap<String, Integer> getTopKillStreak() {
        cacheStats();
        return topKillStreak;
    }

    public static LinkedHashMap<String, Integer> getTopLevel() {
        cacheStats();
        return topLevel;
    }
}

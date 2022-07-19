package me.zxoir.pickmcffa.managers;

import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.customclasses.Stats;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.utils.Title;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/24/2022
 */
public class UserManager {

    public static void levelUp(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        User playerUser = PickMcFFA.getCachedUsers().getIfPresent(uuid);

        if (playerUser == null)
            return;

        Stats stats = playerUser.getStats();

        if (stats.getXp() < stats.getLevelUpXp())
            return;

        int oldLevel = stats.getLevel();
        int newLevel = getNewLevel(stats, stats.getLevel() + 1, stats.getXp() - stats.getLevelUpXp());
        stats.setLevel(newLevel);

        if (player == null)
            return;

        Title title = new Title();
        title.send(player, 1, 3, 1, ConfigManager.getLevelUpTitle(player.getName(), newLevel, oldLevel), ConfigManager.getLevelUpSubtitle(player.getName(), newLevel, oldLevel));
        Bukkit.broadcastMessage(ConfigManager.getLevelUpBroadcast(player.getName(), newLevel, oldLevel));
    }

    private static int getNewLevel(@NotNull Stats stats, int level, int xp) {
        if (xp >= stats.getLevelUpXp(level))
            return getNewLevel(stats, stats.getLevelUpXp(level + 1), xp - stats.getLevelUpXp(level + 1));
        stats.setXp(xp);
        return level;
    }

    @Nullable
    public static Double getXpBoost(@NotNull Player player) {
        if (player.isOp())
            return null;

        if (player.hasPermission("boost.xp"))
            return PickMcFFA.getInstance().getConfig().getDouble("XpBoosts.permission");

        for (String group : PickMcFFA.getInstance().getConfig().getConfigurationSection("XpBoosts").getKeys(true)) {
            if (player.hasPermission("group." + group))
                return PickMcFFA.getInstance().getConfig().getDouble("XpBoosts." + group);
        }


        return null;
    }

    @Nullable
    public static Double getCoinBoost(@NotNull Player player) {

        if (player.isOp())
            return null;

        if (player.hasPermission("boost.coin"))
            return PickMcFFA.getInstance().getConfig().getDouble("CoinBoosts.permission");

        for (String group : PickMcFFA.getInstance().getConfig().getConfigurationSection("CoinBoosts").getKeys(true)) {
            if (player.hasPermission("group." + group))
                return PickMcFFA.getInstance().getConfig().getDouble("CoinBoosts." + group);
        }


        return null;
    }

}

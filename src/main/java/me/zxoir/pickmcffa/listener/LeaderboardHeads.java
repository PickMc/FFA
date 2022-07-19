package me.zxoir.pickmcffa.listener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.managers.StatsManager;
import me.zxoir.pickmcffa.utils.LocationAdapter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 7/4/2022
 */

@SuppressWarnings("SuspiciousMethodCalls")
public class LeaderboardHeads implements Listener {
    @Getter
    @Setter
    private static Sign topOneKiller;
    @Getter
    @Setter
    private static Sign topTwoKiller;
    @Getter
    @Setter
    private static Sign topThreeKiller;
    @Getter
    @Setter
    private static Sign topOneLevel;
    @Getter
    @Setter
    private static Sign topTwoLevel;
    @Getter
    @Setter
    private static Sign topThreeLevel;

    public static void refreshSigns() {
        Map<String, Integer> topKillers = StatsManager.getTopKills();
        Map<String, Integer> topLevels = StatsManager.getTopLevel();

        if (topOneKiller != null) {
            if (topKillers.size() < 1) return;

            int topKill = topKillers.get(topKillers.keySet().toArray()[0]);
            String topKiller = (String) topKillers.keySet().toArray()[0];
            topOneKiller.setLine(0, colorize("&b&l" + topKiller));
            topOneKiller.setLine(1, colorize("&7" + topKill + " Kills"));
            topOneKiller.setLine(2, "");
            topOneKiller.setLine(3, colorize("&7#1"));
            topOneKiller.update();

            Block blockBehindSign = getBlockBehindSign(topOneKiller);
            if (blockBehindSign != null) {
                Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                if (blockSkull.getType().equals(Material.SKULL)) {
                    Skull skull = (Skull) blockSkull.getState();
                    skull.setOwner(topKiller);
                    skull.setSkullType(SkullType.PLAYER);
                    skull.update();
                }
            }
        }

        if (topTwoKiller != null) {
            if (topKillers.size() < 2) return;

            int topKill = topKillers.get(topKillers.keySet().toArray()[1]);
            String topKiller = (String) topKillers.keySet().toArray()[1];
            topTwoKiller.setLine(0, colorize("&b&l" + topKiller));
            topTwoKiller.setLine(1, colorize("&7" + topKill + " Kills"));
            topTwoKiller.setLine(2, "");
            topTwoKiller.setLine(3, colorize("&7#2"));
            topTwoKiller.update();

            Block blockBehindSign = getBlockBehindSign(topTwoKiller);
            if (blockBehindSign != null) {
                Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                if (blockSkull.getType().equals(Material.SKULL)) {
                    Skull skull = (Skull) blockSkull.getState();
                    skull.setOwner(topKiller);
                    skull.setSkullType(SkullType.PLAYER);
                    skull.update();
                }
            }
        }

        if (topThreeKiller != null) {
            if (topKillers.size() < 3) return;

            int topKill = topKillers.get(topKillers.keySet().toArray()[2]);
            String topKiller = (String) topKillers.keySet().toArray()[2];
            topThreeKiller.setLine(0, colorize("&b&l" + topKiller));
            topThreeKiller.setLine(1, colorize("&7" + topKill + " Kills"));
            topThreeKiller.setLine(2, "");
            topThreeKiller.setLine(3, colorize("&7#3"));
            topThreeKiller.update();

            Block blockBehindSign = getBlockBehindSign(topThreeKiller);
            if (blockBehindSign != null) {
                Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                if (blockSkull.getType().equals(Material.SKULL)) {
                    Skull skull = (Skull) blockSkull.getState();
                    skull.setOwner(topKiller);
                    skull.setSkullType(SkullType.PLAYER);
                    skull.update();
                }
            }
        }

        if (topOneLevel != null) {
            if (topLevels.size() < 1) return;

            int topLevel = topLevels.get(topLevels.keySet().toArray()[0]);
            String topLevelName = (String) topLevels.keySet().toArray()[0];
            topOneLevel.setLine(0, colorize("&b&l" + topLevelName));
            topOneLevel.setLine(1, colorize("&7" + topLevel + " Levels"));
            topOneLevel.setLine(2, "");
            topOneLevel.setLine(3, colorize("&7#1"));
            topOneLevel.update();

            Block blockBehindSign = getBlockBehindSign(topOneLevel);
            if (blockBehindSign != null) {
                Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                if (blockSkull.getType().equals(Material.SKULL)) {
                    Skull skull = (Skull) blockSkull.getState();
                    skull.setOwner(topLevelName);
                    skull.setSkullType(SkullType.PLAYER);
                    skull.update();
                }
            }
        }

        if (topTwoLevel != null) {
            if (topLevels.size() < 2) return;

            int topLevel = topLevels.get(topLevels.keySet().toArray()[1]);
            String topLevelName = (String) topLevels.keySet().toArray()[1];
            topTwoLevel.setLine(0, colorize("&b&l" + topLevelName));
            topTwoLevel.setLine(1, colorize("&7" + topLevel + " Levels"));
            topTwoLevel.setLine(2, "");
            topTwoLevel.setLine(3, colorize("&7#2"));
            topTwoLevel.update();

            Block blockBehindSign = getBlockBehindSign(topTwoLevel);
            if (blockBehindSign != null) {
                Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                if (blockSkull.getType().equals(Material.SKULL)) {
                    Skull skull = (Skull) blockSkull.getState();
                    skull.setOwner(topLevelName);
                    skull.setSkullType(SkullType.PLAYER);
                    skull.update();
                }
            }
        }

        if (topThreeLevel != null) {
            if (topLevels.size() < 3) return;

            int topLevel = topLevels.get(topLevels.keySet().toArray()[2]);
            String topLevelName = (String) topLevels.keySet().toArray()[2];
            topThreeLevel.setLine(0, colorize("&b&l" + topLevelName));
            topThreeLevel.setLine(1, colorize("&7" + topLevel + " Levels"));
            topThreeLevel.setLine(2, "");
            topThreeLevel.setLine(3, colorize("&7#3"));
            topThreeLevel.update();

            Block blockBehindSign = getBlockBehindSign(topThreeLevel);
            if (blockBehindSign != null) {
                Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                if (blockSkull.getType().equals(Material.SKULL)) {
                    Skull skull = (Skull) blockSkull.getState();
                    skull.setOwner(topLevelName);
                    skull.setSkullType(SkullType.PLAYER);
                    skull.update();
                }
            }
        }
    }

    @Nullable
    private static Block getBlockBehindSign(@NotNull Sign sign) {
        MaterialData data = sign.getData();
        if (data instanceof Directional) {
            Directional directional = (Directional) data;
            return sign.getBlock().getRelative(directional.getFacing().getOppositeFace());
        }

        return null;
    }

    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent event) {
        Block clicked = event.getClickedBlock();
        if (event.getClickedBlock() == null || event.getClickedBlock().getState() == null) return;
        BlockState state = clicked.getState();

        if (!(state instanceof Sign)) return;
        Sign sign = (Sign) clicked.getState();


        if (!sign.getLine(0).equalsIgnoreCase("[PickMc]")) return;

        if (sign.getLine(1).equalsIgnoreCase("Level")) {
            if (PickMcFFA.getCachedUsers().asMap().isEmpty()) return;
            Map<String, Integer> topLevels = StatsManager.getTopLevel();

            if (sign.getLine(2).equalsIgnoreCase("first")) {
                if (topLevels.keySet().toArray()[0] == null) return;
                int topLevel = topLevels.get(topLevels.keySet().toArray()[0]);
                String topLevelName = (String) topLevels.keySet().toArray()[0];
                sign.setLine(0, colorize("&b&l" + topLevelName));
                sign.setLine(1, colorize("&7" + topLevel + " Levels"));
                sign.setLine(2, "");
                sign.setLine(3, colorize("&7#1"));
                sign.update();

                Block blockBehindSign = getBlockBehindSign(sign);
                if (blockBehindSign != null) {
                    Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                    if (blockSkull.getType().equals(Material.SKULL)) {
                        Skull skull = (Skull) blockSkull.getState();
                        skull.setOwner(topLevelName);
                        skull.setSkullType(SkullType.PLAYER);
                        skull.update();
                    }
                }

                topOneLevel = sign;
                Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
                PickMcFFA.getDataFile().getConfig().set("ldh.levels.one", gson.toJson(sign.getLocation(), Location.class));
                PickMcFFA.getDataFile().saveConfig();
            }

            if (sign.getLine(2).equalsIgnoreCase("second")) {
                if (topLevels.keySet().toArray().length < 2 || topLevels.keySet().toArray()[1] == null) return;

                int topLevel = topLevels.get(topLevels.keySet().toArray()[1]);
                String topLevelName = (String) topLevels.keySet().toArray()[1];
                sign.setLine(0, colorize("&b&l" + topLevelName));
                sign.setLine(1, colorize("&7" + topLevel + " Levels"));
                sign.setLine(2, "");
                sign.setLine(3, colorize("&7#2"));
                sign.update();

                Block blockBehindSign = getBlockBehindSign(sign);
                if (blockBehindSign != null) {
                    Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                    if (blockSkull.getType().equals(Material.SKULL)) {
                        Skull skull = (Skull) blockSkull.getState();
                        skull.setOwner(topLevelName);
                        skull.setSkullType(SkullType.PLAYER);
                        skull.update();
                    }
                }

                topTwoLevel = sign;

                Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
                PickMcFFA.getDataFile().getConfig().set("ldh.levels.two", gson.toJson(sign.getLocation(), Location.class));
                PickMcFFA.getDataFile().saveConfig();
            }

            if (sign.getLine(2).equalsIgnoreCase("third")) {
                if (topLevels.keySet().toArray().length < 3 || topLevels.keySet().toArray()[2] == null) return;

                int topLevel = topLevels.get(topLevels.keySet().toArray()[2]);
                String topLevelName = (String) topLevels.keySet().toArray()[2];
                sign.setLine(0, colorize("&b&l" + topLevelName));
                sign.setLine(1, colorize("&7" + topLevel + " Levels"));
                sign.setLine(2, "");
                sign.setLine(3, colorize("&7#3"));
                sign.update();

                Block blockBehindSign = getBlockBehindSign(sign);
                if (blockBehindSign != null) {
                    Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                    if (blockSkull.getType().equals(Material.SKULL)) {
                        Skull skull = (Skull) blockSkull.getState();
                        skull.setOwner(topLevelName);
                        skull.setSkullType(SkullType.PLAYER);
                        skull.update();
                    }
                }

                topThreeLevel = sign;
                Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
                PickMcFFA.getDataFile().getConfig().set("ldh.levels.three", gson.toJson(sign.getLocation(), Location.class));
                PickMcFFA.getDataFile().saveConfig();
            }

        } else if (sign.getLine(1).equalsIgnoreCase("Kills")) {
            if (PickMcFFA.getCachedUsers().asMap().isEmpty()) return;
            Map<String, Integer> topKillers = StatsManager.getTopKills();

            if (sign.getLine(2).equalsIgnoreCase("first")) {
                if (topKillers.keySet().toArray()[0] == null) return;
                int topKill = topKillers.get(topKillers.keySet().toArray()[0]);
                String topKiller = (String) topKillers.keySet().toArray()[0];
                sign.setLine(0, colorize("&b&l" + topKiller));
                sign.setLine(1, colorize("&7" + topKill + " Kills"));
                sign.setLine(2, "");
                sign.setLine(3, colorize("&7#1"));
                sign.update();

                Block blockBehindSign = getBlockBehindSign(sign);
                if (blockBehindSign != null) {
                    Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                    if (blockSkull.getType().equals(Material.SKULL)) {
                        Skull skull = (Skull) blockSkull.getState();
                        skull.setOwner(topKiller);
                        skull.setSkullType(SkullType.PLAYER);
                        skull.update();
                    }
                }

                topOneKiller = sign;
                Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
                PickMcFFA.getDataFile().getConfig().set("ldh.kills.one", gson.toJson(sign.getLocation(), Location.class));
                PickMcFFA.getDataFile().saveConfig();
            }

            if (sign.getLine(2).equalsIgnoreCase("second")) {
                if (topKillers.keySet().toArray().length < 2 || topKillers.keySet().toArray()[1] == null) return;

                int topKill = topKillers.get(topKillers.keySet().toArray()[1]);
                String topKiller = (String) topKillers.keySet().toArray()[1];
                sign.setLine(0, colorize("&b&l" + topKiller));
                sign.setLine(1, colorize("&7" + topKill + " Kills"));
                sign.setLine(2, "");
                sign.setLine(3, colorize("&7#2"));
                sign.update();

                Block blockBehindSign = getBlockBehindSign(sign);
                if (blockBehindSign != null) {
                    Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                    if (blockSkull.getType().equals(Material.SKULL)) {
                        Skull skull = (Skull) blockSkull.getState();
                        skull.setOwner(topKiller);
                        skull.setSkullType(SkullType.PLAYER);
                        skull.update();
                    }
                }

                topTwoKiller = sign;

                Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
                PickMcFFA.getDataFile().getConfig().set("ldh.kills.two", gson.toJson(sign.getLocation(), Location.class));
                PickMcFFA.getDataFile().saveConfig();
            }

            if (sign.getLine(2).equalsIgnoreCase("third")) {
                if (topKillers.keySet().toArray().length < 3 || topKillers.keySet().toArray()[2] == null) return;

                int topKill = topKillers.get(topKillers.keySet().toArray()[2]);
                String topKiller = (String) topKillers.keySet().toArray()[2];
                sign.setLine(0, colorize("&b&l" + topKiller));
                sign.setLine(1, colorize("&7" + topKill + " Kills"));
                sign.setLine(2, "");
                sign.setLine(3, colorize("&7#3"));
                sign.update();

                Block blockBehindSign = getBlockBehindSign(sign);
                if (blockBehindSign != null) {
                    Block blockSkull = blockBehindSign.getRelative(BlockFace.UP);
                    if (blockSkull.getType().equals(Material.SKULL)) {
                        Skull skull = (Skull) blockSkull.getState();
                        skull.setOwner(topKiller);
                        skull.setSkullType(SkullType.PLAYER);
                        skull.update();
                    }
                }

                topThreeKiller = sign;
                Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
                PickMcFFA.getDataFile().getConfig().set("ldh.kills.three", gson.toJson(sign.getLocation(), Location.class));
                PickMcFFA.getDataFile().saveConfig();
            }

        }
    }
}

package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import org.jetbrains.annotations.NotNull;

import static me.zxoir.pickmcffa.utils.Utils.colorize;

/**
 * MIT License Copyright (c) 2022 Zxoir
 *
 * @author Zxoir
 * @since 3/23/2022
 */
public class ConfigManager {
    private static final PickMcFFA main = PickMcFFA.getInstance();

    @Getter
    private static boolean debug;

    @Getter
    private static String username;

    @Getter
    private static String database;

    @Getter
    private static String password;

    @Getter
    private static String ip;

    @Getter
    private static String port;

    @Getter
    private static String ShopVillagerName;

    @Getter
    private static String InvalidPermission;

    @Getter
    private static int ExplosionChance;

    @Getter
    private static String ExplosionActivated;

    @Getter
    private static String ExplosionActivatedActionbar;

    private static String ExplosionDamage;

    private static String ExplosionDamageActionbar;

    @Getter
    private static int AbsorptionChance;

    @Getter
    private static int AbsorptionDuration;

    @Getter
    private static String AbsorptionActivated;

    @Getter
    private static String AbsorptionActivatedActionbar;

    @Getter
    private static int SpeedChance;

    @Getter
    private static int SpeedDuration;

    @Getter
    private static String SpeedActivated;

    @Getter
    private static String SpeedActivatedActionbar;

    @Getter
    private static String FailedProfileSave;

    @Getter
    private static String SameKitError;

    @Getter
    private static String NoKitError;

    @Getter
    private static String NoKitAccessError;

    @Getter
    private static String KitOutsideSpawnError;

    private static String killedMessage;

    private static String killMessage;

    private static String killedActionbar;

    private static String killActionbar;

    @Getter
    private static boolean combatCountdown;

    private static String combatMessage;

    private static String combatActionbar;

    private static String combatlogKillMessage;

    private static String combatlogFinishMessage;

    @Getter
    private static int combatTime;

    private static String levelUpTitle;

    private static String levelUpSubtitle;

    private static String levelUpBroadcast;

    private static void getConfigData() {
        debug = main.getConfig().getBoolean("debug");
        username = main.getConfig().getString("username");
        database = main.getConfig().getString("database");
        password = main.getConfig().getString("password");
        ip = main.getConfig().getString("ip");
        port = main.getConfig().getString("port");
        ShopVillagerName = colorize(main.getConfig().getString("ShopVillagerName"));
        InvalidPermission = colorize(main.getConfig().getString("InvalidPermission"));
        ExplosionChance = main.getConfig().getInt("ExplosionChance");
        ExplosionActivated = colorize(main.getConfig().getString("ExplosionActivated"));
        ExplosionActivatedActionbar = colorize(main.getConfig().getString("ExplosionActivatedActionbar"));
        ExplosionDamage = colorize(main.getConfig().getString("ExplosionDamage"));
        ExplosionDamageActionbar = colorize(main.getConfig().getString("ExplosionDamageActionbar"));
        AbsorptionChance = main.getConfig().getInt("AbsorptionChance");
        AbsorptionDuration = main.getConfig().getInt("AbsorptionDuration");
        AbsorptionActivated = colorize(main.getConfig().getString("AbsorptionActivated"));
        AbsorptionActivatedActionbar = colorize(main.getConfig().getString("AbsorptionActivatedActionbar"));
        SpeedChance = main.getConfig().getInt("SpeedChance");
        SpeedDuration = main.getConfig().getInt("SpeedDuration");
        SpeedActivated = colorize(main.getConfig().getString("SpeedActivated"));
        SpeedActivatedActionbar = colorize(main.getConfig().getString("SpeedActivatedActionbar"));
        FailedProfileSave = colorize(main.getConfig().getString("FailedProfileSave"));
        SameKitError = colorize(main.getConfig().getString("SameKitError"));
        NoKitError = colorize(main.getConfig().getString("NoKitError"));
        NoKitAccessError = colorize(main.getConfig().getString("NoKitAccessError"));
        KitOutsideSpawnError = colorize(main.getConfig().getString("KitOutsideSpawnError"));
        killedMessage = colorize(main.getConfig().getString("killed_message"));
        killMessage = colorize(main.getConfig().getString("kill_message"));
        killedActionbar = colorize(main.getConfig().getString("killed_actionbar"));
        killActionbar = colorize(main.getConfig().getString("kill_actionbar"));
        combatCountdown = main.getConfig().getBoolean("combat_countdown");
        combatMessage = colorize(main.getConfig().getString("combat_message"));
        combatActionbar = colorize(main.getConfig().getString("combat_actionbar"));
        combatlogKillMessage = colorize(main.getConfig().getString("combatlog_kill_message"));
        combatlogFinishMessage = colorize(main.getConfig().getString("combatlog_finish_message"));
        combatTime = main.getConfig().getInt("combat_time");
        levelUpTitle = colorize(main.getConfig().getString("levelUp_title"));
        levelUpSubtitle = colorize(main.getConfig().getString("levelUp_subtitle"));
        levelUpBroadcast = colorize(main.getConfig().getString("levelUp_broadcast"));
    }

    public static void setup() {
        main.saveDefaultConfig();
        getConfigData();
    }

    public static void reloadConfig() {
        main.reloadConfig();
        PickMcFFA.getDataFile().reloadConfig();
        getConfigData();
    }

    @NotNull
    public static String getExplosionDamage(String player) {
        return ExplosionDamage.replace("%player%", player);
    }

    @NotNull
    public static String getExplosionDamageActionbar(String player) {
        return ExplosionDamageActionbar.replace("%player%", player);
    }

    @NotNull
    public static String getKilledMessage(String killer, String killed, int pointsRemoved) {
        return killedMessage.replace("%killer%", killer).replace("%killed%", killed).replace("%points_removed%", String.valueOf(pointsRemoved));
    }

    @NotNull
    public static String getKillMessage(String killer, String killed, int pointsGained, int xpGained) {
        return killMessage.replace("%killer%", killer).replace("%killed%", killed).replace("%points_gained%", String.valueOf(pointsGained)).replace("%xp_gained%", String.valueOf(xpGained));
    }

    @NotNull
    public static String getKilledActionbar(String killer, String killed, int pointsRemoved) {
        return killedActionbar.replace("%killer%", killer).replace("%killed%", killed).replace("%points_removed%", String.valueOf(pointsRemoved));
    }

    @NotNull
    public static String getKillActionbar(String killer, String killed, int pointsGained, int xpGained) {
        return killActionbar.replace("%killer%", killer).replace("%killed%", killed).replace("%points_gained%", String.valueOf(pointsGained)).replace("%xp_gained%", String.valueOf(xpGained));
    }

    @NotNull
    public static String getCombatMessage(String playerHit) {
        return combatMessage.replace("%playerhit%", playerHit).replace("%time%", String.valueOf(combatTime));
    }

    @NotNull
    public static String getCombatActionbar(String playerHit) {
        return combatActionbar.replace("%playerhit%", playerHit).replace("%time%", String.valueOf(combatTime));
    }

    @NotNull
    public static String getCombatActionbar(String playerHit, long time) {
        return combatActionbar.replace("%playerhit%", playerHit).replace("%time%", String.valueOf(time));
    }

    @NotNull
    public static String getCombatlogKillMessage(String playerHit) {
        return combatlogKillMessage.replace("%playerhit%", playerHit).replace("%time%", String.valueOf(combatTime));
    }

    @NotNull
    public static String getCombatlogFinishMessage(String playerHit) {
        return combatlogFinishMessage.replace("%playerhit%", playerHit).replace("%time%", String.valueOf(combatTime));
    }

    @NotNull
    public static String getLevelUpTitle(String playerName, int newLevel, int oldLevel) {
        return levelUpTitle.replace("%player%", playerName).replace("%new_level%", String.valueOf(newLevel)).replace("%old_level%", String.valueOf(oldLevel));
    }

    @NotNull
    public static String getLevelUpSubtitle(String playerName, int newLevel, int oldLevel) {
        return levelUpSubtitle.replace("%player%", playerName).replace("%new_level%", String.valueOf(newLevel)).replace("%old_level%", String.valueOf(oldLevel));
    }

    @NotNull
    public static String getLevelUpBroadcast(String playerName, int newLevel, int oldLevel) {
        return levelUpBroadcast.replace("%player%", playerName).replace("%new_level%", String.valueOf(newLevel)).replace("%old_level%", String.valueOf(oldLevel));
    }
}
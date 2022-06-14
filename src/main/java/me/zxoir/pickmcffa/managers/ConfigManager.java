package me.zxoir.pickmcffa.managers;

import lombok.Getter;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.menus.*;
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

    private static String KitPurchaseMessage;

    private static String PerkPurchaseMessage;

    @Getter
    private static int SpeedKitPrice;

    @Getter
    private static long SpeedKitExpire;

    @Getter
    private static int SpeedKitLevel;

    @Getter
    private static int SpeedKitEffectDuration;

    @Getter
    private static int StrengthKitPrice;

    @Getter
    private static long StrengthKitExpire;

    @Getter
    private static int StrengthKitLevel;

    @Getter
    private static int TankKitPrice;

    @Getter
    private static long TankKitExpire;

    @Getter
    private static int TankKitLevel;

    @Getter
    private static int ExplosionRadius;

    @Getter
    private static int ExplosionPrice;

    @Getter
    private static long ExplosionExpire;

    @Getter
    private static int ExplosionLevel;

    @Getter
    private static int ExplosionChance;

    @Getter
    private static String ExplosionActivated;

    @Getter
    private static String ExplosionActivatedActionbar;

    private static String ExplosionDamage;

    private static String ExplosionDamageActionbar;

    @Getter
    private static int AbsorptionPrice;

    @Getter
    private static long AbsorptionExpire;

    @Getter
    private static int AbsorptionLevel;

    @Getter
    private static int AbsorptionChance;

    @Getter
    private static int AbsorptionDuration;

    @Getter
    private static String AbsorptionActivated;

    @Getter
    private static String AbsorptionActivatedActionbar;

    @Getter
    private static int SpeedPrice;

    @Getter
    private static long SpeedExpire;

    @Getter
    private static int SpeedLevel;

    @Getter
    private static int SpeedChance;

    @Getter
    private static int SpeedDuration;

    @Getter
    private static String SpeedActivated;

    @Getter
    private static String SpeedActivatedActionbar;

    private static String KitExpireMessage;

    private static String PerkExpireMessage;

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

    @Getter
    private static String SamePerkError;

    @Getter
    private static String NoPerkError;

    @Getter
    private static String NoPerkAccessError;

    @Getter
    private static String PerkOutsideSpawnError;

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
        KitPurchaseMessage = colorize(main.getConfig().getString("KitPurchaseMessage"));
        PerkPurchaseMessage = colorize(main.getConfig().getString("PerkPurchaseMessage"));
        SpeedKitPrice = main.getConfig().getInt("SpeedKitPrice");
        SpeedKitExpire = main.getConfig().getLong("SpeedKitExpire");
        SpeedKitLevel = main.getConfig().getInt("SpeedKitLevel");
        SpeedKitEffectDuration = main.getConfig().getInt("SpeedKitEffectDuration");
        StrengthKitPrice = main.getConfig().getInt("StrengthKitPrice");
        StrengthKitExpire = main.getConfig().getLong("StrengthKitExpire");
        StrengthKitLevel = main.getConfig().getInt("StrengthKitLevel");
        TankKitPrice = main.getConfig().getInt("TankKitPrice");
        TankKitExpire = main.getConfig().getLong("TankKitExpire");
        TankKitLevel = main.getConfig().getInt("TankKitLevel");
        ExplosionRadius = main.getConfig().getInt("ExplosionRadius");
        ExplosionPrice = main.getConfig().getInt("ExplosionPrice");
        ExplosionExpire = main.getConfig().getLong("ExplosionExpire");
        ExplosionLevel = main.getConfig().getInt("ExplosionLevel");
        ExplosionChance = main.getConfig().getInt("ExplosionChance");
        ExplosionActivated = colorize(main.getConfig().getString("ExplosionActivated"));
        ExplosionActivatedActionbar = colorize(main.getConfig().getString("ExplosionActivatedActionbar"));
        ExplosionDamage = colorize(main.getConfig().getString("ExplosionDamage"));
        ExplosionDamageActionbar = colorize(main.getConfig().getString("ExplosionDamageActionbar"));
        AbsorptionPrice = main.getConfig().getInt("AbsorptionPrice");
        AbsorptionExpire = main.getConfig().getLong("AbsorptionExpire");
        AbsorptionLevel = main.getConfig().getInt("AbsorptionLevel");
        AbsorptionChance = main.getConfig().getInt("AbsorptionChance");
        AbsorptionDuration = main.getConfig().getInt("AbsorptionDuration");
        AbsorptionActivated = colorize(main.getConfig().getString("AbsorptionActivated"));
        AbsorptionActivatedActionbar = colorize(main.getConfig().getString("AbsorptionActivatedActionbar"));
        SpeedPrice = main.getConfig().getInt("SpeedPrice");
        SpeedExpire = main.getConfig().getLong("SpeedExpire");
        SpeedLevel = main.getConfig().getInt("SpeedLevel");
        SpeedChance = main.getConfig().getInt("SpeedChance");
        SpeedDuration = main.getConfig().getInt("SpeedDuration");
        SpeedActivated = colorize(main.getConfig().getString("SpeedActivated"));
        SpeedActivatedActionbar = colorize(main.getConfig().getString("SpeedActivatedActionbar"));
        KitExpireMessage = colorize(main.getConfig().getString("KitExpireMessage"));
        PerkExpireMessage = colorize(main.getConfig().getString("PerkExpireMessage"));
        FailedProfileSave = colorize(main.getConfig().getString("FailedProfileSave"));
        SameKitError = colorize(main.getConfig().getString("SameKitError"));
        NoKitError = colorize(main.getConfig().getString("NoKitError"));
        NoKitAccessError = colorize(main.getConfig().getString("NoKitAccessError"));
        KitOutsideSpawnError = colorize(main.getConfig().getString("KitOutsideSpawnError"));
        SamePerkError = colorize(main.getConfig().getString("SamePerkError"));
        NoPerkError = colorize(main.getConfig().getString("NoPerkError"));
        NoPerkAccessError = colorize(main.getConfig().getString("NoPerkAccessError"));
        PerkOutsideSpawnError = colorize(main.getConfig().getString("PerkOutsideSpawnError"));
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
        KitManager.reloadKits();
        PerkManager.reloadPerks();
        KitMenu.loadMenu();
        ShopMenu.loadMenu();
        TempKitShopMenu.loadMenu();
        KitPurchaseConfirmationMenu.loadMenu();
        PerkShopMenu.loadMenu();
        PerkPurchaseConfirmationMenu.loadMenu();
        PerkMenu.loadMenu();
    }

    @NotNull
    public static String getKitPurchaseMessage(String kitName, int kitPrice) {
        return KitPurchaseMessage.replace("%kit_name%", kitName).replace("%kit_price%", String.valueOf(kitPrice));
    }

    @NotNull
    public static String getPerkPurchaseMessage(String perkName, int perkPrice) {
        return PerkPurchaseMessage.replace("%perk_name%", perkName).replace("%perk_price%", String.valueOf(perkPrice));
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
    public static String getKitExpireMessage(String kitName) {
        return KitExpireMessage.replace("%kit_name%", kitName);
    }

    @NotNull
    public static String getPerkExpireMessage(String perkName) {
        return PerkExpireMessage.replace("%perk_name%", perkName);
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
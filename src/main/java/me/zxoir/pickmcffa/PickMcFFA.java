package me.zxoir.pickmcffa;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import lombok.Getter;
import me.zxoir.pickmcffa.commands.*;
import me.zxoir.pickmcffa.commands.topstats.TopDeathsCommand;
import me.zxoir.pickmcffa.commands.topstats.TopKillsCommand;
import me.zxoir.pickmcffa.commands.topstats.TopKillstreak;
import me.zxoir.pickmcffa.commands.topstats.TopLevelCommand;
import me.zxoir.pickmcffa.customclasses.EntityNPC;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.database.DataFile;
import me.zxoir.pickmcffa.database.FFADatabase;
import me.zxoir.pickmcffa.database.UsersDBManager;
import me.zxoir.pickmcffa.listener.*;
import me.zxoir.pickmcffa.listener.eventsListeners.LastManStandingEventListener;
import me.zxoir.pickmcffa.listener.eventsListeners.SnowBallEventListener;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.managers.EventsManager;
import me.zxoir.pickmcffa.managers.StatsManager;
import me.zxoir.pickmcffa.menus.*;
import net.minecraft.server.v1_8_R3.EntityVillager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.runAsync;

public final class PickMcFFA extends JavaPlugin {
    @Getter
    private static final Logger ffaLogger = LogManager.getLogger("PickMc FFA");
    @Getter
    private static PickMcFFA instance;
    @Getter
    private static DataFile dataFile;
    @Getter
    private static Cache<UUID, User> cachedUsers;
    @Getter
    private static WorldEditPlugin worldEditPlugin;

    @Override
    public void onEnable() {
        ffaLogger.info("======================================================================");
        long startTime = System.currentTimeMillis();
        ffaLogger.info("Initializing plugin setup...");

        instance = this;
        ConfigManager.setup();
        dataFile = new DataFile();
        dataFile.setup();
        cachedUsers = CacheBuilder.newBuilder().build();

        ffaLogger.info("Initializing database setup...");
        FFADatabase.createTable("CREATE TABLE IF NOT EXISTS users(" +
                "uuid VARCHAR(36) PRIMARY KEY NOT NULL," +
                "stats LONGTEXT," +
                "selectedPerk VARCHAR(36)," +
                "savedInventories TEXT," +
                "firstJoinDate TEXT" +
                ");");

        ffaLogger.info("Database loaded successfully. Took " + (System.currentTimeMillis() - startTime) + "ms");
        long start = System.currentTimeMillis();

        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin instanceof WorldEditPlugin)
            worldEditPlugin = (WorldEditPlugin) plugin;

        ffaLogger.info("Caching users...");
        loadCachedUsers();
        ffaLogger.info("Cached users successfully. Took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        ffaLogger.info("Starting events timer...");
        EventsManager.setScheduledAnnounceTask(EventsManager.getScheduledAnnounceTask());
        ffaLogger.info("Started events timer successfully. Took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        ffaLogger.info("Loading saved pending vote rewards...");
        loadSavedPendingVoteRewards();
        ffaLogger.info("Loaded saved pending vote rewards successfully. Took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        ffaLogger.info("Loading saved barrier wall selection...");
        loadSavedBarrierWall();
        ffaLogger.info("Loaded saved barrier wall selection successfully. Took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        ffaLogger.info("Loading saved teleport point...");
        loadTeleportPoint();
        ffaLogger.info("Loaded saved teleport point successfully. Took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        ffaLogger.info("Caching stats...");
        StatsManager.cacheStats();
        ffaLogger.info("Cached stats successfully. Took " + (System.currentTimeMillis() - start) + "ms");

        if (SpawnShopCommand.getShopNPC() == null) {
            start = System.currentTimeMillis();
            ffaLogger.info("Registering NPC...");
            EntityNPC npc = new EntityNPC(((CraftWorld) Bukkit.getWorld("world")).getHandle());
            npc.registerEntity(ConfigManager.getShopVillagerName(), 120, EntityVillager.class, EntityNPC.class);
            ffaLogger.info("Registered NPC successfully. Took " + (System.currentTimeMillis() - start) + "ms");
        }

        start = System.currentTimeMillis();
        ffaLogger.info("Registering Listeners...");
        registerEvents();
        ffaLogger.info("Listeners registered successfully. Took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        ffaLogger.info("Registering Commands...");
        registerCommands();
        ffaLogger.info("Commands registered successfully. Took " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        ffaLogger.info("Loading menus...");
        KitMenu.loadMenu();
        ShopMenu.loadMenu();
        TempKitShopMenu.loadMenu();
        KitPurchaseConfirmationMenu.loadMenu();
        PerkShopMenu.loadMenu();
        PerkPurchaseConfirmationMenu.loadMenu();
        PerkMenu.loadMenu();
        ffaLogger.info("Menus loaded. Took " + (System.currentTimeMillis() - start) + "ms");

        ffaLogger.info("Plugin loaded and initialized successfully. Took " + (System.currentTimeMillis() - startTime) + "ms");
        ffaLogger.info("======================================================================");

        // Auto save users every 10 minutes
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> cachedUsers.asMap().forEach((uuid, user) -> user.save()), 0, 12000);
    }

    private void loadSavedBarrierWall() {
        if (getDataFile().getConfig().getString("BarrierWall.world") == null)
            return;

        World world = Bukkit.getWorld(getDataFile().getConfig().getString("BarrierWall.world"));
        double minX = getDataFile().getConfig().getDouble("BarrierWall.minX");
        double minY = getDataFile().getConfig().getDouble("BarrierWall.minY");
        double minZ = getDataFile().getConfig().getDouble("BarrierWall.minZ");

        double maxX = getDataFile().getConfig().getDouble("BarrierWall.maxX");
        double maxY = getDataFile().getConfig().getDouble("BarrierWall.maxY");
        double maxZ = getDataFile().getConfig().getDouble("BarrierWall.maxZ");


        Location minimumPoint = new Location(world, minX, minY, minZ);
        Location maximumPoint = new Location(world, maxX, maxY, maxZ);

        EventsManager.setBarrierWall(new CuboidSelection(world, minimumPoint, maximumPoint));
    }

    private void loadTeleportPoint() {
        if (getDataFile().getConfig().getString("Teleport.world") == null)
            return;

        World world = Bukkit.getWorld(getDataFile().getConfig().getString("Teleport.world"));
        double x = getDataFile().getConfig().getDouble("Teleport.x");
        double y = getDataFile().getConfig().getDouble("Teleport.y");
        double z = getDataFile().getConfig().getDouble("Teleport.z");
        float pitch = (float) getDataFile().getConfig().getDouble("Teleport.pitch");
        float yaw = (float) getDataFile().getConfig().getDouble("Teleport.yaw");

        Location teleportPoint = new Location(world, x, y, z, yaw, pitch);

        EventsManager.setTeleportPoint(teleportPoint);
    }

    @Override
    public void onDisable() {
        savePendingVoteRewards();

        if (SpawnShopCommand.getShopNPC() != null)
            SpawnShopCommand.getShopNPC().remove();

        if (cachedUsers != null && cachedUsers.size() >= 1) {
            try {
                runAsync(() -> cachedUsers.asMap().forEach((uuid, user) -> {
                    user.save();
                })).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        Collection<Hologram> holograms = HologramsAPI.getHolograms(this);
        if (holograms.isEmpty()) return;
        holograms.forEach(Hologram::delete);

        FFADatabase.getDataSource().close();
        instance = null;
    }

    private void savePendingVoteRewards() {
        if (VoteListener.getPendingVoteReward().isEmpty())
            return;

        for (UUID uuid : VoteListener.getPendingVoteReward().keySet()) {
            dataFile.getConfig().set("voteReward." + uuid.toString(), VoteListener.getPendingVoteReward().get(uuid));
        }
        dataFile.saveConfig();
    }

    private void loadSavedPendingVoteRewards() {
        if (dataFile.getConfig().getString("voteReward") == null)
            return;

        for (String uuid : dataFile.getConfig().getConfigurationSection("voteReward").getKeys(true)) {
            VoteListener.getPendingVoteReward().put(UUID.fromString(uuid), dataFile.getConfig().getInt("voteReward." + uuid));
        }

        dataFile.getConfig().set("voteReward", null);
        dataFile.saveConfig();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        getServer().getPluginManager().registerEvents(new KillActionListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new KitMenu(), this);
        getServer().getPluginManager().registerEvents(new LaunchPad(), this);
        getServer().getPluginManager().registerEvents(new SnowballDelay(), this);
        getServer().getPluginManager().registerEvents(new CombatLogListener(), this);
        getServer().getPluginManager().registerEvents(new ShopVillager(), this);
        getServer().getPluginManager().registerEvents(new PerkListener(), this);
        getServer().getPluginManager().registerEvents(new ShopMenu(), this);
        getServer().getPluginManager().registerEvents(new TempKitShopMenu(), this);
        getServer().getPluginManager().registerEvents(new KitPurchaseConfirmationMenu(), this);
        getServer().getPluginManager().registerEvents(new PerkShopMenu(), this);
        getServer().getPluginManager().registerEvents(new PerkPurchaseConfirmationMenu(), this);
        getServer().getPluginManager().registerEvents(new PerkMenu(), this);
        getServer().getPluginManager().registerEvents(new KitCheckerListener(), this);
        getServer().getPluginManager().registerEvents(new PerkCheckerListener(), this);
        getServer().getPluginManager().registerEvents(new KillStreakListener(), this);
        getServer().getPluginManager().registerEvents(new KitInventoryListener(), this);
        getServer().getPluginManager().registerEvents(new LeaderboardHeads(), this);
        getServer().getPluginManager().registerEvents(new StatsHologram(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(), this);
        getServer().getPluginManager().registerEvents(new TempFireListener(), this);
        getServer().getPluginManager().registerEvents(new VoteListener(), this);
        getServer().getPluginManager().registerEvents(new SnowBallEventListener(), this);
        getServer().getPluginManager().registerEvents(new EventsListener(), this);
        getServer().getPluginManager().registerEvents(new LastManStandingEventListener(), this);
    }

    private void registerCommands() {
        getCommand("kit").setExecutor(new KitCommand());
        getCommand("spawnshop").setExecutor(new SpawnShopCommand());
        getCommand("ffa").setExecutor(new MainCommand());
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("perk").setExecutor(new PerkCommand());
        getCommand("topkills").setExecutor(new TopKillsCommand());
        getCommand("topdeaths").setExecutor(new TopDeathsCommand());
        getCommand("topkillstreak").setExecutor(new TopKillstreak());
        getCommand("toplevel").setExecutor(new TopLevelCommand());
        getCommand("events").setExecutor(new EventCommand());
    }

    private void loadCachedUsers() {
        cachedUsers.invalidateAll();
        ConcurrentLinkedQueue<User> users = UsersDBManager.getUsers();
        users.forEach(user -> cachedUsers.put(user.getUuid(), user));
    }
}

package me.zxoir.pickmcffa;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import me.zxoir.pickmcffa.commands.KitCommand;
import me.zxoir.pickmcffa.commands.MainCommand;
import me.zxoir.pickmcffa.commands.SpawnShopCommand;
import me.zxoir.pickmcffa.commands.StatsCommand;
import me.zxoir.pickmcffa.customclasses.EntityNPC;
import me.zxoir.pickmcffa.customclasses.User;
import me.zxoir.pickmcffa.database.DataFile;
import me.zxoir.pickmcffa.database.FFADatabase;
import me.zxoir.pickmcffa.database.UsersDBManager;
import me.zxoir.pickmcffa.listener.*;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.menus.*;
import net.minecraft.server.v1_8_R3.EntityVillager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class PickMcFFA extends JavaPlugin {
    @Getter
    private static final Logger ffaLogger = LogManager.getLogger("PickMc FFA");
    @Getter
    private static PickMcFFA instance;
    @Getter
    private static DataFile dataFile;
    @Getter
    private static Cache<UUID, User> cachedUsers;

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
                "stats TEXT" +
                ");");

        /* FFADatabase.createTable("CREATE TABLE IF NOT EXISTS kits(" +
                "name VARCHAR(36) PRIMARY KEY NOT NULL," +
                "description VARCHAR(36) NOT NULL," +
                "price DECIMAL," +
                "expireTime VARCHAR(36)," +
                "items TEXT NOT NULL," +
                "armour TEXT NOT NULL," +
                "icon TEXT NOT NULL," +
                "permissions TEXT," +
                "permanentPotions TEXT" +
                ");");*/

        ffaLogger.info("Database loaded successfully. Took " + (System.currentTimeMillis() - startTime) + "ms");
        long start = System.currentTimeMillis();

        ffaLogger.info("Caching users...");
        loadCachedUsers();
        ffaLogger.info("Cached users successfully. Took " + (System.currentTimeMillis() - start) + "ms");

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
        ffaLogger.info("Menus loaded. Took " + (System.currentTimeMillis() - start) + "ms");

        ffaLogger.info("Plugin loaded and initialized successfully. Took " + (System.currentTimeMillis() - startTime) + "ms");
        ffaLogger.info("======================================================================");

        // Auto save users every 10 minutes
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> cachedUsers.asMap().forEach((uuid, user) -> user.save()), 0, 12000);
    }

    @Override
    public void onDisable() {

        FFADatabase.getDataSource().close();

        if (SpawnShopCommand.getShopNPC() != null)
            SpawnShopCommand.getShopNPC().remove();

        if (cachedUsers != null && cachedUsers.size() >= 1)
            cachedUsers.asMap().forEach((uuid, user) -> {
                user.setSelectedKit(null);
                user.save();
            });
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
    }

    private void registerCommands() {
        getCommand("kit").setExecutor(new KitCommand());
        getCommand("spawnshop").setExecutor(new SpawnShopCommand());
        getCommand("pickmc").setExecutor(new MainCommand());
        getCommand("stats").setExecutor(new StatsCommand());
    }

    private void loadCachedUsers() {
        cachedUsers.invalidateAll();
        ConcurrentLinkedQueue<User> users = UsersDBManager.getUsers();
        users.forEach(user -> cachedUsers.put(user.getUuid(), user));
    }
}

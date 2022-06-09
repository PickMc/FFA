package me.zxoir.pickmcffa.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.zxoir.pickmcffa.PickMcFFA;
import me.zxoir.pickmcffa.commands.SpawnShopCommand;
import me.zxoir.pickmcffa.customclasses.EntityNPC;
import me.zxoir.pickmcffa.managers.ConfigManager;
import me.zxoir.pickmcffa.utils.LocationAdapter;
import net.minecraft.server.v1_8_R3.EntityVillager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.io.File;
import java.io.IOException;

public class DataFile {
    public final PickMcFFA plugin = PickMcFFA.getInstance();
    public FileConfiguration playerscfg;
    public File playersfile;

    // Sets up the configuration yml
    public void setup() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }

        File dataFile = new File(this.plugin.getDataFolder() + File.separator + "Data" + File.separator);
        this.playersfile = new File(dataFile.getPath(), "DataFile.yml");

        if (!dataFile.exists()) {
            try {
                dataFile.mkdirs();
                this.plugin.getServer().getConsoleSender().sendMessage("the Data folder has been created!");
            } catch (SecurityException e) {
                this.plugin.getServer().getConsoleSender().sendMessage("Could not create the Data folder");
            }
        }

        if (!this.playersfile.exists()) {
            try {
                this.playersfile.createNewFile();
                this.plugin.getServer().getConsoleSender().sendMessage("the DataFile.yml file has been created!");
            } catch (IOException e) {
                this.plugin.getServer().getConsoleSender().sendMessage("Could not create the DataFile.yml file");
            }
        }

        this.playerscfg = YamlConfiguration.loadConfiguration(this.playersfile);
        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationAdapter()).serializeNulls().create();
        /*if (getConfig().getString("holo") != null)
            StatsCommand.setStatsLocation(gson.fromJson(getConfig().getString("holo"), Location.class));
        if (getConfig().getString("ldh.one") != null) {
            Location loc = gson.fromJson(getConfig().getString("ldh.one"), Location.class);
            Block block = loc.getWorld().getBlockAt(loc);
            if (block != null && block.getType().equals(Material.WALL_SIGN)) {
                LeaderboardHeads.setTopOneKiller((Sign) block.getState());
            }
        }
        if (getConfig().getString("ldh.two") != null) {
            Location loc = gson.fromJson(getConfig().getString("ldh.two"), Location.class);
            Block block = loc.getWorld().getBlockAt(loc);
            if (block != null && block.getType().equals(Material.WALL_SIGN)) {
                LeaderboardHeads.setTopTwoKiller((Sign) block.getState());
            }
        }
        if (getConfig().getString("ldh.three") != null) {
            Location loc = gson.fromJson(getConfig().getString("ldh.three"), Location.class);
            Block block = loc.getWorld().getBlockAt(loc);
            if (block != null && block.getType().equals(Material.WALL_SIGN)) {
                LeaderboardHeads.setTopThreeKiller((Sign) block.getState());
            }
        }*/

        if (getConfig().getString("shop") != null) {
            Location loc = gson.fromJson(getConfig().getString("shop"), Location.class);
            EntityNPC npc = new EntityNPC(((CraftWorld) loc.getWorld()).getHandle());
            npc.registerEntity(ConfigManager.getShopVillagerName(), 120, EntityVillager.class, EntityNPC.class);
            SpawnShopCommand.setShopNPC(EntityNPC.spawn(loc, ConfigManager.getShopVillagerName()));
        }
    }

    // Gets the yml configuration
    public FileConfiguration getConfig() {
        return this.playerscfg;
    }

    // Saves the yml configuration
    public void saveConfig() {
        try {
            this.playerscfg.save(this.playersfile);
        } catch (IOException localIOException) {
            this.plugin.getServer().getConsoleSender().sendMessage("Could not save the DataFile.yml file");
        }
    }

    // Reloads the yml configuration
    public void reloadConfig() {
        this.playerscfg = YamlConfiguration.loadConfiguration(this.playersfile);
    }
}

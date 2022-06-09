package me.zxoir.pickmcffa.customclasses;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityNPC extends EntityVillager {

    public EntityNPC(World world) {
        super(world);

        try {
            Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
            bField.setAccessible(true);
            Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
            cField.setAccessible(true);

            bField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            bField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(goalSelector, new UnsafeList<PathfinderGoalSelector>());
            cField.set(targetSelector, new UnsafeList<PathfinderGoalSelector>());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        //this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
        //this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 0D));
        //his.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 0F, 0));
        //this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityVillager.class, 5.0F, 0.02F));
        //this.goalSelector.a(9, new PathfinderGoalRandomStroll(this, 0D));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 0, 0));
    }

    @Override
    public void move(double d0, double d1, double d2) {

    }

    @Override
    public void collide(Entity entity) {
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    @Override
    public void g(double d0, double d1, double d2) {
    }

    public static Villager spawn(@NotNull Location loc, String customName){
        World mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
        final EntityNPC customEnt = new EntityNPC(mcWorld);
        customEnt.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        customEnt.setInvisible(true);
        customEnt.setCustomName(customName);
        customEnt.setCustomNameVisible(true);
        Vector dirBetweenLocations = loc.toVector().subtract(loc.toVector());
        Location locs = customEnt.bukkitEntity.getLocation();
        loc.setDirection(dirBetweenLocations);
        customEnt.teleportTo(locs, false);

        ((CraftLivingEntity) customEnt.getBukkitEntity()).setRemoveWhenFarAway(false);
        mcWorld.addEntity(customEnt, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (Villager) customEnt.getBukkitEntity();
    }

    public void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass, Class<? extends EntityInsentient> customClass){
        try {

            List<Map<?, ?>> dataMap = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()){
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())){
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }

            if (dataMap.get(2).containsKey(id)){
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }

            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

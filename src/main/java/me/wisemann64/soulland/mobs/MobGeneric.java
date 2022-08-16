package me.wisemann64.soulland.mobs;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.players.Stats;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class MobGeneric extends SLMob {

    public static class Customizer {

        MobGenericTypes type;
        String name = "Mob";
        int level = 0;
        int xp = 0;
        double explosionPower = 0;
        final List<String> drops = new ArrayList<>();
        final EnumMap<Stats,Double> initStats = new EnumMap<>(Stats.class);

        public Customizer(MobGenericTypes handlerEnum) {
            type = handlerEnum;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setExplosionPower(double explosionPower) {
            this.explosionPower = explosionPower;
        }

        public List<String> getDrops() {
            return drops;
        }

        public MobGenericTypes getType() {
            return type;
        }

        public void setXp(int xp) {
            this.xp = xp;
        }

        public void setType(MobGenericTypes type) {
            this.type = type;
        }

        public EnumMap<Stats, Double> getInitStats() {
            return initStats;
        }
    }

    private final Customizer custom;

    public MobGeneric(World w, Customizer custom) {
        super(w, custom.name, custom.level, true);
        this.custom = custom;
        genericConst(w);
    }

    private void genericConst(World w) {
        createHandle(w);
        if (handle == null) {
            SoulLand.getMobManager().removeMobFromRegistry(mobId);
            return;
        }
        initAttribute();
        tick.runTaskTimer(SoulLand.getPlugin(),1L,1L);
    }

    @Override
    public EntityCreature getMobHandle() {
        return handle;
    }

    @Override
    public double getExplosionPower() {
        return custom.explosionPower;
    }

    @Override
    public int getXpYield() {
        return custom.xp;
    }

    @Override
    public void createHandle(World world) {
        WorldServer ws = ((CraftWorld)world).getHandle();
        Class<? extends EntityCreature> c = custom.type.getHandleClass();
        Constructor<?>[] constructors = c.getDeclaredConstructors();
        for (Constructor<?> co : constructors) {
            if (co.getParameterCount() != 2) continue;
            Class<?>[] pt = co.getParameterTypes();
            if (!pt[0].equals(net.minecraft.server.v1_16_R3.World.class)) continue;
            if (!pt[1].getSuperclass().equals(SLMob.class)) continue;
            try {
                handle = (EntityCreature) co.newInstance(ws,null);
            } catch (Exception ex) {
                System.err.println("Failed to create MobGeneric " + this);
                handle = null;
                return;
            }
            try {
                Field handler = handle.getClass().getDeclaredField("handler");
                handler.setAccessible(true);
                handler.set(handle,this);
            } catch (Exception ex) {
                System.err.println("Failed to replace handler field on MobGeneric " + this + "'s handle");
            }
            break;
        }
    }

    @Override
    public void initAttribute() {
        MobAttributes a = getAttributes();
        EnumMap<Stats, Double> init = custom.getInitStats();
        a.setMaxHealth(init.getOrDefault(Stats.HEALTH,20.0));
        a.setHealth(init.getOrDefault(Stats.HEALTH,20.0));
        a.setAttackPower(init.getOrDefault(Stats.ATK,0.0));
        a.setDefense(init.getOrDefault(Stats.DEF,0.0));
        a.setMagicAttack(init.getOrDefault(Stats.MATK,0.0));
        a.setMagicDefense(init.getOrDefault(Stats.MDEF,0.0));
        a.setMagicPEN(init.getOrDefault(Stats.MPEN,0.0));
        a.setPhysicalPEN(init.getOrDefault(Stats.PEN,0.0));
    }

    @Override
    public List<String> drops() {
        return custom.drops;
    }
}

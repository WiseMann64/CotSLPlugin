package me.wisemann64.soulland.mobs;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.combat.CombatEntity;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.wisemann64.soulland.Utils.color;

public abstract class SLMob implements CombatEntity {

    protected final CraftServer server = ((CraftServer)Bukkit.getServer());
    protected EntityLiving handle;
    protected final int mobId;
    protected org.bukkit.entity.Entity lastDamager = null;
    protected final MobAttributes attributes = new MobAttributes(this);
    private final EnumMap<EntityDamageEvent.DamageCause,Integer> envDamageCooldown = new EnumMap<>(EntityDamageEvent.DamageCause.class);
    private final Map<UUID,Integer> damageCooldown = new HashMap<>();
    private final int level;
    private final String mobName;
    private final String tag = "&8[&7Lv %level&8] &3%name %health&c❤";

    private boolean isInvul;

    private final BukkitRunnable tick = new BukkitRunnable() {
        @Override
        public void run() {
            tick();
        }
    };

    public void tick() {
        handle.getBukkitEntity().setCustomName(parseTag(tag));

        for (EntityDamageEvent.DamageCause v : envDamageCooldown.keySet()) {
            int cd = envDamageCooldown.get(v)-1;
            if (cd == 0) envDamageCooldown.remove(v);
            else envDamageCooldown.put(v,cd);
        }

        for (UUID v : damageCooldown.keySet()) {
            int cd = damageCooldown.get(v) - 1;
            if (cd == 0) damageCooldown.remove(v);
            else damageCooldown.put(v,cd);
        }
    }

    private String parseTag(String s) {
        if (s.contains("%health")) {
            String color = getHealthFraction() > 0.5 ? "&a" : getHealthFraction() > 0.2 ? "&e" : "&4";
            s = s.replace("%health", color + Math.round(attributes.getHealth()));
        }
        if (s.contains("%name")) s = s.replace("%name",mobName);
        if (s.contains("%level")) s = s.replace("%level",String.valueOf(level));
        return color(s);
    }

    public SLMob(World w, String name, int level) {
        this.level = level;
        mobId = SoulLand.getMobManager().putMobToRegistry(this);
        mobName = name;
        createHandle(w,name);
        initAttribute();
        tick.runTaskTimer(SoulLand.getPlugin(),1L,1L);
    }


    public Entity getHandle() {
        return handle.getBukkitEntity();
    }
    public abstract EntityLiving getSLHandler();
    public abstract double getExplosionPower();

    public MobAttributes getAttributes() {
        return attributes;
    }

    public void spawn(Location l) {
        if (handle == null) return;
        setLocation(handle,l);
        ((CraftWorld)l.getWorld()).getHandle().addEntity(handle);
    }

    public void remove() {
        try {
            tick.cancel();
        } catch (RuntimeException ignored) {

        }
        SoulLand.getMobManager().removeMobFromRegistry(mobId);
        if (getHandle() instanceof LivingEntity g) g.setHealth(0);
        else getHandle().remove();
    }

    public void quickRemove() {
        try {
            tick.cancel();
        } catch (RuntimeException ignored) {

        }
        lastDamager = null;
        getHandle().remove();
    }

    public static void setLocation(net.minecraft.server.v1_16_R3.Entity handle, Location loc) {
        handle.setLocation(loc.getX(),loc.getY(),loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public double dealDamage(double amount) {
        setHealth(getHealth()-amount);
        return amount;
    }

    public void die() {
        remove();
    }

    public void setNoAI(boolean val) {
        if (this.handle instanceof EntityInsentient g) g.setNoAI(val);
    }
    public Location getLocation() {
        return this.getHandle().getLocation();
    }
    public abstract void createHandle(World world, String name);
    public abstract void initAttribute();
    public void setInvul(boolean var) {
        isInvul = var;
    }
    public void fakeDamage() {
        handle.damageEntity(DamageSource.GENERIC,0.0F);
    }
    public CraftWorld getWorld() {
        return handle.getWorld().getWorld();
    }

    public boolean isInvis() {
        return isInvul;
    }

    public double getHealth() {
        return attributes.getHealth();
    }
    public double getMaxHealth() {
        return attributes.getMaxHealth();
    }
    public float getHealthFraction() {
        return (float) (attributes.getHealth()/ attributes.getMaxHealth());
    }
    public double getAttackPower() {
        return attributes.getAttackPower();
    }
    public double getMagicAttackPower() {
        return attributes.getMagicAttack();
    }
    public double getRangedAttackPower() {
        return getAttackPower();
    }
    public double getDefense() {
        return attributes.getDefense();
    }
    public double getMagicDefense() {
        return attributes.getMagicDefense();
    }
    public double getPhysicalPEN() {
        return attributes.getPhysicalPEN();
    }
    public double getMagicPEN() {
        return attributes.getMagicPEN();
    }
    public int getLevel() {
        return level;
    }
    public Location getEyeLocation() {
        if (handle.getBukkitEntity() instanceof LivingEntity li) return li.getEyeLocation();
        return getLocation();
    }
    public void setHealth(double health) {
        attributes.setHealth(health);
        if (health <= 0) {
            attributes.setHealth(0);
            handle.getBukkitEntity().setCustomName(parseTag(tag));
            die();
        }
    }
    public void heal(double amount) {
        attributes.setHealth(attributes.getHealth()+amount);
    }
    public void sendMessage(String msg) {

    }
    public EnumMap<EntityDamageEvent.DamageCause, Integer> getEnvDamageCooldown() {
        return envDamageCooldown;
    }

    public Map<UUID, Integer> getDamageCooldown() {
        return damageCooldown;
    }

    public String getMobName() {
        return mobName;
    }
}
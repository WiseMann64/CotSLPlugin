package me.wisemann64.soulland.system.mobs;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.combat.CombatEntity;
import me.wisemann64.soulland.system.players.SLPlayer;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityCreature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static me.wisemann64.soulland.system.util.Utils.color;

public abstract class SLMob implements CombatEntity {

    protected final CraftServer server = ((CraftServer)Bukkit.getServer());
    protected EntityCreature handle;
    protected final int mobId;
    protected org.bukkit.entity.Entity lastDamager = null;
    protected final MobAttributes attributes = new MobAttributes(this);
    private final EnumMap<EntityDamageEvent.DamageCause,Integer> envDamageCooldown = new EnumMap<>(EntityDamageEvent.DamageCause.class);
    private final Map<UUID,Integer> damageCooldown = new HashMap<>();
    private final int level;
    private final String mobName;
    private final String tag = "&8[&7Lv %level&8] &3%name %health&c❤";

    private boolean isInvul;

    final BukkitRunnable tick = new BukkitRunnable() {
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
        createHandle(w);
        initAttribute();
        tick.runTaskTimer(SoulLand.getPlugin(),1L,1L);
    }

    SLMob(World w, String name, int level, boolean delayCreateHandle) {
        this.level = level;
        mobId = SoulLand.getMobManager().putMobToRegistry(this);
        mobName = name;
        if (!delayCreateHandle) {
            createHandle(w);
            initAttribute();
            tick.runTaskTimer(SoulLand.getPlugin(), 1L, 1L);
        }
    }


    public Entity getHandle() {
        return handle.getBukkitEntity();
    }
    public abstract EntityCreature getMobHandle();
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
        if (lastDamager != null) {
            SLPlayer p = SoulLand.getPlayerManager().getPlayer(lastDamager.getUniqueId());
            if (p != null) {
                p.addXp(getXpYield());
            }
        }
        if (getHandle() instanceof LivingEntity g) g.setHealth(0);
        else getHandle().remove();
        SoulLand.getMobManager().removeMobFromRegistry(mobId);
    }

    public void quickRemove() {
        try {
            tick.cancel();
        } catch (RuntimeException ignored) {

        }
        lastDamager = null;
        getHandle().remove();
        SoulLand.getMobManager().removeMobFromRegistry(mobId);
    }

    public static void setLocation(net.minecraft.server.v1_16_R3.Entity handle, Location loc) {
        handle.setLocation(loc.getX(),loc.getY(),loc.getZ(), loc.getYaw(), loc.getPitch());
    }

    public double dealDamage(double amount, CombatEntity damager) {
        if (damager != null) lastDamager = damager.getHandle();
        setHealth(getHealth()-amount);
        return amount;
    }

    public void die() {
        remove();
    }

    public void setNoAI(boolean val) {
        handle.setNoAI(val);
    }
    public Location getLocation() {
        return this.getHandle().getLocation();
    }
    public abstract void createHandle(World world);
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
    public abstract List<String> drops();
    /*
    Chance:ITEM:Count
    1:BOW:1
    0.74:FLESH:2~4
     */
    public List<ItemStack> getDrops() {
        List<ItemStack> ret = new ArrayList<>();
        if (drops() == null) return ret;
        for (String s : drops()) {
            String[] a = s.split(":");
            try {
                double chance = Double.parseDouble(a[0]);
                Random r = new Random();
                boolean exec = r.nextDouble() < chance;
                if (!exec) continue;
                String id = a[1];
                int count;
                if (a[2].contains("~")) {
                    String[] b = a[2].split("~");
                    int min = Integer.parseInt(b[0]);
                    count = r.nextInt(1+Integer.parseInt(b[1])-min) + min;
                } else count = Integer.parseInt(a[2]);
                ret.add(SoulLand.getItemManager().getItem(id,count).toItem());
            } catch (RuntimeException e) {
                System.err.println("Mob drop " + s + " caused an exception");
            }
        }
        return ret;
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

    public int getXpYield() {
        return 0;
    }

    public void setLastDamager(CombatEntity lastDamager) {
        this.lastDamager = lastDamager.getHandle();
    }
}
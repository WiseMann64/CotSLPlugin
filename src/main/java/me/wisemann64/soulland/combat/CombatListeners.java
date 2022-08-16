package me.wisemann64.soulland.combat;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.mobs.SLMob;
import me.wisemann64.soulland.players.SLPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.persistence.PersistentDataType;

import static me.wisemann64.soulland.items.SLItems.key;

public class CombatListeners implements Listener {

    @EventHandler
    public void event(EntityDamageEvent v) {
        if (v.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            v.setCancelled(true);
            return;
        }
        CombatEntity c = SoulLand.getCombatEntity(v.getEntity().getUniqueId());
        if (c == null) return;
        double initDamage = v.getDamage();
        v.setDamage(0);
        switch (v.getCause()) {
            case CONTACT, DRYOUT, CRAMMING, HOT_FLOOR, FLY_INTO_WALL, CUSTOM, DRAGON_BREATH, THORNS, FALLING_BLOCK,
                    POISON, MAGIC, FIRE, FIRE_TICK, WITHER, MELTING -> {
                if (c.getEnvDamageCooldown().getOrDefault(v.getCause(),0) != 0) {
                    v.setCancelled(true);
                    return;
                }
                Damage dam = new Damage(initDamage, DamageType.TRUE,0,false);
                c.dealDamage(dam);
                c.getEnvDamageCooldown().put(v.getCause(),10);
            }
            case ENTITY_ATTACK, PROJECTILE, ENTITY_EXPLOSION -> {
                v.setDamage(initDamage);
                if (v instanceof EntityDamageByEntityEvent v1) damageByEntity(v1);
            }
            case FALL -> {
                float fallDistance = c.getHandle().getFallDistance()-3;
                float frac = fallDistance/25;
                Damage dam = new Damage(frac*c.getMaxHealth(),DamageType.TRUE,0,false);
                c.dealDamage(dam);
            }
            case SUFFOCATION, DROWNING, STARVATION -> {
                if (c.getEnvDamageCooldown().getOrDefault(v.getCause(),0) != 0) {
                    v.setCancelled(true);
                    return;
                }
                Damage dam = new Damage(0.05*c.getMaxHealth(),DamageType.TRUE,0,false);
                c.dealDamage(dam);
                c.getEnvDamageCooldown().put(v.getCause(),10);
            }
            case LAVA -> {
                if (c.getEnvDamageCooldown().getOrDefault(v.getCause(),0) != 0) {
                    v.setCancelled(true);
                    return;
                }
                Damage dam = new Damage(0.1*c.getMaxHealth(),DamageType.TRUE,0,false);
                c.dealDamage(dam);
                c.getEnvDamageCooldown().put(v.getCause(),10);
            }
            case VOID, SUICIDE -> {
                Damage dam = new Damage(c.getMaxHealth(), DamageType.TRUE, 0, true);
                c.dealDamage(dam);
            }
            case LIGHTNING, BLOCK_EXPLOSION -> v.setCancelled(true);
        }
    }

    private void damageByEntity(EntityDamageByEntityEvent v) {
        CombatEntity rec = SoulLand.getCombatEntity(v.getEntity().getUniqueId());
        if (rec == null) return;
        double dmg = v.getDamage();
        v.setDamage(0);
        Entity dam0 = v.getDamager();
        if (dam0 instanceof Arrow a) {
            damageByArrow(rec,a,v);
            return;
        }
        CombatEntity dam1 = SoulLand.getCombatEntity(dam0.getUniqueId());
        if (dam1 == null) {
            Damage damage = new Damage(dmg,DamageType.TRUE,0,false);
            rec.dealDamage(damage);
            return;
        }
        // PLAYER TO PLAYER
        if (rec instanceof SLPlayer && dam1 instanceof SLPlayer) {
            v.setCancelled(true);
            return;
        }
        // MOB TO PLAYER
        if (rec instanceof SLPlayer p && dam1 instanceof SLMob dam2) {
            double damage = v.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ? dam2.getExplosionPower() : dam2.getAttackPower();
            double pen = dam2.getPhysicalPEN();
            Damage d = new Damage(damage,DamageType.PHYSICAL,pen,false);
            if (p.isInvis()) d.setNewValue(0);
            d.addPen(2.5*(dam2.getLevel()-p.getLevel()));
            p.dealDamage(d);
            return;
        }
        // PLAYER TO MOB
        if (dam1 instanceof SLPlayer p && rec instanceof SLMob rec1) {
            if (rec1.getDamageCooldown().getOrDefault(p.getUUID(),0) != 0) {
                v.setCancelled(true);
                return;
            }
            Damage d = p.basicAttack();
            d.setDamager(p);
            if (rec1.isInvis()) {
                v.setCancelled(true);
                return;
            }
            d.addPen(2.5*(p.getLevel()-rec1.getLevel()));
            rec1.dealDamage(d);
            rec1.getDamageCooldown().put(p.getUUID(),10);
            return;
        }
        // MOB TO MOB
        if (dam1 instanceof SLMob dam2 && rec instanceof SLMob rec1) {
            double damage = v.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ? dam2.getExplosionPower() : dam2.getAttackPower();
            Damage d = new Damage(damage,DamageType.PHYSICAL,dam2.getPhysicalPEN(),false);
            d.setDamager(dam2);
            if (rec1.isInvis()) {
                v.setCancelled(true);
                return;
            }
            d.addPen(2.5*(dam2.getLevel()-rec1.getLevel()));
            rec1.dealDamage(d);
        }
    }

    @EventHandler
    public void event(ProjectileLaunchEvent v) {
        if (!(v.getEntity() instanceof Arrow a)) return;
        if (!(a.getShooter() instanceof Entity e)) return;
        CombatEntity c = SoulLand.getCombatEntity(e.getUniqueId());
        boolean crit = a.isCritical();
        double damage;
        String shooter;
        double pen;
        int level;
        if (c instanceof SLPlayer p) {
            Damage dmg = p.arrowAttack(crit);
            damage = dmg.getOldValue();
            shooter = "player";
            pen = p.getPhysicalPEN();
            level = p.getLevel();
            crit = dmg.isCrit();
        } else if (c instanceof SLMob m) {
            damage = m.getRangedAttackPower();
            shooter = "mob";
            pen = m.getPhysicalPEN();
            level = m.getLevel();
        } else {
            damage = a.getDamage();
            shooter = "generic";
            pen = 0;
            level = 0;
        }
        a.getPersistentDataContainer().set(key("arrow_damage"), PersistentDataType.DOUBLE,damage);
        a.getPersistentDataContainer().set(key("arrow_shooter"), PersistentDataType.STRING,shooter);
        a.getPersistentDataContainer().set(key("arrow_shooter_uuid"), new CombatEntityPersistentDataType(),c);
        a.getPersistentDataContainer().set(key("arrow_ppen"), PersistentDataType.DOUBLE,pen);
        a.getPersistentDataContainer().set(key("arrow_level"), PersistentDataType.INTEGER,level);
        a.getPersistentDataContainer().set(key("arrow_critical"), PersistentDataType.INTEGER,crit ? 1 : 0);
    }

    private void damageByArrow(CombatEntity rec, Arrow a, EntityDamageByEntityEvent v) {
        if (rec.isInvis()) {
            v.setCancelled(true);
            return;
        }
        double damage = a.getPersistentDataContainer().getOrDefault(key("arrow_damage"),PersistentDataType.DOUBLE,0.0);
        String shooter = a.getPersistentDataContainer().getOrDefault(key("arrow_shooter"),PersistentDataType.STRING,"generic");
        double pen = a.getPersistentDataContainer().getOrDefault(key("arrow_ppen"),PersistentDataType.DOUBLE,0.0);
        int level = a.getPersistentDataContainer().getOrDefault(key("arrow_level"),PersistentDataType.INTEGER,0);
        boolean crit = a.getPersistentDataContainer().getOrDefault(key("arrow_critical"),PersistentDataType.INTEGER,0) == 1;
        CombatEntity damager = a.getPersistentDataContainer().get(key("arrow_shooter_uuid"),new CombatEntityPersistentDataType());
        if (rec instanceof SLPlayer && shooter.equals("player")) {
            v.setCancelled(true);
            return;
        }
        Damage dmg = new Damage(damage,DamageType.PHYSICAL,pen,crit);
        dmg.setDamager(damager);
        dmg.addPen(2.5*(level-rec.getLevel()));
        rec.dealDamage(dmg);
    }
}
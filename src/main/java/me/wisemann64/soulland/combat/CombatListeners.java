package me.wisemann64.soulland.combat;

import me.wisemann64.soulland.SoulLand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatListeners implements Listener {

    @EventHandler
    public void event(EntityDamageEvent v) {
        if (v.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            v.setCancelled(true);
            return;
        }
        CombatEntity c = SoulLand.getCombatEntity(v.getEntity().getUniqueId());
        if (c == null) return;
        switch (v.getCause()) {
            case CONTACT, DRYOUT, CRAMMING, HOT_FLOOR, FLY_INTO_WALL, CUSTOM, DRAGON_BREATH, THORNS, FALLING_BLOCK,
                    POISON, MAGIC, FIRE, FIRE_TICK, WITHER, MELTING -> {
                if (c.getEnvDamageCooldown().getOrDefault(v.getCause(),0) != 0) {
                    v.setCancelled(true);
                    return;
                }
                Damage dam = new Damage(v.getDamage(), DamageType.TRUE,0,false);
                c.dealDamage(dam);
                c.getEnvDamageCooldown().put(v.getCause(),10);
            }
            case ENTITY_ATTACK, PROJECTILE, ENTITY_EXPLOSION -> {
                // CUSTOMIZE
            }
            case FALL -> {
                float fallDistance = Math.min(40,c.getHandle().getFallDistance()-3);
                float frac = fallDistance/40;
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
                c.getEnvDamageCooldown().put(v.getCause(),5);
            }
            case LAVA -> {
                // TRUE DAMAGE
            }
            case VOID, SUICIDE -> {
                Damage dam = new Damage(c.getMaxHealth(), DamageType.TRUE, 1300, true);
                c.dealDamage(dam);
            }
            case LIGHTNING, BLOCK_EXPLOSION -> v.setCancelled(true);
        }
        v.setDamage(0);
    }

}

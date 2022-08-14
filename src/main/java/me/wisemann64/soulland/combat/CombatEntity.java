package me.wisemann64.soulland.combat;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.Utils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumMap;
import java.util.Random;

public interface CombatEntity {

    boolean isInvis();
    double getHealth();
    double getMaxHealth();
    float getHealthFraction();
    double getAttackPower();
    double getMagicAttackPower();
    double getRangedAttackPower();
    double getDefense();
    double getMagicDefense();
    double getPhysicalPEN();
    double getMagicPEN();
    int getLevel();
    Location getLocation();
    Location getEyeLocation();
    void setHealth(double health);
    void heal(double amount);
    double dealDamage(double finalDamage);
    Entity getHandle();
    void sendMessage(String msg);
    EnumMap<EntityDamageEvent.DamageCause, Integer> getEnvDamageCooldown();
    default double dealDamage(Damage damage) {
        double def = switch (damage.getType()) {
            case PHYSICAL -> getDefense();
            case MAGIC -> getMagicDefense();
            case TRUE -> 0;
        };
        def -= damage.getPEN();
        def = Math.max(def,-300);
        double mul = 1 + 0.01 * (def < 0 ? -def*0.1575757576 : def*def/16500 - 26*def/165);
        damage.setNewValue(dealDamage(damage.getOldValue()*mul));
        showIndicator(damage);
        return damage.getNewValue();
    }
    default void showIndicator(Damage damage) {
        Location loc = getEyeLocation().clone();
        Random random = new Random();
        loc.add(1.4*random.nextDouble()-0.7,0.6*random.nextDouble()-0.8,1.4*random.nextDouble()-0.7);
        StringBuilder value = new StringBuilder(damage.isCrit() ? damage.getType().getAlternateColor() + "✧" : damage.getType().getColor());
        value.append(String.format("%.2f",damage.getNewValue()));
        if (damage.isCrit()) value.append("✧");
        ArmorStand label = loc.getWorld().spawn(loc,ArmorStand.class, e -> {
            e.setVisible(false);
            e.setBasePlate(false);
            e.setCustomNameVisible(true);
            e.setGravity(false);
            e.setMarker(true);
            e.setInvulnerable(true);
            e.setCustomName(Utils.color(value.toString()));
        });
        new BukkitRunnable(){
            @Override
            public void run() {
                label.remove();
            }
        }.runTaskLaterAsynchronously(SoulLand.getPlugin(),60L);
    }
}

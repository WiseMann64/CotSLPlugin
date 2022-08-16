package me.wisemann64.soulland.gameplay.objective;

import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.system.mobs.MobGeneric;
import me.wisemann64.soulland.system.mobs.SLMob;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Constructor;

public class ObjectiveKillMob extends Objective {

    private SLMob mobToKill = null;
    private boolean mobDied = false;

    public ObjectiveKillMob(Location location, MobGeneric.Customizer genericMobCustomizer) {
        mobToKill = new MobGeneric(location.getWorld(),genericMobCustomizer);
        mobToKill.setObjKill(this);
        mobToKill.spawn(location);
    }

    public ObjectiveKillMob(Location location, Class<? extends SLMob> mobClass, String name) {
        Constructor<?>[] constructors = mobClass.getDeclaredConstructors();
        for (Constructor<?> co : constructors) {
            if (co.getParameterCount() != 2) continue;
            Class<?>[] pt = co.getParameterTypes();
            if (!pt[0].equals(World.class)) continue;
            if (!pt[1].equals(String.class)) continue;
            try {
                mobToKill = (SLMob) co.newInstance(location.getWorld(), name);
            } catch (Exception ex) {
                System.err.println("Failed to create ObjectiveKillMob " + this + " due to mob can't be spawned");
                return;
            }
            break;
        }
        if (mobToKill != null) {
            mobToKill.setObjKill(this);
            mobToKill.spawn(location);
        }
    }

    @Override
    public boolean check(GameManager game) {
        if (mobToKill.getHandle().isDead()) return true;
        return mobDied;
    }

    public void mobDie() {
        mobDied = true;
    }
}

package me.wisemann64.soulland.gameplay.objective;

import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.system.mobs.MobGeneric;
import me.wisemann64.soulland.system.mobs.SLMob;
import org.bukkit.Location;
import org.bukkit.World;

public class ObjectiveKillMob extends Objective {

    private final SLMob mobToKill;
    private boolean mobDied = false;

    public ObjectiveKillMob(Location location, MobGeneric.Customizer genericMobCustomizer) {
        mobToKill = new MobGeneric(location.getWorld(),genericMobCustomizer);
        mobToKill.setObjKill(this);
        mobToKill.spawn(location);
    }

    @Override
    public boolean check(GameManager game) {
        return mobDied;
    }

    public void mobDie() {
        mobDied = true;
    }
}

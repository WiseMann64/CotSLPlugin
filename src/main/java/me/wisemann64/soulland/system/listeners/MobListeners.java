package me.wisemann64.soulland.system.listeners;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.mobs.SLMob;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class MobListeners implements Listener {

    @EventHandler
    public void event(EntityDeathEvent v) {
        v.setDroppedExp(0);
        v.getDrops().clear();
        SLMob d = SoulLand.getMobManager().getMob(v.getEntity().getUniqueId());
        if (d == null) return;
        d.getDrops().forEach(v.getDrops()::add);
    }

    @EventHandler
    public void event(EntityExplodeEvent v) {
        System.out.println(v.getLocation());
        if (v.getEntity() instanceof Creeper c) {
            System.out.println(c);
        }
    }
}

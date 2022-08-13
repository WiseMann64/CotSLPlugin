package me.wisemann64.soulland.listeners;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.mobs.SLMob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobListeners implements Listener {

    @EventHandler
    public void event(EntityDeathEvent v) {
        v.setDroppedExp(0);
        v.getDrops().clear();
        SLMob d = SoulLand.getMobManager().getMob(v.getEntity().getUniqueId());
        if (d == null) return;
        d.getDrops().forEach(v.getDrops()::add);
    }
}

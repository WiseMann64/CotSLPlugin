package me.wisemann64.soulland.listeners;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.players.SLPlayer;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListeners implements Listener {
    /*
    Prevent Breaking and Placing Blocks
     */
    @EventHandler
    public void event(BlockPlaceEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getPlayer());
        if (p == null) return;
        if (!(p.isDebugMode() || p.getHandle().getGameMode() == GameMode.CREATIVE)) v.setCancelled(true);
    }

    @EventHandler
    public void event(BlockBreakEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getPlayer());
        if (p == null) return;
        if (!(p.isDebugMode() || p.getHandle().getGameMode() == GameMode.CREATIVE)) v.setCancelled(true);
    }

    @EventHandler
    public void event(EntityPotionEffectEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getEntity().getUniqueId());
        if (p == null) return;
        if (v.getAction() != EntityPotionEffectEvent.Action.ADDED) return;
        PotionEffect eff = v.getNewEffect();
        if (eff == null) return;
        if (eff.getType().equals(PotionEffectType.ABSORPTION)) {
            int duration = eff.getDuration();
            int level = eff.getAmplifier() + 1;
            p.setAbsorption(p.getMaxHealth()*level/5,duration);
            v.setCancelled(true);
        }
        if (eff.getType().equals(PotionEffectType.HEALTH_BOOST)) v.setCancelled(true);
        if (eff.getType().equals(PotionEffectType.JUMP)) v.setCancelled(true);
        if (eff.getType().equals(PotionEffectType.FIRE_RESISTANCE)) v.setCancelled(true);
    }
}

package me.wisemann64.soulland.listeners;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.players.SLPlayer;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

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
}

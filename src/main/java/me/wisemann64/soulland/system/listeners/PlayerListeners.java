package me.wisemann64.soulland.system.listeners;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.LockManager;
import me.wisemann64.soulland.system.items.SLItems;
import me.wisemann64.soulland.system.players.SLPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
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
    public void event(PlayerInteractEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getPlayer());
        if (p == null) return;
        if (v.getHand() == EquipmentSlot.OFF_HAND) return;
        switch (v.getAction()) {
            case RIGHT_CLICK_BLOCK -> {
                Block b = v.getClickedBlock();
                if (b == null) return;
                if (b.getType() == Material.DARK_OAK_DOOR) {
                    Bisected data = (Bisected) b.getBlockData();
                    Location l = b.getLocation();
                    if (data.getHalf() == Bisected.Half.TOP) l.subtract(0,1,0);
                    String base = "x=" + l.getBlockX() + " y=" + l.getBlockY() + " z=" + l.getBlockZ() + " world=" + l.getWorld().getName();
                    LockManager.Lock lock = SoulLand.getLockManager().getLock(base);
                    if (lock == null) return;
                    switch (lock.type()) {
                        case ABSOLUTE -> v.setCancelled(true);
                        case LOCKED -> {
                            if (((Openable) data).isOpen()) return;
                            String id = lock.keyId();
                            if (id == null) return;
                            ItemStack hand = p.getHandle().getInventory().getItemInMainHand();
                            if (hand.getItemMeta() == null) {
                                v.setCancelled(true);
                                return;
                            }
                            if (!id.equals(SLItems.getString(hand,"lock"))) v.setCancelled(true);
                        }
                        case PROMPT -> {
                            if (lock.prompt() == null) return;
                            boolean lockDoor = lock.prompt().test(SoulLand.getGameManager());
                            if (lockDoor) v.setCancelled(true);
                        }
                    }
                }
            }
        }
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

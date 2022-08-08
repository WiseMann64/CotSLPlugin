package me.wisemann64.soulland;

import me.wisemann64.soulland.menu.MainMenu;
import me.wisemann64.soulland.players.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager implements Listener {

    private final List<SLPlayer> players;

    public PlayerManager() {
        players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            addPlayer(new SLPlayer(p));
        }
    }

    public List<SLPlayer> getPlayers() {
        return players;
    }
    public void addPlayer(SLPlayer player) {
        for (SLPlayer p : players) {
            if (p.getUUID().equals(player.getUUID())) return;
        }
        players.add(player);
    }

    public SLPlayer getPlayer(UUID uuid) {
        for (SLPlayer p : players) {
            if (p.getUUID().equals(uuid)) return p;
        }
        return null;
    }

    public SLPlayer getPlayer(Player player) {
        for (SLPlayer p : players) {
            if (p.getHandle().equals(player)) return p;
        }
        return null;
    }

    public void removePlayer(SLPlayer player) {
        players.remove(player);
    }

    public void removePlayer(Player player) {
        SLPlayer toRemove = null;
        for (SLPlayer p : players) {
            if (p.getHandle().equals(player)) toRemove = p;
        }
        if (toRemove != null) removePlayer(toRemove);
    }

    public void removePlayer(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) removePlayer(p);
    }

    @EventHandler
    public void event(PlayerJoinEvent v) {
        SLPlayer join = new SLPlayer(v.getPlayer());
        players.add(join);
        join.onJoin();
    }

    @EventHandler
    public void event(PlayerQuitEvent v) {
        SLPlayer player = getPlayer(v.getPlayer());
        if (player == null) return;
        removePlayer(player);
        player.logout();
    }

    @EventHandler
    public void event(EntityRegainHealthEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getEntity().getUniqueId());
        if (p == null) return;
        EntityRegainHealthEvent.RegainReason res = v.getRegainReason();
        if (res == EntityRegainHealthEvent.RegainReason.CUSTOM || res == EntityRegainHealthEvent.RegainReason.ENDER_CRYSTAL ||
                res == EntityRegainHealthEvent.RegainReason.WITHER || res == EntityRegainHealthEvent.RegainReason.WITHER_SPAWN) return;
        p.heal(v.getAmount());
    }

    @EventHandler
    public void event(EntityDamageEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getEntity().getUniqueId());
        if (p == null) return;
        p.damage(v.getDamage());
        v.setDamage(0);
    }

    @EventHandler
    public void event(EntityPotionEffectEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getEntity().getUniqueId());
        if (p == null) return;
        if (v.getAction() != EntityPotionEffectEvent.Action.ADDED) return;
        PotionEffect eff = v.getNewEffect();
        if (eff == null) return;
        if (!(eff.getType().equals(PotionEffectType.ABSORPTION))) return;
        int duration = eff.getDuration();
        int level = eff.getAmplifier() + 1;
        p.setAbsorption(p.getMaxHealth()*level/5,duration);
        v.setCancelled(true);
    }

    @EventHandler
    public void event(InventoryClickEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getWhoClicked().getUniqueId());
        if (p == null) return;
        if (v.getHotbarButton() == 8) {
            v.setCancelled(true);
            return;
        }
        if (!(v.getClickedInventory() instanceof PlayerInventory)) return;
        if (v.getSlot() != 8) return;
        v.setResult(Event.Result.DENY);
        p.openMenu(new MainMenu(p));
        v.setCancelled(true);
    }

    @EventHandler
    public void event(PlayerDropItemEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getPlayer());
        if (p == null) return;
        if (p.getHandle().getInventory().getHeldItemSlot() != 8) return;
        v.setCancelled(true);
        p.openMenu(new MainMenu(p));
    }

    @EventHandler
    public void event(PlayerSwapHandItemsEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getPlayer());
        if (p == null) return;
        if (p.getHandle().getInventory().getHeldItemSlot() != 8) return;
        v.setCancelled(true);
        p.openMenu(new MainMenu(p));
    }

    @EventHandler
    public void event(PlayerInteractEvent v) {
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(v.getPlayer());
        if (p == null) return;
        if (p.getHandle().getInventory().getHeldItemSlot() != 8) return;
        if (v.getHand() == EquipmentSlot.OFF_HAND) return;
        v.setCancelled(true);
        p.openMenu(new MainMenu(p));
    }
}

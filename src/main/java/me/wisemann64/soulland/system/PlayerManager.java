package me.wisemann64.soulland.system;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.menu.MainMenu;
import me.wisemann64.soulland.system.players.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

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
        for (SLPlayer p : players) if (p.getHandle().equals(player)) toRemove = p;
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
        v.setAmount(0);
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

package me.wisemann64.soulland.system.listeners;

import me.wisemann64.soulland.system.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {

    @EventHandler
    public void event(InventoryClickEvent v) {
        if (v.getInventory().getHolder() instanceof Menu menu) menu.onClick(v);
    }
}

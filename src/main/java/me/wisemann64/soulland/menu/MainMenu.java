package me.wisemann64.soulland.menu;

import me.wisemann64.soulland.items.SLItems;
import me.wisemann64.soulland.players.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class MainMenu extends Menu {

    public MainMenu(SLPlayer owner) {
        super(owner);
    }

    @Override
    protected Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(this,54,"Curse of The Soul Land");
        for (int i = 0; i < 53; i++) {
            inv.setItem(i,(PANE));
        }
        inv.setItem(53,SLItems.generator(Material.REDSTONE,"&6Your Stats", List.of("&eClick here to view"),null));
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        clickEvent.setCancelled(true);
        if (getInventory().equals(clickEvent.getClickedInventory())) {
            if (clickEvent.getSlot() == 53) getOwner().openMenu(new StatsMenu(getOwner(),this));
        }
    }
}

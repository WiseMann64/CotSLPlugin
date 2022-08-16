package me.wisemann64.soulland.menu;

import me.wisemann64.soulland.items.SLItems;
import me.wisemann64.soulland.players.SLPlayer;
import me.wisemann64.soulland.players.Stats;
import me.wisemann64.soulland.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends Menu {

    public MainMenu(SLPlayer owner) {
        super(owner);
    }

    @Override
    protected Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(this,54,"Curse of The Soul Land");
        inv.setItem(0,getStats());
        inv.setItem(1,a("STR"));
        inv.setItem(2,a("VIT"));
        inv.setItem(3,a("INT"));
        inv.setItem(4,a("CRIT"));
        inv.setItem(53,SLItems.generator(Material.REDSTONE,"&6Your Stats", List.of("&eClick here to view"),null));
        return inv;
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        clickEvent.setCancelled(true);
        if (getInventory().equals(clickEvent.getClickedInventory())) {
            if (clickEvent.getSlot() == 1) {
                getOwner().getAttributes().addStats(Stats.STR);
                update();
            }
            if (clickEvent.getSlot() == 2) {
                getOwner().getAttributes().addStats(Stats.VIT);
                update();
            }
            if (clickEvent.getSlot() == 3) {
                getOwner().getAttributes().addStats(Stats.INT);
                update();
            }
            if (clickEvent.getSlot() == 4) {
                getOwner().getAttributes().addStats(Stats.CRIT);
                update();
            }
            if (clickEvent.getSlot() == 53) getOwner().openMenu(new StatsMenu(getOwner(),this));
        }
    }

    private ItemStack getStats() {
        ItemStack ret = new ItemStack(Material.STONE);
        ItemMeta meta = ret.getItemMeta();
        meta.setDisplayName(Utils.color("&3Stats"));
        List<String> lore = new ArrayList<>();
        lore.add("&cStrength: " + getOwner().getAttributes().getAttribute(Stats.STR));
        lore.add("&bIntelligence: " + getOwner().getAttributes().getAttribute(Stats.INT));
        lore.add("&aVitality: " + getOwner().getAttributes().getAttribute(Stats.VIT));
        lore.add("&6Critical: " + getOwner().getAttributes().getAttribute(Stats.CRIT));
        lore.add("&eAvailable: " + getOwner().getAttributes().getStatsPoint());
        meta.setLore(Utils.color(lore));
        ret.setItemMeta(meta);
        return ret;
    }

    private ItemStack a(String s) {
        ItemStack ret = new ItemStack(Material.STONE);
        ItemMeta meta = ret.getItemMeta();
        meta.setDisplayName(Utils.color(s));
        ret.setItemMeta(meta);
        return ret;
    }
    
    private void update() {
        getInventory().setItem(0,getStats());
    }
}

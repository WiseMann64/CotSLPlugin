package me.wisemann64.soulland.menu;

import me.wisemann64.soulland.players.SLPlayer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static me.wisemann64.soulland.Utils.color;

public abstract class Menu implements InventoryHolder {

    private Inventory inventory;
    private final SLPlayer owner;
    protected Menu backMenu;

    public Menu(SLPlayer owner) {
        this.owner = owner;
        this.inventory = createInventory();
    }

    public Menu(SLPlayer owner, Menu from) {
        this.owner = owner;
        backMenu = from;
        this.inventory = createInventory();
    }

    protected abstract Inventory createInventory();
    public abstract void onClick(InventoryClickEvent clickEvent);

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    public void overrideInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public SLPlayer getOwner() {
        return owner;
    }

    public void open() {
        getOwner().openMenu(this);
    }
    public void close() {
        getOwner().getHandle().closeInventory();
    }
    public void back() {
        if (backMenu != null) backMenu.open();
    }

    protected final static ItemStack PANE = createPane();
    protected final static ItemStack BACK = createBack();
    protected final static ItemStack CLOSE = createClose();
    protected final static ItemStack COMING_SOON = createComingSoon();
    protected final static ItemStack NOT_YET_UNLOCKED = createNotYetUnlocked();

    private static ItemStack createPane() {
        ItemStack is = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = is.getItemMeta();
        assert meta != null;
        meta.setDisplayName(color("&e"));
        is.setItemMeta(meta);
        return is;
    }
    private static ItemStack createBack() {
        ItemStack pane = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = pane.getItemMeta();
        assert meta != null;
        meta.setDisplayName(color("&eGo Back"));
        pane.setItemMeta(meta);
        return pane;
    }
    private static ItemStack createClose() {
        ItemStack pane = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = pane.getItemMeta();
        assert meta != null;
        meta.setDisplayName(color("&cClose"));
        pane.setItemMeta(meta);
        return pane;
    }
    private static ItemStack createComingSoon() {
        ItemStack pane = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta meta = pane.getItemMeta();
        assert meta != null;
        meta.setDisplayName(color("&eComing Soon!"));
        pane.setItemMeta(meta);
        return pane;
    }

    private static ItemStack createNotYetUnlocked() {
        ItemStack pane = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = pane.getItemMeta();
        assert meta != null;
        meta.setDisplayName(color("&cNot Yet Unlocked"));
        pane.setItemMeta(meta);
        return pane;
    }

}

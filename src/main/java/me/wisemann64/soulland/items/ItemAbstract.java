package me.wisemann64.soulland.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static me.wisemann64.soulland.items.SLItems.*;

public abstract class ItemAbstract {

    protected final String id;
    protected final ItemType type;
    protected final Material material;
    protected String name;

    public ItemAbstract(String id, ItemType type, Material material) {
        this.id = id;
        this.type = type;
        this.material = material;
    }

    protected final ItemStack generate() {
        ItemStack ret = new ItemStack(material);
        setId(ret,id);
        setString(ret,"type",type.toString());
        hideAllFlags(ret);
        return ret;
    }

    public abstract ItemStack toItem();
    public abstract ItemAbstract fromItem(ItemStack from);

    public String getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public String getName() {
        return name;
    }
}

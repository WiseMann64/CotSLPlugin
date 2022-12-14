package me.wisemann64.soulland.system.items;

import me.wisemann64.soulland.SoulLand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class ItemAbstract implements Cloneable {

    private final String id;
    private final ItemType type;
    private final Material material;
    private String name;
    private List<String> lore = null;
    protected int count = 1;
    protected boolean glow;
    protected String lock = null;

    public ItemAbstract(String id, ItemType type, Material material) {
        this.id = id;
        this.type = type;
        this.material = material;
    }

    protected final ItemStack generate() {
        ItemStack ret = new ItemStack(material);
        SLItems.setId(ret,id);
        SLItems.setString(ret,"type",type.toString());
        SLItems.hideAllFlags(ret);
        if (glow) SLItems.setGlowing(ret);
        if (lock != null) SLItems.setString(ret,"lock", lock);
        return ret;
    }

    public abstract ItemStack toItem();

    public static ItemAbstract fromItem(ItemStack from) {
        ItemMeta meta = from.getItemMeta();
        String id = SLItems.getId(from);
        ItemType type = ItemType.valueOf(SLItems.getString(from,"type"));
        ItemAbstract i0 = SoulLand.getItemManager().getItem(id).clone();
        i0.setCount(from.getAmount());
        i0.setLock(SLItems.getString(from,"lock"));
        switch (type) {
            case WEAPON, ARMOR -> {
                ItemModifiable iw = (ItemModifiable) i0;
                PersistentDataContainer modifiable = meta.getPersistentDataContainer().get(SLItems.key("modifiable"), PersistentDataType.TAG_CONTAINER);
                iw.setUpgradeLevel(modifiable.getOrDefault(SLItems.key("upgrade"),PersistentDataType.INTEGER,0));
                int slot = modifiable.getOrDefault(SLItems.key("slot"),PersistentDataType.INTEGER,0);
                iw.setSlotted(slot == 1);
                String power = modifiable.getOrDefault(SLItems.key("power"),PersistentDataType.STRING,"NONE");
                iw.setPowerStone("NONE".equals(power) ? null : power);
            }
        }
        return i0;
    }

    public static void refresh(ItemStack item) {
        try {
            ItemStack ref = fromItem(item).toItem();
            item.setItemMeta(ref.getItemMeta());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public ItemType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public final String getName() {
        return name;
    }

    protected final void setName(String name) {
        this.name = name;
    }

    public final List<String> getLore() {
        return lore;
    }

    public final void setLore(List<String> lore) {
        this.lore = lore;
    }

    @Override
    public ItemAbstract clone() {
        try {
            ItemAbstract clone = (ItemAbstract) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public int getCount() {
        return count;
    }

    public ItemAbstract setCount(int count) {
        this.count = count;
        return this;
    }

    public ItemAbstract setGlow(boolean glow) {
        this.glow = glow;
        return this;
    }

    public ItemAbstract setLock(String key) {
        this.lock = key;
        return this;
    }
}

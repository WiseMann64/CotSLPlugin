package me.wisemann64.soulland.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemWeapon extends ItemAbstract {

    public ItemWeapon(String id, Material material) {
        super(id, ItemType.WEAPON, material);
    }

    @Override
    public ItemStack toItem() {
        return generate();
    }

    @Override
    public ItemAbstract fromItem(ItemStack from) {
        return null;
    }
}

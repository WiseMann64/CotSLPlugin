package me.wisemann64.soulland.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.wisemann64.soulland.util.Utils.color;
import static me.wisemann64.soulland.items.SLItems.hideAllFlags;

public class ItemKey extends ItemAbstract {

    public ItemKey(String id, Material material) {
        super(id, ItemType.KEY, material);
    }

    @Override
    public ItemStack toItem() {
        ItemStack ret = generate();
        ret.setAmount(count);
        hideAllFlags(ret);
        ItemMeta meta = ret.getItemMeta();
        meta.setDisplayName(color("&4" + getName()));
        meta.setLore(generateLore());
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,10,true);
        ret.setItemMeta(meta);
        return ret;
    }

    protected List<String> generateLore() {
        List<String> lore = new ArrayList<>();
        lore.add("&cKey Item");
        if (!getLore().isEmpty()) {
            lore.add("");
            getLore().forEach(s -> lore.add("&7" + s));
        }
        return color(lore);
    }
}

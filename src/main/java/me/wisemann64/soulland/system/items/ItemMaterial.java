package me.wisemann64.soulland.system.items;

import me.wisemann64.soulland.system.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.wisemann64.soulland.system.util.Utils.color;

public class ItemMaterial extends ItemAbstract {


    public ItemMaterial(String id, Material material) {
        super(id, ItemType.MATERIAL, material);
    }

    @Override
    public ItemStack toItem() {
        ItemStack ret = generate();
        ret.setAmount(count);
        ItemMeta meta = ret.getItemMeta();
        meta.setDisplayName(color("&e" + getName()));
        meta.setLore(generateLore());
        ret.setItemMeta(meta);
        return ret;
    }

    protected List<String> generateLore() {
        List<String> lore = new ArrayList<>();
        lore.add("&8Materials");
        if (!getLore().isEmpty()) {
            lore.add("");
            getLore().forEach(s -> lore.add("&7" + s));
        }
        return Utils.color(lore);
    }
}

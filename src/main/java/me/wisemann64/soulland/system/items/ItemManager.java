package me.wisemann64.soulland.system.items;

import me.wisemann64.soulland.SoulLand;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemManager {

    private Map<String, ItemAbstract> items;
    private final YamlConfiguration config;

    public ItemManager() {
        items = new HashMap<>();
        SoulLand.getPlugin().saveResource("items/itemlist.yml",true);
        File file = new File(SoulLand.getPlugin().getDataFolder().getPath() + "/items/itemlist.yml");
        config = YamlConfiguration.loadConfiguration(file);
        registerItems();
    }

    private void registerItems() {
        Set<String> ids = config.getKeys(false);
        for (String id : ids) {
            ConfigurationSection sect = config.getConfigurationSection(id);
            if (sect == null) continue;
            ItemType type = ItemType.valueOf(sect.getString("type"));
            Material mat = Material.valueOf(sect.getString("material"));
            ItemAbstract i = switch (type) {
                case WEAPON -> new ItemWeapon(id,mat);
                case MATERIAL -> new ItemMaterial(id,mat);
                case KEY -> new ItemKey(id,mat);
                case ARMOR -> new ItemArmor(id,mat);
            };
            i.setName(sect.getString("name"));
            i.setLore(sect.getStringList("lore"));
            i.setGlow(sect.getBoolean("glow",false));

            if (i instanceof ItemModifiable im) {
                ConfigurationSection mod = sect.getConfigurationSection("mod");
                if (mod != null) im.readModifiableData(mod);
            }

            if (i instanceof ItemArmor ia) {
                ConfigurationSection armor = sect.getConfigurationSection("armor");
                if (armor != null) ia.readArmorData(armor);
            }

            items.put(id,i);
        }
    }

    public Set<String> getItemIds() {
        return items.keySet();
    }

    public ItemAbstract getItem(String id) {
        return items.get(id).clone();
    }

    public ItemAbstract getItem(String id, int amount) {
        return items.get(id).clone().setCount(amount);
    }
}

package me.wisemann64.soulland.items;

import me.wisemann64.soulland.SoulLand;
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

        }
    }
}

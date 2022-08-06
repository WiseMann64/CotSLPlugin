package me.wisemann64.soulland.items;

import me.wisemann64.soulland.SoulLand;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.wisemann64.soulland.Utils.color;

public class SLItems {

    public static NamespacedKey key(@NotNull String key) {
        return new NamespacedKey(SoulLand.getPlugin(),key);
    }

    public static String getId(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(key("id"), PersistentDataType.STRING) ? data.get(key("id"),PersistentDataType.STRING) : null;
    }

    public static void setId(@NotNull ItemStack item, @NotNull String id) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key("id"),PersistentDataType.STRING,id);
        item.setItemMeta(meta);
    }

    public static String getString(@NotNull ItemStack item, @NotNull  String key) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(key(key), PersistentDataType.STRING) ? data.get(key(key),PersistentDataType.STRING) : null;
    }

    public static void setString(@NotNull ItemStack item, @NotNull String key, @NotNull String value) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(key(key),PersistentDataType.STRING,value);
        item.setItemMeta(meta);
    }

    public static void setName(@NotNull ItemStack item, @NotNull String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        item.setItemMeta(meta);
    }

    public static ItemStack menuStar() {
        ItemStack ret = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = ret.getItemMeta();
        meta.setDisplayName(color("&e>> &6&lMenu &e<<"));
        meta.setLore(List.of(color("&eClick here to open")));
        ret.setItemMeta(meta);
        setId(ret,"menu_item");
        return ret;
    }

    public static ItemStack generator(@NotNull Material material, @Nullable  String name, @Nullable List<String> lore, @Nullable String id) {
        ItemStack ret = new ItemStack(material);
        ItemMeta meta = ret.getItemMeta();
        if (name != null) meta.setDisplayName(color(name));
        if (lore != null) meta.setLore(color(lore));
        if (id != null) setId(ret,id);
        ret.setItemMeta(meta);
        return ret;
    }

    public static void hideAllFlags(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        for (ItemFlag v : ItemFlag.values()) meta.addItemFlags(v);
        item.setItemMeta(meta);
    }
}

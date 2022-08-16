package me.wisemann64.soulland.system.items;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.util.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static me.wisemann64.soulland.system.util.Utils.color;

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
        meta.setDisplayName(Utils.color(name));
        item.setItemMeta(meta);
    }

    public static ItemStack menuStar() {
        ItemStack ret = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = ret.getItemMeta();
        meta.setDisplayName(Utils.color("&e>> &6&lMenu &e<<"));
        meta.setLore(List.of(Utils.color("&eClick here to open")));
        ret.setItemMeta(meta);
        setId(ret,"menu_item");
        return ret;
    }

    public static ItemStack generator(@NotNull Material material, @Nullable  String name, @Nullable List<String> lore, @Nullable String id) {
        ItemStack ret = new ItemStack(material);
        ItemMeta meta = ret.getItemMeta();
        if (name != null) meta.setDisplayName(Utils.color(name));
        if (lore != null) meta.setLore(Utils.color(lore));
        if (id != null) setId(ret,id);
        ret.setItemMeta(meta);
        return ret;
    }

    public static void hideAllFlags(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        for (ItemFlag v : ItemFlag.values()) meta.addItemFlags(v);
        item.setItemMeta(meta);
    }

    public static void setGlowing(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.DURABILITY,10,true);
        item.setItemMeta(meta);
    }

    public static net.minecraft.server.v1_16_R3.ItemStack nms(ItemStack from) {
        return CraftItemStack.asNMSCopy(from);
    }

    public static void stripDefense(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR,new AttributeModifier(UUID.randomUUID(),"armor",0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS,new AttributeModifier(UUID.randomUUID(),"armor_toughness",0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        item.setItemMeta(meta);
    }
}

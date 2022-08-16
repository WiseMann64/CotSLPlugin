package me.wisemann64.soulland.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static me.wisemann64.soulland.util.Utils.color;
import static me.wisemann64.soulland.items.SLItems.key;

public class ItemArmor extends ItemAbstract implements ItemModifiable {

    private final EnumMap<ItemModifiers,Double> modifiers = new EnumMap<>(ItemModifiers.class);
    private final EnumMap<ItemModifiers,Double> calculated = new EnumMap<>(ItemModifiers.class);
    private boolean hasSlot = false;
    private String power = null;
    private int maxUpgrade = 0;
    private int upgrade = 0;
    private int[] color;
    private boolean hasColor;
    public ItemArmor(String id, Material material) {
        super(id, ItemType.ARMOR, material);
    }

    @Override
    public ItemStack toItem() {
        ItemStack ret = generate();
        putBase(ret);
        ret.setAmount(count);
        ItemMeta meta = ret.getItemMeta();
        meta.setDisplayName(generateName());
        meta.setLore(generateLore());
        if (hasColor) {
            LeatherArmorMeta meta1 = (LeatherArmorMeta) meta;
            meta1.setColor(Color.fromRGB(color[0],color[1],color[2]));
        }
        ret.setItemMeta(meta);
        SLItems.stripDefense(ret);
        return ret;
    }

    private void putBase(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer body = meta.getPersistentDataContainer();
        PersistentDataContainer modifiable = body.getAdapterContext().newPersistentDataContainer();

        modifiable.set(key("apply"), PersistentDataType.STRING,"armor");
        modifiable.set(key("max_upgrade"),PersistentDataType.INTEGER,maxUpgrade);
        modifiable.set(key("upgrade"),PersistentDataType.INTEGER,upgrade);
        modifiable.set(key("slot"),PersistentDataType.INTEGER,hasSlot ? 1 : 0);
        modifiable.set(key("power"),PersistentDataType.STRING,power == null ? "NONE" : power);

        PersistentDataContainer base = modifiable.getAdapterContext().newPersistentDataContainer();
        modifiers.keySet().forEach(k -> base.set(key(k.getPath()),PersistentDataType.DOUBLE,modifiers.get(k)));

        modifiable.set(key("base"),PersistentDataType.TAG_CONTAINER,base);

        PersistentDataContainer calculated = modifiable.getAdapterContext().newPersistentDataContainer();
        calculate(calculated);

        modifiable.set(key("calculated"),PersistentDataType.TAG_CONTAINER,calculated);

        body.set(key("modifiable"),PersistentDataType.TAG_CONTAINER,modifiable);

        item.setItemMeta(meta);
    }

    private void calculate(PersistentDataContainer cont) {
        for (ItemModifiers v : modifiers.keySet()) {
            switch (v) {
                case HEALTH -> {
                    double hp = modifiers.get(v);
                    double newHp = hp + upgrade *Math.min(0.05*hp,0.25) + upgrade *0.05*hp;
                    newHp = Math.round(newHp*100)/100D;
                    cont.set(key(v.getPath()),PersistentDataType.DOUBLE,newHp);
                    calculated.put(v,newHp);
                }
                case DEF,MDEF -> {
                    double def = modifiers.get(v);
                    double newDef = def + upgrade *Math.min(0.075*def,2.5) + upgrade *0.075*def;
                    newDef = Math.round(newDef*100)/100D;
                    cont.set(key(v.getPath()),PersistentDataType.DOUBLE,newDef);
                    calculated.put(v,newDef);
                }
                default -> {
                    cont.set(key(v.getPath()), PersistentDataType.DOUBLE, modifiers.get(v));
                    calculated.put(v,modifiers.get(v));
                }
            }
        }
        // TODO UPGRADE SCHEME FOR ARMORS
        /*
        Power Stone
         */
    }

    @Override
    public String generateName() {
        String name = "&3" + getName() + (upgrade > 0 ? ("&6 [&e+" + upgrade + "&6]") : "");
        return color(name);
    }

    @Override
    public List<String> generateLore() {
        List<String> lore = new ArrayList<>();

        lore.add("&8Armors");

        for (ItemModifiers v : ItemModifiers.values()) {
            if (!modifiers.containsKey(v)) continue;
            double base = modifiers.get(v);
            StringBuilder sb = new StringBuilder(v.getDisplay());
            sb.insert(2,"• ");
            sb.append(": &7+");
            switch (v) {
                case STR,CRIT,INT,VIT -> sb.append(Math.round(base));
                case CRITICAL_RATE -> sb.append(Math.round(10000*base)/100D).append("%");
                default -> sb.append(Math.round(100*base)/100D);
            }
            double val = calculated.get(v);
            if (val != base) {
                sb.append(" &6[&e+");
                double add = val - base;
                switch (v) {
                    case STR,CRIT,INT,VIT -> sb.append(Math.round(add)).append("&6]");
                    case CRITICAL_RATE -> sb.append(Math.round(10000*add)/100D).append("%&6]");
                    default -> sb.append(Math.round(100*add)/100D).append("&6]");
                }
            }
            lore.add(sb.toString());
        }
        if (!getLore().isEmpty()) {
            lore.add("");
            getLore().forEach(s -> lore.add("&7" + s));
        }

        lore.add("");

        if (maxUpgrade == 0) lore.add("&cThis item cannot be upgraded");
        else {
            String color = upgrade == 0 ? "&7" : "&e";
            lore.add("&9Upgrade: " + (upgrade == maxUpgrade ? "&6MAXED" : color + upgrade + "&6/" + maxUpgrade));
        }

        if (hasSlot) {
            lore.add("");
            lore.add("&5Power Stone: " + (power == null ? "&8Empty" : "&d" + power));
        }

        return color(lore);
    }

    @Override
    public void readModifiableData(@NotNull ConfigurationSection config) {
        for (ItemModifiers v : ItemModifiers.values()) {
            switch (v) {
                case DAMAGE, MAGIC_DAMAGE, PROJECTILE_DAMAGE -> {
                    continue;
                }
            }
            if (!config.contains(v.getPath())) continue;
            double val = config.getDouble(v.getPath());
            modifiers.put(v,val);
        }
        maxUpgrade = config.getInt("max_upgrade",0);
    }

    public void readArmorData(@NotNull ConfigurationSection config) {
        if (config.contains("color")) {
            List<Integer> color = config.getIntegerList("color");
            this.color = new int[]{0, 0, 0};
            for (int i = 0; i < 3; i++) this.color[i] = color.get(i);
            hasColor = true;
        }
    }

    public EnumMap<ItemModifiers, Double> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean hasSlot() {
        return hasSlot;
    }

    @Override
    public void setSlotted(boolean hasSlot) {
        this.hasSlot = hasSlot;
    }

    @Override
    public String getPowerStone() {
        return power;
    }

    @Override
    public void setPowerStone(String powerStone) {
        this.power = powerStone;
    }

    @Override
    public int getUpgradeLevel() {
        return upgrade;
    }

    @Override
    public void setUpgradeLevel(int upgrade) {
        this.upgrade = upgrade;
    }

    @Override
    public int getMaxUpgradeLevel() {
        return maxUpgrade;
    }
}

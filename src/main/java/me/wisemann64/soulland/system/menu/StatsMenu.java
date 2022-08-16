package me.wisemann64.soulland.system.menu;

import me.wisemann64.soulland.system.players.SLPlayer;
import me.wisemann64.soulland.system.players.Stats;
import me.wisemann64.soulland.system.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

import static me.wisemann64.soulland.system.util.Utils.color;

public class StatsMenu extends Menu {
    public StatsMenu(SLPlayer owner, Menu from) {
        super(owner, from);
    }

    @Override
    protected Inventory createInventory() {
        Inventory stats = Bukkit.createInventory(this,54,"Player Stats");
        int[] panes = {0,1,2,3,4,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,50,51,52,53};
        for (int i : panes) stats.setItem(i,Menu.PANE);

        stats.setItem(10,grabStats(Stats.ATK));
        stats.setItem(11,grabStats(Stats.RATK));
        stats.setItem(12,grabStats(Stats.MATK));
        stats.setItem(13,grabStats(Stats.MAX_HEALTH));
        stats.setItem(14,grabStats(Stats.MAX_MANA));
        stats.setItem(15,grabStats(Stats.DEF));
        stats.setItem(16,grabStats(Stats.MDEF));
        stats.setItem(19,grabStats(Stats.CRIT_DAMAGE));
        stats.setItem(20,grabStats(Stats.CRIT_RATE));
        stats.setItem(21,grabStats(Stats.PEN));
        stats.setItem(22,grabStats(Stats.MPEN));

        stats.setItem(48,Menu.BACK);
        stats.setItem(49,Menu.CLOSE);
        return stats;
    }

    @Override
    public void onClick(InventoryClickEvent clickEvent) {
        clickEvent.setCancelled(true);
        if (getInventory().equals(clickEvent.getClickedInventory())) {
            ItemStack click = clickEvent.getCurrentItem();
            if (click == null) return;
            if (click.equals(Menu.BACK)) back();
            if (click.equals(Menu.CLOSE)) close();
        }
    }

    private ItemStack grabStats(Stats stats) {
        switch (stats) {
            case MATK,MAX_MANA,MPEN -> {
                if (!getOwner().isMagicUnlocked()) return Menu.NOT_YET_UNLOCKED;
            }
        }
        Material mat = switch (stats) {
            case MAX_HEALTH -> Material.GOLDEN_APPLE;
            case MAX_MANA -> Material.LAPIS_LAZULI;
            case ATK -> Material.IRON_SWORD;
            case MATK -> Material.BLAZE_ROD;
            case CRIT_RATE -> Material.FLINT;
            case CRIT_DAMAGE -> Material.GUNPOWDER;
            case DEF -> Material.IRON_CHESTPLATE;
            case MDEF -> Material.TOTEM_OF_UNDYING;
            case PEN -> Material.ARROW;
            case MPEN -> Material.SPECTRAL_ARROW;
            case RATK -> Material.BOW;
            default -> Material.STONE;
        };
        ItemStack ret = new ItemStack(mat);
        ItemMeta meta = ret.getItemMeta();
        String title = switch (stats) {
            case MAX_HEALTH -> "&c";
            case MAX_MANA -> "&b";
            case ATK -> "&4";
            case MATK -> "&3";
            case RATK -> "&2";
            case CRIT_RATE, CRIT_DAMAGE -> "&9";
            case DEF -> "&a";
            case MDEF -> "&d";
            case PEN -> "&8";
            case MPEN -> "&5";
            default -> "&f";
        } + stats.DISPLAY + " &f";
        String stat = number(stats);
        meta.setDisplayName(Utils.color(title + stat));
        List<String> lore = switch (stats) {
            case MAX_HEALTH -> List.of("&7Measures how much damage","&7you can take before dying.");
            case MAX_MANA -> List.of("&7Measures how many magic you","&7can use for a short period.");
            case ATK -> List.of("&7Your attacks will become","&7more powerful when your","&7ATK increases.");
            case RATK -> List.of("&7Your arrow attacks will become","&7more powerful when your","&7ATK increases.");
            case MATK -> List.of("&7Your magic attacks will","&7become more powerful when","&7your MATK increases.");
            case CRIT_RATE -> List.of("&7Chance of your attack","&7become critical attack,","&7dealing extra damage.");
            case CRIT_DAMAGE -> List.of("&7Extra damage when critical","&7attack is dealt.");
            case DEF -> List.of("&7Gives protection that","&7reduces physical damage taken.");
            case MDEF -> List.of("&7Gives protection that","&7reduces magic damage taken.");
            case PEN -> List.of("&7Reduces opponent DEF when","&7using physical attack.");
            case MPEN -> List.of("&7Reduces opponent MDEF when","&7using magic attack.");
            default -> Collections.emptyList();
        };
        meta.setLore(Utils.color(lore));
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_DYE);
        switch (stats) {
            case ATK,MATK,MAX_HEALTH,MAX_MANA,MDEF,MPEN -> meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,10,true);
        }
        ret.setItemMeta(meta);
        return ret;
    }

    private String number(Stats stats) {
        switch (stats) {
            case STR,CRIT,VIT,INT -> {
                return "";
            }
            case CRIT_RATE -> {
                double s = getOwner().getAttributes().getStats(Stats.CRIT_RATE);
                s = Math.round(s*1000)/10D;
                return s + "%";
            }
            default -> {
                double s = getOwner().getAttributes().getStats(stats);
                s = Math.round(s*100)/100D;
                return String.valueOf(s);
            }
        }
    }
}

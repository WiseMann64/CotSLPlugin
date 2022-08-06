package me.wisemann64.soulland.commands;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.Utils;
import me.wisemann64.soulland.items.ItemAbstract;
import me.wisemann64.soulland.items.ItemWeapon;
import me.wisemann64.soulland.items.SLItems;
import me.wisemann64.soulland.players.SLPlayer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestCommand implements TabExecutor {

    private String[] args = {"item","ping","item2","item3","slot"};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) return false;
        if (!(commandSender instanceof Player pl)) return false;
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(pl);
        if (p == null) return false;
        switch (strings[0]) {
            case "item" -> {
                ItemStack stone = new ItemStack(Material.STONE);
                ItemMeta meta = stone.getItemMeta();
                PersistentDataContainer stoneData = meta.getPersistentDataContainer();
                stoneData.set(SLItems.key("id"), PersistentDataType.STRING,"batu_ngab");
                PersistentDataContainer two = stoneData.getAdapterContext().newPersistentDataContainer();
                two.set(SLItems.key("ngab"),PersistentDataType.STRING,"1");
                two.set(SLItems.key("ngabs"),PersistentDataType.STRING,"2");
                stoneData.set(SLItems.key("bego"), PersistentDataType.TAG_CONTAINER,two);
                stone.setItemMeta(meta);
                pl.getInventory().addItem(stone);
            }
            case "ping" -> p.sendMessage(String.valueOf(p.getHandle().getPing()));
            case "item2" -> {
                ItemStack stone = new ItemStack(Material.STONE);
                ItemMeta meta = stone.getItemMeta();
                meta.setDisplayName(Utils.color("&3Lorem Ipsum"));
                List<String> lore = new ArrayList<>();
                lore.add("&8Melee Weapon");
                lore.add("&c• Damage: &75.5");
                lore.add("&c• Critical Rate: &7+25%");
                lore.add("&c• Critical Damage: &7+50");
                lore.add("&c• Physical PEN: &7+40");
                lore.add("&d• Mana: &7+400");
                lore.add("&d• Magic PEN: &7+69");
                lore.add("&d• Magic Defense: &7+230");
                lore.add("&a• Health: &7+40");
                lore.add("&a• Defense: &7+69");
                lore.add("&6• Vitality: &7+5");
                lore.add("&6• Intelligence: &7+5");
                lore.add("&6• Strength: &7+5");
                lore.add("&6• Critical: &7+5");
                lore.add("");
                lore.add("&7Lorem ipsum dolor sit amet, consectetuer");
                lore.add("&7adipiscing elit, sed diam nonummy nibh");
                lore.add("&7euismod tincidunt ut laoreet dolore magna");
                lore.add("");
                lore.add("&9Upgrade: &70&c/&616");
                lore.add("&9Rarity: &6Legendary");
                lore.add("");
                lore.add("&bPower Stone #1: &7Lorem Ipsum");
                lore.add("&bPower Stone #2: &7Lorem Ipsum");
                meta.setLore(Utils.color(lore));
                stone.setItemMeta(meta);
                pl.getInventory().addItem(stone);
            }
            case "item3" -> {
                ItemStack i = pl.getInventory().getItemInMainHand();
                pl.getInventory().addItem(ItemAbstract.fromItem(i).toItem());
            }
            case "slot" -> {
                ItemStack i = pl.getInventory().getItemInMainHand();
                ItemAbstract j = ItemAbstract.fromItem(i);
                if (j instanceof ItemWeapon k) {
                    k.setSlotted(true);
                    k.setUpgradeLevel(7);
                }
                pl.getInventory().setItemInMainHand(j.toItem());
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) return Arrays.asList(args);
        return null;
    }
}

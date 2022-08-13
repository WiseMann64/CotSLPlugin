package me.wisemann64.soulland.commands;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.items.ItemAbstract;
import me.wisemann64.soulland.items.ItemModifiable;
import me.wisemann64.soulland.players.SLPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemCommand implements TabExecutor {

    private final Set<String> itemIds = SoulLand.getItemManager().getItemIds();
    private final String[] args = {"get","modify"};
    private final String[] args$modify = {"slot","upgrade",/*"power"*/};

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player pl)) return false;
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(pl);
        if (p == null) return false;
        if (args.length == 2 && args[0].equals("get")) {
            String id = args[1];
            ItemAbstract i = SoulLand.getItemManager().getItem(id);
            if (i == null) {
                p.sendMessage("&cNo such item id '" + id + "'.");
                return false;
            }
            p.getHandle().getInventory().addItem(i.toItem());
        }
        if (args.length == 3 && args[0].equals("modify")) {
            String a2 = args[2];
            ItemAbstract i = ItemAbstract.fromItem(pl.getInventory().getItemInMainHand());
            if (!(i instanceof ItemModifiable im)) {
                p.sendMessage("&cYou cannot modify this item");
                return false;
            }
            switch (args[1]) {
                case "slot" -> {
                    boolean b = Boolean.parseBoolean(a2);
                    im.setSlotted(b);
                    pl.getInventory().setItemInMainHand(i.toItem());
                    return false;
                }
                case "upgrade" -> {
                    int val;
                    try {
                        val = Integer.parseInt(a2);
                    } catch (NumberFormatException e) {
                        p.sendMessage("&cInvalid number!");
                        return false;
                    }
                    int max = im.getMaxUpgradeLevel();
                    im.setUpgradeLevel(Math.min(max,val));
                    pl.getInventory().setItemInMainHand(i.toItem());
                }
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> ret = new ArrayList<>();
        if (args.length == 1) {
            String st = args[0];
            for (String o : this.args) {
                if (StringUtils.containsIgnoreCase(o,st)) ret.add(o);
            }
        }
        if (args.length == 2) {
            String st = args[1];
            switch (args[0]) {
                case "get" -> itemIds.forEach(o -> {
                    if (StringUtils.containsIgnoreCase(o,st)) ret.add(o);
                });
                case "modify" -> {
                    for (String o : args$modify) if (StringUtils.containsIgnoreCase(o,st)) ret.add(o);
                }
            }
        }
        if (args.length == 3) {
            String st = args[2];
            if (args[0].equals("modify") && args[1].equals("slot")) {
                String[] s1 = {"true","false"};
                for (String o : s1) if (StringUtils.containsIgnoreCase(o,st)) ret.add(o);
            }
        }
        return ret;
    }
}

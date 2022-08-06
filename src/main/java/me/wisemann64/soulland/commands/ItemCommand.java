package me.wisemann64.soulland.commands;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.items.ItemAbstract;
import me.wisemann64.soulland.players.SLPlayer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemCommand implements TabExecutor {

    private final Set<String> itemIds = SoulLand.getItemManager().getItemIds();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player pl)) return false;
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(pl);
        if (p == null) return false;
        if (args.length == 1) {
            String id = args[0];
            ItemAbstract i = SoulLand.getItemManager().getItem(id);
            if (i == null) return false;
            p.getHandle().getInventory().addItem(i.toItem());
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            String st = args[0];
            List<String> ret = new ArrayList<>();
            itemIds.forEach(o -> {
                if (StringUtils.containsIgnoreCase(o,st)) ret.add(o);
            });
            return ret;
        }
        return null;
    }
}

package me.wisemann64.soulland.commands;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.items.ItemWeapon;
import me.wisemann64.soulland.players.SLPlayer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TestCommand implements TabExecutor {

    private String[] args = {"item","ping"};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 1) return false;
        if (!(commandSender instanceof Player pl)) return false;
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(pl);
        if (p == null) return false;
        switch (strings[0]) {
            case "item" -> {
                p.getHandle().getInventory().addItem(new ItemWeapon("ngab", Material.WOODEN_AXE).toItem());
                return false;
            }
            case "ping" -> {
                p.sendMessage(String.valueOf(p.getHandle().getPing()));
                return false;
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

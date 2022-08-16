package me.wisemann64.soulland.system.commands;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DemoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        SoulLand.getGameManager().setState(GameState.TESTING);
        return false;
    }
}

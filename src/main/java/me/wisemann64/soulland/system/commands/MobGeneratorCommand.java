package me.wisemann64.soulland.system.commands;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.mobs.MobGeneric;
import me.wisemann64.soulland.system.mobs.MobGenericTypes;
import me.wisemann64.soulland.system.players.SLPlayer;
import me.wisemann64.soulland.system.players.Stats;
import me.wisemann64.soulland.system.util.CommandParser;
import me.wisemann64.soulland.system.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MobGeneratorCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender send, @NotNull Command command, @NotNull String s, @NotNull String[] arg) {
        if (!(send instanceof Player pl)) return false;
        SLPlayer p = SoulLand.getPlayerManager().getPlayer(pl);
        if (p == null) return false;
        if (arg.length < 1) return false;
        MobGenericTypes t;
        try {
            t = MobGenericTypes.valueOf(arg[0]);
        } catch (IllegalArgumentException e) {
            p.sendMessage("&cUnknown mob type '" + arg[0] + "'");
            return false;
        }
        MobGeneric.Customizer c = new MobGeneric.Customizer(t);
        c.setName(Utils.titleCase(t));

        if (arg.length > 1) {
            String args = CommandParser.appender(Arrays.copyOfRange(arg,1,arg.length));
            Map<String,String> args1 = CommandParser.parse(args);
            if (args1.containsKey("n")) c.setName(args1.get("n"));
            if (args1.containsKey("id")) c.setId(args1.get("id"));
            if (args1.containsKey("l")) c.setLevel(Integer.parseInt(args1.get("l")));
            if (args1.containsKey("xp")) c.setXp(Integer.parseInt(args1.get("xp")));
            if (args1.containsKey("ep")) c.setExplosionPower(Double.parseDouble(args1.get("ep")));
            EnumMap<Stats, Double> stats = c.getInitStats();
            if (args1.containsKey("hp")) stats.put(Stats.HEALTH,Double.parseDouble(args1.get("hp")));
            if (args1.containsKey("atk")) stats.put(Stats.ATK,Double.parseDouble(args1.get("atk")));
            if (args1.containsKey("matk")) stats.put(Stats.MATK,Double.parseDouble(args1.get("matk")));
            if (args1.containsKey("def")) stats.put(Stats.DEF,Double.parseDouble(args1.get("def")));
            if (args1.containsKey("mdef")) stats.put(Stats.MDEF,Double.parseDouble(args1.get("mdef")));
            if (args1.containsKey("pen")) stats.put(Stats.PEN,Double.parseDouble(args1.get("pen")));
            if (args1.containsKey("mpen")) stats.put(Stats.MPEN,Double.parseDouble(args1.get("mpen")));
        }

        Location l = p.getLocation();
        new MobGeneric(l.getWorld(),c).spawn(l);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] arg) {
        List<String> r = new ArrayList<>();
        if (arg.length == 1) for (MobGenericTypes v : MobGenericTypes.values()) if (v.toString().toLowerCase().contains(arg[0].toLowerCase())) r.add(v.toString());
        return r;
    }
}

package me.wisemann64.soulland.system.commands;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.players.SLPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeleporterCommand implements TabExecutor {

    private static class Destination {
        private final String world;
        private final double[] xyz;
        private final float[] yp;

        Destination(String world, double[] xyz, float[] yp) {
            this.world = world;
            this.xyz = xyz;
            this.yp = yp;
        }
    }

    private final YamlConfiguration config;
    private final Map<String,Destination> destination = new HashMap<>();

    public TeleporterCommand() {
        File file = new File(SoulLand.getPlugin().getDataFolder().getPath() + "/teleport.yml");
        if (!file.exists()) SoulLand.getPlugin().saveResource("teleport.yml",false);
        config = YamlConfiguration.loadConfiguration(file);
        config.getKeys(false).forEach(key -> {
            ConfigurationSection sect = config.getConfigurationSection(key);
            if (sect != null) {
                double[] i = new double[3];
                float[] j = new float[2];
                String k = sect.getString("world","world");
                i[0] = sect.getDouble("x",0.0);
                i[1] = sect.getDouble("y",0.0);
                i[2] = sect.getDouble("z",0.0);
                j[0] = (float) sect.getDouble("yaw",0.0);
                j[1] = (float) sect.getDouble("pitch",0.0);
                destination.put(key,new Destination(k,i,j));
            }
        });
    }

    public void saveToFile() {
        destination.forEach((k,v) -> {
            config.set(k + ".world",v.world);
            config.set(k + ".x",v.xyz[0]);
            config.set(k + ".y",v.xyz[1]);
            config.set(k + ".z",v.xyz[2]);
            config.set(k + ".yaw",v.yp[0]);
            config.set(k + ".pitch",v.yp[1]);
        });
        try {
            config.save(new File(SoulLand.getPlugin().getDataFolder().getPath() + "/teleport.yml"));
        } catch (IOException e) {
            System.err.println("Failed to save teleport.yml");
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return false;
        SLPlayer pl = SoulLand.getPlayerManager().getPlayer(p);
        if (pl == null) return false;
        if (args.length > 1) {
            switch(args[0]) {
                case "to" -> {
                    Destination d = destination.getOrDefault(args[1],null);
                    if (d == null) return false;
                    World w = Bukkit.getWorld(d.world);
                    if (w == null) return false;
                    Location l = new Location(w,d.xyz[0],d.xyz[1],d.xyz[2],d.yp[0],d.yp[1]);
                    pl.getHandle().teleport(l);
                }
                case "set" -> {
                    if (destination.containsKey(args[1])) return false;
                    Location l = pl.getLocation();
                    String world = l.getWorld().getName();
                    double[] xyz = {l.getX(),l.getY(),l.getZ()};
                    float[] yp = {l.getYaw(),l.getPitch()};
                    Destination d = new Destination(world, xyz, yp);
                    destination.put(args[1],d);
                    saveToFile();
                }
                case "remove" -> {
                    destination.remove(args[1]);
                    saveToFile();
                }
            }
        }
        return true;
    }

    static final String[] a = {"to","set","remove"};

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> ret = new ArrayList<>();
        if (args.length == 1) for (String s1 : a) if (s1.contains(args[0].toLowerCase())) ret.add(s1);
        if (args.length == 2 && (args[0].equals("to") || args[0].equals("remove"))) destination.keySet().forEach(k -> {
            if (k.toLowerCase().contains(args[1].toLowerCase())) ret.add(k);
        });
        return ret;
    }
}
package me.wisemann64.soulland.gameplay.lock;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class LockManager {

    public enum LockType {
        LOCKED, ABSOLUTE, PROMPT;
    }

    private final List<DoorLock> locks = new ArrayList<>();

    public LockManager() {
        SoulLand.getPlugin().saveResource("gameplay/lock.json",true);
        Gson GSON = new Gson();
        JsonObject file = new JsonObject();
        try {
            file = GSON.fromJson(
                    new JsonReader(
                            new FileReader(SoulLand.getPlugin().getDataFolder().getPath() + "/gameplay/lock.json")),JsonObject.class);
        } catch (IOException ex) {
            System.err.println("Failed to load lock.json");
        }
        JsonArray lock = file.get("lock").getAsJsonArray();
        lock.forEach(c -> {
            JsonObject obj = c.getAsJsonObject();
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            int z = obj.get("z").getAsInt();
            World world = Bukkit.getWorld(obj.get("world").getAsString());
            LockType type = LockType.valueOf(obj.get("type").getAsString());
            switch (type) {
                case LOCKED -> {
                    String key = obj.get("key").getAsString();
                    locks.add(new DoorLock(new Location(world,x,y,z),LockType.LOCKED,null,key));
                }
                case ABSOLUTE -> locks.add(new DoorLock(new Location(world,x,y,z),LockType.ABSOLUTE,null,null));
                case PROMPT -> {
                    // TODO, DIKUNCINYA PAS KAPAN
                    Predicate<GameManager> man = gm -> {
                      World w = Bukkit.getWorld("world");
                      if (w == null) return false;
                      long t = w.getTime();
                      return t > 13000 && t < 23000;
                    };
                    locks.add(new DoorLock(new Location(world,x,y,z),LockType.PROMPT,man,null));
                }
            }
        });
    }

    public List<DoorLock> getLocks() {
        return locks;
    }

    public DoorLock getLockByLocation(Location loc) {
        for (DoorLock lock : locks) if (compare(loc,lock.loc())) return lock;
        return null;
    }

    private boolean compare(Location a, Location b) {
        if (!Objects.equals(a.getWorld(), b.getWorld())) return false;
        if (a.getBlockX() != b.getBlockX()) return false;
        if (a.getBlockY() != b.getBlockY()) return false;
        return a.getBlockZ() == b.getBlockZ();
    }
}

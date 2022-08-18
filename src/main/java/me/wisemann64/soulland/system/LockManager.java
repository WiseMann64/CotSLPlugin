package me.wisemann64.soulland.system;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.gameplay.GameManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class LockManager {

    public enum LockType {
        LOCKED, ABSOLUTE, PROMPT;
    }

    public record Lock(String identifier, LockType type, @Nullable String keyId, @Nullable Predicate<GameManager> prompt) {

    }

    private final Map<String,Lock> lock = new HashMap<>();

    public LockManager() {
        SoulLand.getPlugin().saveResource("gameplay/lock.yml",true);
        File file = new File(SoulLand.getPlugin().getDataFolder().getPath() + "/gameplay/lock.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> absolute = config.getStringList("absolute");
        absolute.forEach(key -> lock.put(key,new Lock(key,LockType.ABSOLUTE,null,null)));

        ConfigurationSection lockSection = config.getConfigurationSection("lock");
        lockSection.getKeys(false).forEach(k -> lock.put(k, new Lock(k, LockType.LOCKED, lockSection.getString(k), null)));
    }

    public Lock getLock(String s) {
        return lock.getOrDefault(s,null);
    }
}

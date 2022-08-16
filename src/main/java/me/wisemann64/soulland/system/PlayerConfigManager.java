package me.wisemann64.soulland.system;

import me.wisemann64.soulland.SoulLand;
import me.wisemann64.soulland.system.players.SLPlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerConfigManager {

    public static final File TEMPLATE = new File(SoulLand.getPlugin().getDataFolder().getPath() + "/players/template.yml");

    public static void reloadPlayerConfigTemplate() {
        SoulLand.getPlugin().saveResource("players/template.yml",true);
    }

    public static boolean hasData(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        File file = new File(SoulLand.getPlugin().getDataFolder().getPath() + "/players/" + uuid +".yml");
        return file.exists();
    }

    public static YamlConfiguration getData(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        File file = new File(SoulLand.getPlugin().getDataFolder().getPath() + "/players/" + uuid +".yml");
        return file.exists() ? YamlConfiguration.loadConfiguration(file) : YamlConfiguration.loadConfiguration(TEMPLATE);
    }

    public static File getFile(@NotNull SLPlayer player) {
        UUID uuid = player.getUUID();
        return new File(SoulLand.getPlugin().getDataFolder().getPath() + "/players/" + uuid +".yml");
    }

    public static void saveData(SLPlayer player) {
        UUID uuid = player.getUUID();
        YamlConfiguration yaml = player.getConfig();
        if (yaml == null) return;
        File file = new File(SoulLand.getPlugin().getDataFolder().getPath() + "/players/" + uuid.toString() +".yml");
        try {
            yaml.save(file);
        } catch (IOException e) {
            System.err.println(uuid + ".yml didn't save!" );
        }
    }
}

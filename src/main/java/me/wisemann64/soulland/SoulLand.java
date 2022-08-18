package me.wisemann64.soulland;

import me.wisemann64.soulland.gameplay.GameManager;
import me.wisemann64.soulland.gameplay.GameState;
import me.wisemann64.soulland.gameplay.objects.ObjectParser;
import me.wisemann64.soulland.system.LockManager;
import me.wisemann64.soulland.system.MobManager;
import me.wisemann64.soulland.system.PlayerConfigManager;
import me.wisemann64.soulland.system.PlayerManager;
import me.wisemann64.soulland.system.combat.CombatEntity;
import me.wisemann64.soulland.system.combat.CombatListeners;
import me.wisemann64.soulland.system.commands.*;
import me.wisemann64.soulland.system.items.ItemManager;
import me.wisemann64.soulland.system.listeners.InventoryClick;
import me.wisemann64.soulland.system.listeners.MobListeners;
import me.wisemann64.soulland.system.listeners.PlayerListeners;
import me.wisemann64.soulland.system.players.SLPlayer;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class SoulLand extends JavaPlugin {

    private static SoulLand plugin;
    private static PlayerManager playerManager;
    private static MobManager mobManager;
    private static ItemManager itemManager;
    private static GameManager gameManager;
    private static LockManager lockManager;
    private static ObjectParser objectParser;

    private BukkitRunnable pluginTick;

    private final static List<BukkitTask> asyncTasks = new ArrayList<>();
    @Override
    public void onEnable() {
        plugin = this;
        itemManager = new ItemManager();

        PlayerConfigManager.reloadPlayerConfigTemplate();

        playerManager = new PlayerManager();
        getServer().getPluginManager().registerEvents(playerManager, this);
        runTick();

        mobManager = new MobManager();

        // Commands registration
        getCommand("test").setExecutor(new TestCommand());
        getCommand("item").setExecutor(new ItemCommand());
        getCommand("mob").setExecutor(new MobGeneratorCommand());
        getCommand("sltp").setExecutor(new TeleporterCommand());
        getCommand("demo").setExecutor(new DemoCommand());

        // Events Registration
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(),this);
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new CombatListeners(), this);
        pm.registerEvents(new MobListeners(),this);

        gameManager = new GameManager();
        gameManager.setState(GameState.DEVELOPMENT);

        lockManager = new LockManager();
        objectParser = new ObjectParser();
    }

    @Override
    public void onDisable() {
        playerManager.getPlayers().forEach(SLPlayer::logout);
        stopTick();
    }

    private void runTick() {
        pluginTick = new BukkitRunnable() {
            @Override
            public void run() {
                pluginTick();
            }
        };
        pluginTick.runTaskTimer(this,0,1);
    }

    private void stopTick() {
        try {
            pluginTick.cancel();
        } catch (IllegalStateException ignored) {

        }
    }

    private void pluginTick() {
        gameManager.mainTick();
    }

    public static SoulLand getPlugin() {
        return plugin;
    }
    public static GameManager getGameManager() {
        return gameManager;
    }
    public static PlayerManager getPlayerManager() {
        return playerManager;
    }
    public static ItemManager getItemManager() {
        return itemManager;
    }
    public static MobManager getMobManager() {
        return mobManager;
    }
    public static List<BukkitTask> getAsyncTasks() {
        return asyncTasks;
    }
    public static CombatEntity getCombatEntity(UUID uuid) {
        SLPlayer a = playerManager.getPlayer(uuid);
        if (a != null) return a;
        return mobManager.getMob(uuid);
    }

    public static LockManager getLockManager() {
        return lockManager;
    }

    public static ObjectParser getObjectParser() {
        return objectParser;
    }

    public static void startDemo() {
        gameManager.setState(GameState.TESTING);
    }
}

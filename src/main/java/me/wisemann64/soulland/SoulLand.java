package me.wisemann64.soulland;

import me.wisemann64.soulland.combat.CombatEntity;
import me.wisemann64.soulland.combat.CombatListeners;
import me.wisemann64.soulland.commands.ItemCommand;
import me.wisemann64.soulland.commands.TestCommand;
import me.wisemann64.soulland.items.ItemManager;
import me.wisemann64.soulland.listeners.InventoryClick;
import me.wisemann64.soulland.listeners.MobListeners;
import me.wisemann64.soulland.listeners.PlayerListeners;
import me.wisemann64.soulland.mobs.SLMob;
import me.wisemann64.soulland.players.SLPlayer;
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

    private GameManager gameManager;
    private BukkitRunnable pluginTick;

    private final static List<BukkitTask> asyncTasks = new ArrayList<>();
    @Override
    public void onEnable() {
        plugin = this;
        itemManager = new ItemManager();

        PlayerConfigManager.reloadPlayerConfigTemplate();

        playerManager = new PlayerManager();
        getServer().getPluginManager().registerEvents(playerManager, this);
        gameManager = new GameManager(this);
        runTick();

        mobManager = new MobManager();

        // Commands registration
        getCommand("test").setExecutor(new TestCommand());
        getCommand("item").setExecutor(new ItemCommand());

        // Events Registration
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(),this);
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new CombatListeners(), this);
        pm.registerEvents(new MobListeners(),this);
    }

    @Override
    public void onDisable() {
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

    }

    public static SoulLand getPlugin() {
        return plugin;
    }
    public GameManager getGameManager() {
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
}

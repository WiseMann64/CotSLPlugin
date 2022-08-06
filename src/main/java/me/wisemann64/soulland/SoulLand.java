package me.wisemann64.soulland;

import me.wisemann64.soulland.commands.TestCommand;
import me.wisemann64.soulland.items.ItemManager;
import me.wisemann64.soulland.listeners.InventoryClick;
import me.wisemann64.soulland.listeners.PlayerListeners;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class SoulLand extends JavaPlugin {

    private static SoulLand plugin;
    private static PlayerManager playerManager;
    private static ItemManager itemManager;

    private GameManager gameManager;
    private BukkitRunnable pluginTick;


    @Override
    public void onEnable() {
        plugin = this;
        itemManager = new ItemManager();

        PlayerConfigManager.reloadPlayerConfigTemplate();

        playerManager = new PlayerManager();
        getServer().getPluginManager().registerEvents(playerManager, this);
        gameManager = new GameManager(this);
        runTick();

        // Commands registration
        getCommand("test").setExecutor(new TestCommand());

        // Events Registration
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryClick(),this);
        pm.registerEvents(new PlayerListeners(), this);
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
}

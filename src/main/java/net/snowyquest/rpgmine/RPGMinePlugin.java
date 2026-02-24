package net.snowyquest.rpgmine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import de.obey.crown.core.data.plugin.Messanger;
import de.obey.crown.core.event.CoreStartEvent;

public final class RPGMinePlugin extends JavaPlugin implements Listener { 

    private ExecutorService executorService;
    private PluginConfig pluginConfig;
    private Messanger messanger;
    private BlockRefillManager blockRefillManager;

    @Override
    public void onEnable() {
        if (this.getServer().getPluginManager().getPlugin("CrownCore") == null) {
            getLogger().warning("[!] Please install the CrownCore.");
            return;
        }

        this.executorService = Executors.newCachedThreadPool();
        this.pluginConfig = new PluginConfig(this);
        this.messanger = this.pluginConfig.getMessanger();
        this.blockRefillManager = new BlockRefillManager(pluginConfig);
    }

    @EventHandler
    public void onCoreStart(CoreStartEvent event) {
        Bukkit.getLogger().info("[^] Thank you for using " + this.getName() + " made by Obey!");
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new MineBlockListener(this.pluginConfig, this.blockRefillManager), this);
        MineCommand mineCommand = new MineCommand(this.pluginConfig, this.messanger);
        this.getCommand("mine").setExecutor(mineCommand);
        this.getCommand("mine").setTabCompleter(mineCommand);
        this.blockRefillManager.startTask();
    }

    @Override
    public void onDisable() {
        this.blockRefillManager.refillAllImmediately();
    }

    public static RPGMinePlugin getInstance() {
        return RPGMinePlugin.getPlugin(RPGMinePlugin.class);
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

    public Messanger getMessanger() {
        return this.messanger;
    }
}

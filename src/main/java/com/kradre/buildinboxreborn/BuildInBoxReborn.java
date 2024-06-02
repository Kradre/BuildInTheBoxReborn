package com.kradre.buildinboxreborn;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class BuildInBoxReborn extends JavaPlugin {

    private static BuildInBoxReborn instance;

    @Override
    public void onEnable() {
        instance = this;
        File pluginFolder = new File(getDataFolder(), "");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        Bukkit.getPluginManager().registerEvents(new SpecialChestListener(), this);
        Objects.requireNonNull(this.getCommand("bibr")).setExecutor(new SaveRegionCommand());

    }

    public static BuildInBoxReborn getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

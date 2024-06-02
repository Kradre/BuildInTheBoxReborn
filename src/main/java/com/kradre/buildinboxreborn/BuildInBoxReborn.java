package com.kradre.buildinboxreborn;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        File pluginMemory = new File(getDataFolder(), "saves.json");
        if (!pluginMemory.exists()) {
            try (FileWriter file = new FileWriter(pluginMemory)) {
                file.write("[]");  // Writing empty JSON array to the file
                file.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

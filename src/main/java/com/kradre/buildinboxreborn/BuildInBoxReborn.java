package com.kradre.buildinboxreborn;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class BuildInBoxReborn extends JavaPlugin {
    private static BuildInBoxReborn instance;

    @Override
    public void onEnable() {
        instance = this;

        Path pluginFolder = Paths.get(getDataFolder().getAbsolutePath());
        if (Files.notExists(pluginFolder)) {
            try {
                Files.createDirectories(pluginFolder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path coordsFolder = Paths.get(getDataFolder().getAbsolutePath() + "/locations/");
        if (Files.notExists(coordsFolder)) {
            try {
                Files.createDirectories(coordsFolder);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Path pluginMemory = pluginFolder.resolve("saves.json");
        if (Files.notExists(pluginMemory)) {
            try {
                Files.writeString(pluginMemory, "[]");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        World schematicWorld = Bukkit.getServer().getWorld("schematicWorld");
        if (schematicWorld == null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv create schematicWorld normal -t flat -g VoidGen");
            schematicWorld = Bukkit.getServer().getWorld("schematicWorld");
        }

        Bukkit.getPluginManager().registerEvents(new SpecialChestListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestClickListener(), this);
        Objects.requireNonNull(this.getCommand("bibr")).setExecutor(new SaveRegionCommand());
        Objects.requireNonNull(this.getCommand("bibr2")).setExecutor(new CreateNBTItem());
    }

    public static BuildInBoxReborn getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
    }
}

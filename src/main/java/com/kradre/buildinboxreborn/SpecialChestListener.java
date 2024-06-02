package com.kradre.buildinboxreborn;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class SpecialChestListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType() == Material.CHEST) {
            Player player = event.getPlayer();
            ItemStack item = event.getItemInHand();
            if(item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
                String schematicName = item.getItemMeta().getDisplayName();
                File schematicFile = new File(BuildInBoxReborn.getInstance().getDataFolder(), schematicName + ".schem");
                if(schematicFile.exists()) {
                    editSchematic(block, schematicFile, player);
                    block.setType(Material.AIR);
                }
            }
        }
    }

    private void placeBlockSafely(com.sk89q.worldedit.world.World world, BlockVector3 position, BlockStateHolder block,Player player) {
        Bukkit.getScheduler().callSyncMethod(BuildInBoxReborn.getInstance(),
                () -> {
                    org.bukkit.World bukkitWorld = Bukkit.getServer().getWorld(world.getName());
                    int x = position.getBlockX();
                    int y = position.getBlockY();
                    int z = position.getBlockZ();
                    bukkitWorld.getBlockAt(x, y, z).setBlockData(BukkitAdapter.adapt(block).clone());
                    player.sendMessage(ChatColor.GRAY + "Placing block at X:" + x + " Y:" + y + " Z:" + z);
                    return null;
                });
    }

    public void editSchematic(Block block, File schematicFile, Player player) {
        BuiltInClipboardFormat format = BuiltInClipboardFormat.SPONGE_SCHEMATIC;
        try (ClipboardReader reader = format.getReader(Files.newInputStream(schematicFile.toPath()))) {
            Clipboard clipboard = reader.read();
            BlockVector3 minPoint = clipboard.getMinimumPoint();
            com.sk89q.worldedit.world.World worldEditWorld = BukkitAdapter.adapt(player.getWorld());
            BlockVector3 chestLocation = BukkitAdapter.asBlockVector(block.getLocation());
            int delay = 0;
            for (BlockVector3 point : clipboard.getRegion()) {
                BlockVector3 placePosition = point.subtract(minPoint).add(chestLocation);
                BlockStateHolder blockHolder = clipboard.getBlock(point);
                Bukkit.getScheduler().runTaskLater(BuildInBoxReborn.getInstance(),
                        () -> placeBlockSafely(worldEditWorld, placePosition, blockHolder,player), delay++);
            }
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Error loading schematic: " + e.getMessage());
        }
    }
}
package com.kradre.buildinboxreborn;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
                    block.setType(Material.BRICK_STAIRS);
                }
            }
        }
    }

    public void editSchematic(Block block, File schematicFile, Player player) {
        BuiltInClipboardFormat format = BuiltInClipboardFormat.SPONGE_SCHEMATIC;
        try (ClipboardReader reader = format.getReader(Files.newInputStream(schematicFile.toPath()))) {
            Clipboard clipboard = reader.read();
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(block.getWorld()), -1);
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(block.getX(), block.getY(), block.getZ())) // fixed this line
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
            editSession.flushSession();
            player.sendMessage(ChatColor.GREEN + "Unpacked space.");
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Error loading schematic: " + e.getMessage());
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }
    }
}
package com.kradre.buildinboxreborn;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.visitor.RegionVisitor;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class SaveRegionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getName());
        try {
            Region region = session.getSelection(BukkitAdapter.adapt(player.getWorld()));
            if (region != null) {
                if (args.length == 0) {
                    player.sendMessage(ChatColor.YELLOW + "Please, provide the name for selection");
                } else {
                    BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
                    try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(player.getWorld()), -1)) {
                        ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
                        Operations.completeLegacy(copy);
                    } catch(Exception ex) {
                        player.sendMessage(ChatColor.RED + "An error occurred while copying the region: " + ex.getMessage());
                        return false;
                    }

                    File schematicFile = new File(BuildInBoxReborn.getInstance().getDataFolder(), Math.abs(args[0].hashCode()) + ".schem");
                    BuiltInClipboardFormat format = BuiltInClipboardFormat.SPONGE_SCHEMATIC;
                    try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(schematicFile)); ClipboardWriter writer = format.getWriter(stream)) {
                        writer.write(clipboard);
                    }

                    ItemStack chest = new ItemStack(Material.CHEST);
                    ItemMeta meta = chest.getItemMeta();
                    meta.setDisplayName(args[0]);
                    meta.setCustomModelData(Math.abs(args[0].hashCode()));
                    meta.setLore(Arrays.asList(ChatColor.GRAY + "Place on ground to open the black hole chest"));
                    chest.setItemMeta(meta);
                    player.getInventory().addItem(chest);
                    player.sendMessage(ChatColor.GREEN + "Added '" + args[0] + "' black hole chest in your inventory." + ChatColor.RED + " Place it down to unravel the depths.");
                    ClipboardFormat formatExtra = ClipboardFormats.findByFile(schematicFile);
                    World schematicWorld = Bukkit.getServer().getWorld("schematicWorld");
                    long seed = player.getName().hashCode() ^ args[0].hashCode();
                    Random rnd = new Random(seed);
                    int x = rnd.nextInt(1000);
                    int z = rnd.nextInt(1000);
                    int y = 0;
                    player.sendMessage(ChatColor.GREEN + "Coords: [" + x + ";" + z + ";" + y + "]");
                    try (ClipboardReader reader = formatExtra.getReader(new FileInputStream(schematicFile))) {
                        Clipboard clipboard2 = reader.read();

                        try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(schematicWorld), -1)) {
                            Operation operation = new ClipboardHolder(clipboard2)
                                    .createPaste(editSession)
                                    .to(BlockVector3.at(x, y, z))
                                    .ignoreAirBlocks(false)
                                    .build();
                            Operations.complete(operation);
                        } catch (WorldEditException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "You must first select a region");
            }
        } catch (IOException | IncompleteRegionException e) {
            player.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
        }
        return true;
    }
}
package com.kradre.buildinboxreborn;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class CreateNBTItem implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        World schematicWorld = Bukkit.getServer().getWorld("schematicWorld");

        String schematicName = args[0];
        Player player = (Player) sender;
        long seed = player.getName().hashCode() ^ schematicName.hashCode();
        Random rnd = new Random(seed);
        int x = rnd.nextInt(1000);
        int z = rnd.nextInt(1000);
        int y = schematicWorld.getHighestBlockYAt(x, z);

        Location teleportLocation = new Location(schematicWorld, x, y, z);
        Location blockPlacement = new Location(schematicWorld,x,y - 1,z);
        Block block = blockPlacement.getBlock();
        block.setType(Material.STONE);

        if (player.getWorld().equals(schematicWorld)) {
            player.teleport(player.getRespawnLocation());
        } else {
            player.teleport(teleportLocation);
        }

        return true;
    }
}

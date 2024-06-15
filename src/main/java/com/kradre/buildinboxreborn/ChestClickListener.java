package com.kradre.buildinboxreborn;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.Random;

public class ChestClickListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (
                item.getType() == Material.CHEST &&
                ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR)) &&
                item.hasItemMeta() &&
                item.getItemMeta().getDisplayName() != null
        ) {
            int schematicMeta = item.getItemMeta().getCustomModelData();
            // check if schematic file exists
            File schematicFile = new File(BuildInBoxReborn.getInstance().getDataFolder(), schematicMeta + ".schem");
            if (!schematicFile.exists()) {
                event.getPlayer().sendMessage("Schematic does not exist.");
                return;
            }

            Player player = (Player) event.getPlayer();

            // if player is in 'schematicWorld', teleport him back and delete stored data
            if(player.getWorld().getName().equals("schematicWorld")){
                Location initialLocation = PlayerLocationManager.getPlayerLocation(player);

                if(initialLocation != null){
                    player.teleport(initialLocation);
                    PlayerLocationManager.deletePlayerLocation(player);
                }

                return;
            } else {
                PlayerLocationManager.storePlayerLocation(player);
                World schematicWorld = Bukkit.getServer().getWorld("schematicWorld");
                long seed = player.getName().hashCode() ^ schematicMeta;
                Random rnd = new Random(seed);
                int x = rnd.nextInt(1000);
                int z = rnd.nextInt(1000);
                int y = 2;
                player.sendMessage(ChatColor.GREEN + "Coords: [" + x + ";" + z + ";" + y + "]");
                Location teleportLocation = new Location(schematicWorld, x, y, z);
                Location blockPlacement = new Location(schematicWorld,x,y - 1,z);
                Block block = blockPlacement.getBlock();
                block.setType(Material.STONE);
                player.teleport(teleportLocation);
            }
        }
    }

    public static class PlayerLocationManager {

        public static void storePlayerLocation(Player player){
            Location location = player.getLocation();

            // Save the 'location' data to a file
            // Using player.getUniqueId().toString() to uniquely identify player's file
            try(FileWriter writer = new FileWriter(BuildInBoxReborn.getInstance().getDataFolder() + "/locations/" + player.getUniqueId().toString())){
                writer.write(location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static Location getPlayerLocation(Player player){
            // Reads the file using player.getUniqueId().toString()
            // Fetches the data, constructs and return Location object

            Location location = null;
            try(BufferedReader reader = new BufferedReader(new FileReader(BuildInBoxReborn.getInstance().getDataFolder() + "/locations/" + player.getUniqueId().toString()))){
                String[] split = reader.readLine().split(":");
                World world = Bukkit.getWorld(split[0]);
                double x = Double.parseDouble(split[1]);
                double y = Double.parseDouble(split[2]);
                double z = Double.parseDouble(split[3]);

                location = new Location(world, x, y, z);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return location;
        }

        public static void deletePlayerLocation(Player player){
            // Deletes the file using player.getUniqueId().toString()
            File file = new File(BuildInBoxReborn.getInstance().getDataFolder() + "/locations/" + player.getUniqueId().toString());
            if(file.exists()){
                file.delete();
            }
        }
    }
}
package com.kradre.buildinboxreborn;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchematicCommands implements CommandExecutor {

    private List<File> schematics;

    public SchematicCommands() {
        // Assuming the schematics are stored in a folder named 'schematics' in the plugin's data folder
        File schematicsFolder = new File(BuildInBoxReborn.getInstance().getDataFolder(),"");
        if (!schematicsFolder.exists() || !schematicsFolder.isDirectory()) {
            return;
        }

        // Get list of schematic files
        schematics = new ArrayList<>();
        for (File file : schematicsFolder.listFiles()) {
            if (file.getName().endsWith(".schema")) {
                schematics.add(file);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("bibr list")) {
            for (int i = 0; i < schematics.size(); i++) {
                // Display the schematics list in chat
                sender.sendMessage((i + 1) + ". " + schematics.get(i).getName());
            }
        } else if (command.getName().equalsIgnoreCase("bibr clone") && sender instanceof Player) {
            if (args.length == 0) {
                sender.sendMessage("You must specify a schematic number!");
                return true;
            }

            int index = Integer.parseInt(args[0]) - 1;  // Convert to zero-based index
            if (index >= 0 && index < schematics.size()) {
                giveSchematicChest((Player) sender, schematics.get(index),index);
                return true;
            } else {
                sender.sendMessage("Invalid schematic number!");
            }
        }

        return false;
    }

    private void giveSchematicChest(Player player, File schematic,Integer index) {
        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();

        // Set the name of the item to imply it contains the schematic
        meta.setDisplayName(index + " Schematic Chest");

        meta.setCustomModelData(Integer.parseInt(schematic.getName()));

        chest.setItemMeta(meta);
        player.getInventory().addItem(chest);
    }
}

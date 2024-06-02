# BuildInBox Reborn

The BuildInBox Reborn is a remake of my favourite plugin I've used to play with on Minecraft Bukkit/Spigot/PaperMc
servers. This is an attempt to rewrite the same functionality it had before it got outdated.

## Requirements

- Maven
- Java 17 at minimum
- Worldedit plugin (included in maven)

This this the first release and so far it's barebones.

## How to use

/saveregion <name> - save the selection from worldedit into the box and place the box in the inventory.
Upon placing the box, it gets replaced with the build what was saved in that selection.

## Known bugs

- If the player makes the chest with the same name as the box, it acts as the same box.
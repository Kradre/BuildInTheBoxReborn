# BuildInBox Reborn

BuildInBox Reborn is a fresh take on one of my favorite plugins used in Minecraft Bukkit/Spigot/PaperMc servers. The
goal of this project is to reintroduce the functionalities of the original plugin to meet current requirements.

## Overview

This is the first release and, for the moment, it is in its early phase (barebones).

## Requirements

Before you start, please ensure you meet the following requirements:

- **Maven:** The project uses Maven for dependency management.
- **Java** (version 17 at minimum): Java is the primary programming language for this project.
- **Worldedit plugin:** Worldedit is necessary for this plugin to operate correctly. It is included in the Maven
  dependencies.

## Usage

Here is how to use the plugin:

1. Save your selection from Worldedit into the box using the command `/saveregion <name>`.
2. Upon executing the command, the selected area will be saved in the box, and the box will be placed in your inventory.
3. When you place the box, it will automatically be replaced with the build saved previously.

## New features:

- Boxes are now kept in place after being placed down. If the user removes that box, the building that was placed in it gets removed as well.
- The boxes have unique uuids that can now only be duplicated by the admin or creative player.

## Known bugs:

-No way to recover a box after its disappearance.

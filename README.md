# SimpleWarp V3

This repository contains the code for the SimpleWarp Minecraft server plugin. SimpleWarp allows you to create, delete and use 
warp points. These points are some sort of waymarks, were the player can teleport.

The plugin has also a position saving feature. Positions are like warp points except that you can't teleport there.
This feature is disabled by default.

The Position system have to be enabled first in your warps.yml config file.

The plugin also features an auto update System. This System downloads the newest version of the plugin automatically if a new version is available from [DevBukkit](https://dev.bukkit.org/).


The plugin now also features a "per-warp-permission System". 
That means if you enable "RequirePermissionForEachWarp" in your Warps.yml Config, every Warp Point gets an individual Permission that follows this scheme: "simplewarp.warp.WARPNAME". 
(You have to replace WARPNAME with the exact name of your warp point). Only players with the correct permissions can see and use the warp points.

[![CircleCI](https://circleci.com/gh/marylieh/SimpleWarpV3/tree/main.svg?style=shield)](https://circleci.com/gh/marylieh/SimpleWarpV3/tree/main)
## Commands

* `/warp <warpname>` - *Teleport you to a warppoint.*
* `/setwarp <warpname>` - *Create a warppoint.*
* `/delwarp <warpname>` - *Delete a warppoint.*
* `/warps` - *List all warppoints*
* `/warpversion` - *Displays current plugin version*
* `/position <positionName | del | list>`
  * `positionName` - *Shows the coordinates of the position. If the position doesn't exist, the position will be set to your current location*
  * `del` - *Remove a position*
  * `list` - *Lists all available positions*

## Permissions

* `simplewarp.warp` - Allows you to use the **/warp** command.
* `simplewarp.delwarp` - Allows you to use the **/delwarp** command.
* `simplewarp.setwarp` - Allows you to use the **/setwarp** command.
* `simplewarp.warps` - Allows you to use the **/warps** command.
* `simplewarp.position` - Allows you to use the **/position** command.
* `simplewarp.position.view` - Allows you to use the **/position \<name>** command.
* `simplewarp.position.create` - Allows you to use the **/position \<name>**
* `simplewarp.position.del` - Allows you to use the **/position del \<name>**
* `simplewarp.position.list` - Allows you to use the **/position list**

*To give the permissions to players you need a Permission System like [LuckPerms](https://luckperms.net/).*

## Planned Features

Feel free to submit your own ideas in the `issues` tab.

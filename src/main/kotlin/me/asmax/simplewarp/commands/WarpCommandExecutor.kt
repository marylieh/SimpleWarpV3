package me.***REMOVED***.simplewarp.commands

import me.***REMOVED***.simplewarp.SimpleWarp
import me.***REMOVED***.simplewarp.utils.Config
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WarpCommandExecutor : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${SimpleWarp.instance.prefix} §4Just a Player can execute this command!")
            return true
        }
        var player: Player = sender

        if (player.hasPermission("simplewarp.warp")) {
            if (args.size == 1) {

                var id = args[0]

                if (Config.getConfig().getString(".Warps.$id") == null) {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cThis warp didn't exists!")
                    return true
                }

                var world = Config.getConfig().getString(".Warps.${id}.World")?.let { Bukkit.getWorld(it) }

                var x = player.location.x
                var y = player.location.y
                var z = player.location.z

                var yaw = player.location.yaw
                var pitch = player.location.pitch

                Config.getConfig().set(".Warps.${id}.World", world)
                Config.getConfig().set(".Warps.${id}.X", x)
                Config.getConfig().set(".Warps.${id}.Y", y)
                Config.getConfig().set(".Warps.${id}.Z", z)

                Config.getConfig().set(".Warps.${id}.Yaw", yaw)
                Config.getConfig().set(".Warps.${id}.Pitch", pitch)

                player.teleport(Location(world, x, y, z, yaw, pitch))
                player.sendMessage("${SimpleWarp.instance.prefix} §aYou have been teleported to §6$id§a!")

            } else {
                player.sendMessage("${SimpleWarp.instance.prefix} §cPlease use: §7/warp <warpname>")
            }
        } else {
            player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
        }
        return true
    }
}
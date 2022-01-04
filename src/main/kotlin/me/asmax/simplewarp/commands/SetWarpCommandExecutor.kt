package me.asmax.simplewarp.commands

import me.asmax.simplewarp.SimpleWarp
import me.asmax.simplewarp.utils.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SetWarpCommandExecutor : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${SimpleWarp.instance.prefix} §4Just a Player can execute this command!")
            return true
        }
        var player: Player = sender

        if (player.hasPermission("simplewarp.setwarp")) {
            if (args.size == 1) {
                var id = args[0]
                var world: String = player.world.name

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

                player.sendMessage("${SimpleWarp.instance.prefix} §aYou successfully set the Warp §6${id}§a!")

                Config.save()
            } else {
                player.sendMessage("${SimpleWarp.instance.prefix} §cPlease use: §7/setwarp <warpname>")
            }
        } else {
            player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
        }

        return true
    }
}
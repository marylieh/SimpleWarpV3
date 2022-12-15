package me.marylieh.simplewarp.commands

import me.marylieh.simplewarp.SimpleWarp
import me.marylieh.simplewarp.utils.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.command.TabExecutor

class SetWarpCommand : TabExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${SimpleWarp.instance.prefix} §4Just a Player can execute this command!")
            return true
        }
        val player: Player = sender

        if (player.hasPermission("simplewarp.setwarp")) {
            if (args.size == 1) {
                val id = args[0]
                val world: String = player.world.name

                val x = player.location.x
                val y = player.location.y
                val z = player.location.z

                val yaw = player.location.yaw
                val pitch = player.location.pitch

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
    
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return ArrayList<String>()
    }
}
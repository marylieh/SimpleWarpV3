package me.marylieh.simplewarp.commands

import me.marylieh.simplewarp.SimpleWarp
import me.marylieh.simplewarp.impl.WarpManager
import me.marylieh.simplewarp.utils.Config
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
        val player: Player = sender

        if (player.hasPermission("simplewarp.setwarp")) {

            if (Config.getConfig().get("PlayerWarpsOnly") == null) {
                Config.getConfig().set("PlayerWarpsOnly", false)
                println("Old Version of Config detected! Setting PlayerWarpsOnly to false!")
                Config.save()
            }

            if (args.size == 1) {
                val id = args[0]
                val world: String = player.world.name

                val x = player.location.x
                val y = player.location.y
                val z = player.location.z

                val yaw = player.location.yaw
                val pitch = player.location.pitch

                WarpManager.createPublicWarp(id, player.uniqueId.toString(), world, x, y, z, yaw, pitch)

                player.sendMessage("${SimpleWarp.instance.prefix} §aYou successfully set the Warp §6${id}§a!")
            } else {
                player.sendMessage("${SimpleWarp.instance.prefix} §cPlease use: §7/setwarp <warpname>")
            }
        } else {
            player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
        }

        return true
    }
}
package me.marylieh.simplewarp.commands

import me.marylieh.simplewarp.SimpleWarp
import me.marylieh.simplewarp.utils.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class DelWarpCommandExecutor : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${SimpleWarp.instance.prefix} §4Just a Player can execute this command!")
            return true
        }
        val player: Player = sender

        if (player.hasPermission("simplewarp.delwarp")) {
            if (args.size == 1) {

                val id = args[0]

                Config.getConfig().set(".Warps.$id", null)
                Config.save()

                player.sendMessage("${SimpleWarp.instance.prefix} §aThe Warp §6 $id §a was successfully deleted!")

            } else {
                player.sendMessage("${SimpleWarp.instance.prefix} §cPleas use: §7/warp <warpname>")
            }
        } else {
            player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
        }
        return true
    }
}
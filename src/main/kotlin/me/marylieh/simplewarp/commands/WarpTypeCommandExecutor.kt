package me.marylieh.simplewarp.commands

import me.marylieh.simplewarp.SimpleWarp
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WarpTypeCommandExecutor : CommandExecutor {
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${SimpleWarp.instance.prefix} §4Just a Player can execute this command!")
            return true
        }
        val player: Player = sender
        
        if (!player.hasPermission("simplewarp.warptype")) {
            player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
            return true
        }
        
        if (args.isEmpty()) {
            player.sendMessage("${SimpleWarp.instance.prefix} §cPlease use: §7/warptype <default | private>")
            return true
        }
        
        if (args.size == 1) {
            if (args[0].equals("private", ignoreCase = true)) {
                player.sendMessage("${SimpleWarp.instance.prefix} §cDo you really want to switch to private warp points? All of your existing Warppoints will be deleted and the server will have to restart. Read more at: <INSERT LINK>. If you still want to switch, use /warptype private confirm")
                return true
            }
        }

        if (args.size == 2) {
            if (args[1].equals("confirm", ignoreCase = true)) {
                player.sendMessage("${SimpleWarp.instance.prefix} §aThe server will now restart and the new configuration will take effect.")
                return true
            }
        }
        return true
    }
}
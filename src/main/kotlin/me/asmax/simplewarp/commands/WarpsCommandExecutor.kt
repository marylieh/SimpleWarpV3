package me.***REMOVED***.simplewarp.commands

import me.***REMOVED***.simplewarp.SimpleWarp
import me.***REMOVED***.simplewarp.utils.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WarpsCommandExecutor : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${SimpleWarp.instance.prefix} §4Just a Player can execute this command!")
            return true
        }
        val player: Player = sender

        if (player.hasPermission("simplewarp.warps")) {
            player.sendMessage("${SimpleWarp.instance.prefix} ${Config.getConfig().getConfigurationSection(".Warps")?.getKeys(false)}")
        } else {
            player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
        }
        return true
    }
}
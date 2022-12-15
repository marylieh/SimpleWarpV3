package me.marylieh.simplewarp.commands

import me.marylieh.simplewarp.SimpleWarp
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

class WarpVersionCommand : TabExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        sender.sendMessage("${SimpleWarp.instance.prefix} §a${SimpleWarp.instance.version}")
        return true
    }
    
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return ArrayList<String>()
    }
}
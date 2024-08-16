package me.marylieh.simplewarp.commands

import me.marylieh.simplewarp.SimpleWarp
import me.marylieh.simplewarp.permissions.PermissionManager
import me.marylieh.simplewarp.utils.Config
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PermissionManagerCommandExecutor : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${SimpleWarp.instance.prefix} §4Just a Player can execute this command!")
            return true
        }
        val player: Player = sender

        if (player.isOp || player.hasPermission("simplewarp.permissionmanager")) {

            if (!Config.getConfig().getBoolean("IntegratedPermissionSystem")) {
                player.sendMessage("${SimpleWarp.instance.prefix} §cThe integrated permission system is globally disabled.")
                return true
            }

            if (args.size > 3 || args.size < 2) {
                player.sendMessage("${SimpleWarp.instance.prefix} §cPlease use: §7/pm <add | remove | list> <player> [permission]")
                return true
            }

            if (args.size != 2 && !args[2].startsWith("simplewarp")) {
                player.sendMessage("${SimpleWarp.instance.prefix} §cThe permission system can only be used on internal permissions starting with 'simplewarp'.")
                return true
            }

            if (Bukkit.getPlayerExact(args[1]) == null) {
                player.sendMessage("${SimpleWarp.instance.prefix} §cThe player ${args[1]} is not online.")
                return true
            }
            val target: Player = Bukkit.getPlayer(args[1])!!

            when (args[0].lowercase()) {
                "add" -> {
                    PermissionManager.setPermission(target, args[2], true)
                    PermissionManager.savePermission(target, args[2])

                    player.sendMessage("${SimpleWarp.instance.prefix} The permission §a${args[2]} §6has been §2added §6to §b${target.name}§6. §7Remember that permissions set by this plugin only works with SimpleWarp.")
                }
                "remove" -> {
                    PermissionManager.setPermission(target, args[2], false)
                    PermissionManager.removeSavedPermission(target, args[2])

                    player.sendMessage("${SimpleWarp.instance.prefix} The permission §a${args[2]} §6has been §4removed §6from §b${target.name}§6.")
                }
                "list" -> {
                    val permissions: List<String> = PermissionManager.getPermissions(target)

                    player.sendMessage("${SimpleWarp.instance.prefix} The player §b${target.name} §6has the following permission: §a$permissions")
                }
                else -> {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cPlease use: §7/pm <add | remove> <player> <permission>")
                }
            }

        } else {
            player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
        }
        return true
    }
}
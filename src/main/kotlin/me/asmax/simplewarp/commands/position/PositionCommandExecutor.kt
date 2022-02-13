package me.asmax.simplewarp.commands.position

import me.asmax.simplewarp.SimpleWarp
import me.asmax.simplewarp.utils.Config
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PositionCommandExecutor : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${SimpleWarp.instance.prefix} §4Just a Player can execute this command!")
        }
        val player: Player = sender as Player

        if (!player.hasPermission("simplewarp.position")) {
            player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
            return true
        }

        if (!Config.getConfig().getBoolean("PositionSystem")) {
            player.sendMessage("${SimpleWarp.instance.prefix} §cThis feature has been disabled by a Network Administrator!")
            return true
        }

        if (args.size == 1 || args.size == 2) {


        when (args[0]) {
            "list" -> {
                if (!player.hasPermission("simplewarp.position.list")) {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
                    return true
                }
                player.sendMessage(
                    "${SimpleWarp.instance.prefix} §7Available §9positions: §b${
                        Config.getConfig().getConfigurationSection("Positions")?.getKeys(false)
                    }"
                )
            }

            "del" -> {
                if (!player.hasPermission("simplewarp.position.del")) {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
                    return true
                }

                if (Config.getConfig().get("Positions.${args[1]}") != null) {

                    Config.getConfig().set("Positions.${args[1]}", null)
                    Config.save()

                    player.sendMessage("${SimpleWarp.instance.prefix} The Position §a${args[1]} §6has been successfully §cdeleted!")
                } else {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cThis position didn't exists.")
                    return true
                }
            }

            else -> {
                val id = args[0]

                if (Config.getConfig().getString("Positions.$id") != null) {

                    if (!player.hasPermission("simplewarp.position.view")) {
                        player.sendMessage("${SimpleWarp.instance.prefix} §cYou do not have the Permission to do that!")
                        return true
                    }

                    val world = Config.getConfig().getString("Positions.${id}.World")

                    val x = Config.getConfig().getInt("Positions.${id}.X")
                    val y = Config.getConfig().getInt("Positions.${id}.Y")
                    val z = Config.getConfig().getInt("Positions.${id}.Z")

                    player.sendMessage("${SimpleWarp.instance.prefix} §9$id §8[§6$x§8, §6$y§8, §6$z§8, §6$world§8]")
                    return true
                }

                if (!player.hasPermission("simplewarp.position.create")) {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cYou do not have the Permission to do that!")
                    return true
                }

                val world = player.world.name

                val x = player.location.blockX
                val y = player.location.blockY
                val z = player.location.blockZ

                Config.getConfig().set("Positions.${id}.World", world)
                Config.getConfig().set("Positions.${id}.X", x)
                Config.getConfig().set("Positions.${id}.Y", y)
                Config.getConfig().set("Positions.${id}.Z", z)

                Bukkit.broadcast(Component.text("${SimpleWarp.instance.prefix} §a$id §7from §a${player.name} §8[§6$x§8, §6$y §8,§6 $z §8,§6 $world§8]"))

                Config.save()
            }
        }
        } else {
            player.sendMessage("${SimpleWarp.instance.prefix} Invalid Argument, please use one of the following arguments: §c/position <list | position | del>")
        }
        return true
    }
}
package me.marylieh.simplewarp.commands

import me.marylieh.simplewarp.SimpleWarp
import me.marylieh.simplewarp.utils.Config
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.command.TabExecutor

class PositionCommand : TabExecutor {

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

        when (args.size){
            1 -> {
                if(args[0]=="list"){
                if (!player.hasPermission("simplewarp.position.list")) {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cYou don't have the permission to do that!")
                }
                player.sendMessage(
                    "${SimpleWarp.instance.prefix} §7Available §9positions: §b${
                        Config.getConfig().getConfigurationSection("Positions")?.getKeys(false)
                    }"
                )
                return true
                }
            }

            2 -> {
            when(args[0]){
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
                }
                return true
            }

            "get" -> {

                if (Config.getConfig().getString("Positions.${args[1]}") != null) {

                    if (!player.hasPermission("simplewarp.position.get")) {
                        player.sendMessage("${SimpleWarp.instance.prefix} §cYou do not have the Permission to do that!")
                        return true
                    }

                    val world = Config.getConfig().getString("Positions.${args[1]}.World")

                    val x = Config.getConfig().getInt("Positions.${args[1]}.X")
                    val y = Config.getConfig().getInt("Positions.${args[1]}.Y")
                    val z = Config.getConfig().getInt("Positions.${args[1]}.Z")

                    player.sendMessage("${SimpleWarp.instance.prefix} §9${args[1]} §8[§6$x§8, §6$y§8, §6$z§8, §6$world§8]")
                } else {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cThis position does not exist!")
                }
                return true
            }

            "set" -> {
                if (!player.hasPermission("simplewarp.position.set")) {
                    player.sendMessage("${SimpleWarp.instance.prefix} §cYou do not have the Permission to do that!")
                    return true
                }

                val world = player.world.name

                val x = player.location.blockX
                val y = player.location.blockY
                val z = player.location.blockZ

                Config.getConfig().set("Positions.${args[1]}.World", world)
                Config.getConfig().set("Positions.${args[1]}.X", x)
                Config.getConfig().set("Positions.${args[1]}.Y", y)
                Config.getConfig().set("Positions.${args[1]}.Z", z)

                Bukkit.broadcast(Component.text("${SimpleWarp.instance.prefix} §a${args[1]} §7from §a${player.name} §8[§6$x§8, §6$y §8,§6 $z §8,§6 $world§8]"))

                Config.save()
                return true
            }
        }
        }}
            player.sendMessage("${SimpleWarp.instance.prefix} Invalid Argument, please use one of the following arguments: §c/position <set | list | get | del> <warpname>")
        return true
    }
    
    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        val list = ArrayList<String>()
        if (sender !is Player) return list
        val player: Player = sender
        if(args.size==1){
            val filtered = arrayOf<String>("set","list","get","del").filter{value -> value.lowercase().startsWith(args[0].lowercase())}
            filtered?.forEach{list.add(it)}
        }
        if((args[0]=="get")&&(args.size==2)){
            if (player.hasPermission("simplewarp.position.list")) {
                val filtered = Config.getConfig().getConfigurationSection(".Positions")?.getKeys(false)?.filter{value -> value.lowercase().startsWith(args[1].lowercase())}
                filtered?.forEach{list.add(it)}
            }
        }
        return list
    }
    }
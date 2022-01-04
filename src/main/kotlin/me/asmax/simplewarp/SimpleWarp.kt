package me.***REMOVED***.simplewarp

import me.***REMOVED***.simplewarp.commands.*
import me.***REMOVED***.simplewarp.utils.Config
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SimpleWarp : JavaPlugin() {

    val prefix = "§6[SimpleWarp]"
    val VERSION = "B-3.2"

    companion object {
        lateinit var instance: SimpleWarp
        private set
    }

    override fun onLoad() {
        Config.Config()
        instance = this
    }

    override fun onEnable() {
        registerCommands()
        registerListener()
    }

    override fun onDisable() {
        Config.save()
    }

    private fun registerCommands() {
        val setWarpCommand = getCommand("setwarp") ?: error("Couldn't get info command! This should not happen!")
        val delWarpCommand = getCommand("delwarp") ?: error("Couldn't get info command! This should not happen!")
        val warpCommand = getCommand("warp") ?: error("Couldn't get info command! This should not happen!")
        val warpsCommand = getCommand("warps") ?: error("Couldn't get info command! This should not happen!")
        val warpVersionCommand = getCommand("warpversion") ?: error("Couldn't get info command! This should not happen!")
        setWarpCommand.setExecutor(SetWarpCommandExecutor())
        delWarpCommand.setExecutor(DelWarpCommandExecutor())
        warpCommand.setExecutor(WarpCommandExecutor())
        warpsCommand.setExecutor(WarpsCommandExecutor())
        warpVersionCommand.setExecutor(WarpVersionCommandExecutor())
    }

    private fun registerListener() {
        val pluginManager = Bukkit.getPluginManager()
    }

}
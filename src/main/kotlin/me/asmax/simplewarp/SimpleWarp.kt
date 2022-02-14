package me.asmax.simplewarp

import me.asmax.simplewarp.commands.*
import me.asmax.simplewarp.commands.position.PositionCommandExecutor
import me.asmax.simplewarp.utils.Config
import me.asmax.simplewarp.utils.Updater
import org.bukkit.plugin.java.JavaPlugin

class SimpleWarp : JavaPlugin() {

    val prefix = "§6[SimpleWarp]"
    val version = "R-3.3"

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
        initConfig()

        val updater = Updater(this, 395393, this.file, Updater.UpdateType.DEFAULT, true)
    }

    override fun onDisable() {
        Config.save()
    }

    private fun registerCommands() {
        val setWarpCommand = getCommand("setwarp") ?: error("Couldn't get setwarp command! This should not happen!")
        val delWarpCommand = getCommand("delwarp") ?: error("Couldn't get delwarp command! This should not happen!")
        val warpCommand = getCommand("warp") ?: error("Couldn't get warp command! This should not happen!")
        val warpsCommand = getCommand("warps") ?: error("Couldn't get warps command! This should not happen!")
        val warpVersionCommand = getCommand("warpversion") ?: error("Couldn't get warpversion command! This should not happen!")
        val positionCommand = getCommand("position") ?: error("Couldn't get position command! This should not happen!")
        setWarpCommand.setExecutor(SetWarpCommandExecutor())
        delWarpCommand.setExecutor(DelWarpCommandExecutor())
        warpCommand.setExecutor(WarpCommandExecutor())
        warpsCommand.setExecutor(WarpsCommandExecutor())
        warpVersionCommand.setExecutor(WarpVersionCommandExecutor())
        positionCommand.setExecutor(PositionCommandExecutor())
    }

    private fun initConfig() {
        if (Config.getConfig().get("PositionSystem") == null) {
            Config.getConfig().set("PositionSystem", false)
            Config.save()
        }
    }

}
package me.marylieh.simplewarp

import me.marylieh.simplewarp.commands.*
import me.marylieh.simplewarp.commands.position.PositionCommandExecutor
import me.marylieh.simplewarp.listener.PlayerJoinListener
import me.marylieh.simplewarp.utils.Config
import me.marylieh.simplewarp.utils.PermissionFile
import me.marylieh.simplewarp.utils.Updater
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class SimpleWarp : JavaPlugin() {

    val prefix = "§6[SimpleWarp]"
    val version = "R-4.0"
    private val pluginId: Int = 20196

    companion object {
        lateinit var instance: SimpleWarp
        private set
    }

    override fun onLoad() {
        Config.Config()
        PermissionFile.Permission()
        instance = this
    }

    override fun onEnable() {
        registerCommands()
        initConfig()
        initDefaultPermissions()
        registerListener()

        // Initialize Auto Updater
        if (Config.getConfig().getBoolean("auto-update")) {val updater = Updater(this, 395393, this.file, Updater.UpdateType.DEFAULT, true)}

        // Initialize bstats Metrics
        val metrics = Metrics(this, pluginId)

        if (Bukkit.getOnlinePlayers().isNotEmpty()) {
            Bukkit.getLogger().log(Level.WARNING, "$prefix It looks like the Server reloaded, this is not recommended. Please restart instead. SimpleWarp $version might not work as expected.")
        }
    }

    override fun onDisable() {
        Config.save()
        PermissionFile.savePermissions()
    }

    private fun registerCommands() {
        val setWarpCommand = getCommand("setwarp") ?: error("Couldn't get setwarp command! This should not happen!")
        val delWarpCommand = getCommand("delwarp") ?: error("Couldn't get delwarp command! This should not happen!")
        val warpCommand = getCommand("warp") ?: error("Couldn't get warp command! This should not happen!")
        val warpsCommand = getCommand("warps") ?: error("Couldn't get warps command! This should not happen!")
        val warpVersionCommand = getCommand("warpversion") ?: error("Couldn't get warpversion command! This should not happen!")
        val positionCommand = getCommand("position") ?: error("Couldn't get position command! This should not happen!")
        val permissionManagerCommand = getCommand("pm") ?: error("Couldn't get permissions manager command! This should not happen!")
        setWarpCommand.setExecutor(SetWarpCommandExecutor())
        delWarpCommand.setExecutor(DelWarpCommandExecutor())
        warpCommand.setExecutor(WarpCommandExecutor())
        warpsCommand.setExecutor(WarpsCommandExecutor())
        warpVersionCommand.setExecutor(WarpVersionCommandExecutor())
        positionCommand.setExecutor(PositionCommandExecutor())
        permissionManagerCommand.setExecutor(PermissionManagerCommandExecutor())
        warpCommand.setTabCompleter(WarpTabCompleter())
        delWarpCommand.setTabCompleter(WarpTabCompleter())
    }

    private fun registerListener() {
        val pluginManager = Bukkit.getPluginManager()

        if (Config.getConfig().getBoolean("DefaultPermissions")) {
            pluginManager.registerEvents(PlayerJoinListener(), this)
            Bukkit.getLogger().log(Level.INFO, "The Following default permissions will be set for each player: ${PermissionFile.getFile().getList("DefaultPermissions")}")
        }

        if (Config.getConfig().getBoolean("IntegratedPermissionSystem")) {
            pluginManager.registerEvents(PlayerJoinListener(), this)
            Bukkit.getLogger().log(Level.INFO, "The integrated permission system has been enabled. That means you can use the /pm command as OP to give permissions to your players. This only works if the permission you are trying to add is a simplewarp permission.")
        }
    }

    private fun initConfig() {
        if (Config.getConfig().get("PositionSystem") == null) {
            Config.getConfig().set("PositionSystem", false)}
        if (Config.getConfig().get("auto-update") == null) {
            Config.getConfig().set("auto-update", true)}
        if (Config.getConfig().get("PlayerWarpsOnly") == null) {
            Config.getConfig().set("PlayerWarpsOnly", false)}
        if (Config.getConfig().get("RequirePermissionForEachWarp") == null) {
            Config.getConfig().set("RequirePermissionForEachWarp", false)
        }
        if (Config.getConfig().get("DefaultPermissions") == null) {
            Config.getConfig().set("DefaultPermissions", false)
        }
        if (Config.getConfig().get("IntegratedPermissionSystem") == null) {
            Config.getConfig().set("IntegratedPermissionSystem", false)
        }
        Config.save()
    }

    private fun initDefaultPermissions() {
        val defaultPermissionsList: List<String> = listOf("ThisCouldBeYourDefaultPermission", "MakeSureToEditThisBeforeEnablingDefaultPermissions")
        if (PermissionFile.getFile().get("DefaultPermissions") == null) {
            PermissionFile.getFile().set("DefaultPermissions", defaultPermissionsList)
        }
        PermissionFile.savePermissions()
    }

}
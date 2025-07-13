package me.marylieh.simplewarp.listener

import me.marylieh.simplewarp.permissions.PermissionManager
import me.marylieh.simplewarp.utils.Config
import me.marylieh.simplewarp.utils.PermissionFile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {

    @EventHandler fun handleJoin(event: PlayerJoinEvent) {
        @Suppress("UNCHECKED_CAST")
        val permissions = PermissionFile.getFile().getList("DefaultPermissions") as List<String>

        permissions.forEach {

            if (!it.startsWith("simplewarp")) {
                return
            }

            PermissionManager.setPermission(event.player, it, true)
        }

        if (Config.getConfig().getBoolean("IntegratedPermissionSystem")) {
            val integratedPermissions = PermissionManager.getPermissions(event.player)

            integratedPermissions.forEach {
                if (!it.startsWith("simplewarp")) {
                    return
                }

                PermissionManager.setPermission(event.player, it, true)
            }
        }
    }
}
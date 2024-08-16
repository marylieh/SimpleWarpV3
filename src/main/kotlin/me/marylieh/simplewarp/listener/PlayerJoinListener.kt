package me.marylieh.simplewarp.listener

import me.marylieh.simplewarp.SimpleWarp
import me.marylieh.simplewarp.permissions.PermissionManager
import me.marylieh.simplewarp.utils.Config
import me.marylieh.simplewarp.utils.PermissionFile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {

    @EventHandler fun handleJoin(event: PlayerJoinEvent) {
        val permissions = PermissionFile.getFile().getList("DefaultPermissions") as List<String>

        if (Config.getConfig().getBoolean("IntegratedPermissionSystem")) {
            val integratedPermissions = PermissionManager.getPermissions(event.player)

            integratedPermissions.forEach {
                if (!it.startsWith("simplewarp")) {
                    return
                }

                PermissionManager.setPermission(event.player, it, true)
            }
        }

        permissions.forEach {

            if (!it.startsWith("simplewarp")) {
                return
            }

            val attachment = event.player.addAttachment(SimpleWarp.instance)
            attachment.setPermission(it, true)
        }

    }
}
package me.marylieh.simplewarp.listener

import me.marylieh.simplewarp.permissions.PermissionManager
import me.marylieh.simplewarp.utils.Config
import me.marylieh.simplewarp.utils.PermissionFile
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.logging.Level

class PlayerJoinListener : Listener {

    @EventHandler fun handleJoin(event: PlayerJoinEvent) {
        @Suppress("UNCHECKED_CAST")
        val permissions = PermissionFile.getFile().getList("DefaultPermissions") as List<String>
        Bukkit.getLogger().log(Level.INFO, "Got default Permissions: $permissions")

        permissions.forEach {

            if (!it.startsWith("simplewarp")) {
                return
            }

            PermissionManager.setPermission(event.player, it, true)
            Bukkit.getLogger().log(Level.INFO, "Setting $it for ${event.player.name} from Default Permissions")
        }

        if (Config.getConfig().getBoolean("IntegratedPermissionSystem")) {
            val integratedPermissions = PermissionManager.getPermissions(event.player)

            integratedPermissions.forEach {
                if (!it.startsWith("simplewarp")) {
                    return
                }

                PermissionManager.setPermission(event.player, it, true)
                Bukkit.getLogger().log(Level.INFO, "Setting $it for ${event.player.name} from Integrated Permissions")
            }
        }
    }
}
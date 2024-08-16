package me.marylieh.simplewarp.permissions

import me.marylieh.simplewarp.SimpleWarp
import me.marylieh.simplewarp.utils.Config
import org.bukkit.entity.Player

object PermissionManager {

    fun setPermission(player: Player, permission: String, state: Boolean) {
        val attachment = player.addAttachment(SimpleWarp.instance)
        attachment.setPermission(permission, state)
    }

    fun savePermission(player: Player, permission: String) {
        if (Config.getConfig().get("IntegratedPermissions.${player.uniqueId}.permissions") == null) {
            val tempPerms = mutableListOf<String>()
            tempPerms.add("simplewarp.default")
            Config.getConfig().set("IntegratedPermissions.${player.uniqueId}.permissions", tempPerms)
            Config.save()
        }

        val validPerms = Config.getConfig().getList("IntegratedPermissions.${player.uniqueId}.permissions") as MutableList<String>
        validPerms.add(permission)
        Config.getConfig().set("IntegratedPermissions.${player.uniqueId}.permissions", validPerms)
        Config.save()
    }

    fun removeSavedPermission(player: Player, permission: String) {
        if (Config.getConfig().get("IntegratedPermissions.${player.uniqueId}.permissions") == null) {
            return
        }
        val perms = Config.getConfig().getList("IntegratedPermissions.${player.uniqueId}.permissions") as MutableList<String>
        perms.remove(permission)
        Config.getConfig().set("IntegratedPermissions.${player.uniqueId}.permissions", perms)
        Config.save()
    }

    fun getPermissions(player: Player): List<String> {
        if (Config.getConfig().get("IntegratedPermissions.${player.uniqueId}.permissions") == null) {
            return listOf("player has no permissions")
        }

        if (player.isOp) {
            return listOf("every permission because player is OP")
        }

        val permissions = Config.getConfig().getList("IntegratedPermissions.${player.uniqueId}.permissions") as List<String>

        return permissions
    }
}
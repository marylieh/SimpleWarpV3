package me.marylieh.simplewarp.utils

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

object PermissionFile {

    private lateinit var file: File
    private lateinit var permission: YamlConfiguration

    fun Permission() {
        var dir: File = File("./plugins/SimpleWarp")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        file = File(dir, "Permission.yml")

        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        permission = YamlConfiguration.loadConfiguration(file)
    }

    fun getFile(): YamlConfiguration {
        return permission
    }

    fun savePermissions() {
        try {
            permission.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
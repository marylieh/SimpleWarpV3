package me.marylieh.simplewarp.utils

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

object Config {

    private val file: File
    private val config: YamlConfiguration

    init {
        val dir = File("./plugins/SimpleWarp")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        file = File(dir, "Warps.yml")

        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        config = YamlConfiguration.loadConfiguration(file)
    }

    fun getConfig(): YamlConfiguration = config

    fun save() {
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
package me.asmax.simplewarp.utils

import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

object Config {

    private lateinit var file: File
    private lateinit var config: YamlConfiguration

    fun Config() {
        var dir: File = File("./plugins/SimpleWarp")

        if (!dir.exists()) {
            dir.mkdirs()
        }

        this.file = File(dir, "config.yml")

        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file)
    }

    fun getConfig(): YamlConfiguration {
        return config
    }

    fun save() {
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun reload() {
        try {
            config.load(file)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }
}
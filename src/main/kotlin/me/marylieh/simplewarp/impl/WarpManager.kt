package me.marylieh.simplewarp.impl

import me.marylieh.simplewarp.utils.Config

// val warpTypePrivate = Config.getConfig().getBoolean("warpTypePrivate")

object WarpManager {

    fun createPublicWarp(id: String, owner: String, world: String, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        val config = Config.getConfig()

        config.set(".Warps.${id}.World", world)

        config.set(".Warps.${id}.X", x)
        config.set(".Warps.${id}.Y", y)
        config.set(".Warps.${id}.Z", z)

        config.set(".Warps.${id}.Yaw", yaw)
        config.set(".Warps.${id}.Pitch", pitch)

        config.set(".Warps.${id}.Owner", owner)

        Config.save()
    }

    fun deletePublicWarp(id: String) {
        Config.getConfig().set(".Warps.$id", null)
        Config.save()
    }
}
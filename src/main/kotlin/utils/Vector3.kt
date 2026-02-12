package core.tastycake.utils

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.math.vector.Vector3i
import core.tastycake.config.EasyConfig
import core.tastycake.config.EasyConfigBuilder

/**
 * @author TastyCake
 * @date 2/7/2026
 */

class Vector3(): EasyConfig(), Cloneable {
    init {
        set("X", 0.0)
        set("Y", 0.0)
        set("Z", 0.0)
    }

    constructor(x: Double, y: Double, z: Double) : this() {
        set("X", x)
        set("Y", y)
        set("Z", z)
    }

    companion object {
        val CODEC = EasyConfigBuilder
            .builder { Vector3() }
            .addField("X", Codec.DOUBLE)
            .addField("Y", Codec.DOUBLE)
            .addField("Z", Codec.DOUBLE)
            .build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vector3) return false

        val x1 = get<Double>("X")
        val y1 = get<Double>("Y")
        val z1 = get<Double>("Z")
        val x2 = other.get<Double>("X")
        val y2 = other.get<Double>("Y")
        val z2 = other.get<Double>("Z")

        return x1 == x2
                && y1 == y2
                && z1 == z2
    }

    override fun hashCode(): Int {
        val x = get<Double>("X")
        val y = get<Double>("Y")
        val z = get<Double>("Z")

        return arrayOf(x, y, z).contentHashCode()
    }

    fun add(x: Double, y: Double, z: Double): Vector3 {
        val oldX = getOrDefault("X", 0.0)
        val oldY = getOrDefault("Y", 0.0)
        val oldZ = getOrDefault("Z", 0.0)

        set("X", oldX + x)
        set("Y", oldY + y)
        set("Z", oldZ + z)

        return this
    }

    fun toVector3i(): Vector3i {
        val x = getOrDefault("X", 0.0)
        val y = getOrDefault("Y", 0.0)
        val z = getOrDefault("Z", 0.0)

        return Vector3i(x.toInt(), y.toInt(), z.toInt())
    }

    fun toVector3d(): Vector3d {
        val x = getOrDefault("X", 0.0)
        val y = getOrDefault("Y", 0.0)
        val z = getOrDefault("Z", 0.0)

        return Vector3d(x, y, z)
    }

    fun toVector3f(): Vector3f {
        val x = getOrDefault("X", 0.0)
        val y = getOrDefault("Y", 0.0)
        val z = getOrDefault("Z", 0.0)

        return Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
    }

    public override fun clone(): Vector3 {
        val x = getOrDefault("X", 0.0)
        val y = getOrDefault("Y", 0.0)
        val z = getOrDefault("Z", 0.0)

        return Vector3(x, y, z)
    }
}

fun Vector3i.toVector3(): Vector3 {
    return Vector3(x.toDouble(), y.toDouble(), z.toDouble())
}

fun Vector3d.toVector3(): Vector3 {
    return Vector3(x, y, z)
}

fun Vector3f.toVector3(): Vector3 {
    return Vector3(x.toDouble(), y.toDouble(), z.toDouble())
}
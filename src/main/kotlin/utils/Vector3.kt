package core.tastycake.utils

import com.hypixel.hytale.codec.Codec
import core.tastycake.config.EasyConfig
import core.tastycake.config.EasyConfigBuilder

/**
 * @author TastyCake
 * @date 2/7/2026
 */

class Vector3(): EasyConfig() {

    companion object {
        val CODEC = EasyConfigBuilder
            .builder { Vector3() }
            .addField("X", Codec.DOUBLE)
            .addField("Y", Codec.DOUBLE)
            .addField("Z", Codec.DOUBLE)
            .build()
    }
}
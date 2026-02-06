package core.tastycake.config

import com.hypixel.hytale.codec.Codec

/**
 * @author TastyCake
 * @date 2/5/2026
 */

class Field<T>(
    val codec: Codec<T>,
    val type: T,
) {
}
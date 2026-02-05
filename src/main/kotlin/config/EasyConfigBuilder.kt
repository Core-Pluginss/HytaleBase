package core.tastycake.config

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.PluginBase
import com.hypixel.hytale.server.core.util.Config

/**
 * @author TastyCake
 * @date 2/5/2026
 */

class EasyConfigBuilder<C: EasyConfig>(
    val builder: BuilderCodec.Builder<C>
) {
    companion object {
        inline fun <reified C: EasyConfig> builder(noinline supplier: () -> C): EasyConfigBuilder<C> {
            return EasyConfigBuilder(
                BuilderCodec.builder<C>(
                    C::class.java,
                    supplier
                )
            )
        }
    }

    inline fun <reified T> addField(key: String, codec: Codec<T>): EasyConfigBuilder<C> {
        val key = key.replaceFirstChar { it.uppercase() }

        builder
            .append<T>(
                KeyedCodec(key, codec),
                { config: C, new: T, _: ExtraInfo ->
                    config.set(key, new)
                },
                { config: C, _: ExtraInfo ->
                    config.get<T>(key)
                }
            )
            .add()

        return this
    }

    fun build(): BuilderCodec<C> {
        return builder.build()
    }
}
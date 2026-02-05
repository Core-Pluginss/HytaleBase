package core.tastycake.config.example

import com.hypixel.hytale.codec.Codec
import core.tastycake.config.EasyConfig
import core.tastycake.config.EasyConfigBuilder

/**
 * @author TastyCake
 * @date 2/5/2026
 */

class ExampleConfig : EasyConfig() {

}

public class ConfigTest {
    public fun withConfig() {
        val configCodec = EasyConfigBuilder
            .builder { ExampleConfig() }
            .addField("ErrorMessage", Codec.STRING)
            .build()

        // Use withConfig(configCodec) in main class
    }
}
package core.tastycake.ui.codec.example

import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import core.tastycake.config.EasyConfig
import core.tastycake.config.example.ExampleConfig
import core.tastycake.ui.codec.CodecUI
import core.tastycake.ui.codec.Field

/**
 * @author TastyCake
 * @date 2/6/2026
 */

class ExampleCodecUI(
    val playerRef: PlayerRef,
    val store: Store<EntityStore>,
) {
//    var ui: CodecUI<ExampleConfig> = CodecUI(
//        "Example",
//        // Main.CONFIG.get()
//    ) {
//        // Main.CONFIG.save()
//    }
//        .fillFields()
//
//    fun open() {
//        ui.open(playerRef, store)
//    }
}
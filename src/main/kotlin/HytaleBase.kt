package core.tastycake

import com.hypixel.hytale.server.core.io.adapter.PacketAdapters
import core.tastycake.config.EasyConfig
import core.tastycake.interaction.InteractionWatcher

/**
 * @author TastyCake
 * @date 2/5/2026
 */

public class HytaleBase {
    companion object {
        fun init() {
            PacketAdapters.registerOutbound(InteractionWatcher())
        }
    }
}
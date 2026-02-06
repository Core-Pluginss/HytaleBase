package core.tastycake

import com.hypixel.hytale.server.core.command.system.CommandSender
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import core.tastycake.config.EasyConfig
import core.tastycake.interaction.InteractionWatcher
import core.tastycake.leaderboard.LeaderboardRegistry

/**
 * @author TastyCake
 * @date 2/5/2026
 */

public class HytaleBase {
    companion object {
        var initialized = false

        fun init(plugin: JavaPlugin) {
            if (initialized) return
            initialized = true

            plugin.eventRegistry.registerGlobal(PlayerReadyEvent::class.java)
            { event ->
                LeaderboardRegistry.getLeaderboards()
                    .forEach {
                        it.username((event.player as CommandSender).uuid, event.player.displayName)
                    }
            }

            PacketAdapters.registerOutbound(InteractionWatcher())
        }
    }
}
package core.tastycake.leaderboard

import com.hypixel.hytale.codec.codecs.array.ArrayCodec
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.util.Config
import core.tastycake.config.EasyConfig
import core.tastycake.config.EasyConfigBuilder
import core.tastycake.ui.UIPlayer
import java.util.UUID

/**
 * @author TastyCake
 * @date 2/6/2026
 */

class Leaderboard(
    val name: String,
    val config: Config<LeaderboardConfig>
) {
    // Required: in main class: withConfig()
    public class LeaderboardConfig: EasyConfig() {

    }

    fun getOrNew(uuid: UUID, defaultPoints: Int = 0): LeaderboardPlayer.LeaderboardPlayerConfig {
        var array = config.get()
            .get<Array<LeaderboardPlayer.LeaderboardPlayerConfig>>("Players")!!

        var player = array.firstOrNull { it.get<UUID>("UUID") == uuid }

        if (player == null) {
            player = LeaderboardPlayer.LeaderboardPlayerConfig()
                .set("UUID", uuid)
                .set("Username", "N/A")
                .set("Points", defaultPoints)
                .build()

            array += player

            config.get().set("Players", array)

            config.save()
        }

        return player
    }

    fun set(uuid: UUID, points: Int) {
        val player = getOrNew(uuid)
        player.set("Points", points)

        config.save()
    }

    fun username(uuid: UUID, username: String) {
        val player = getOrNew(uuid)
        player.set("Username", username)

        config.save()
    }

    fun register(): Leaderboard {
        LeaderboardRegistry.
                register(this)

        return this
    }

    companion object {
        val CODEC = EasyConfigBuilder
            .builder(::LeaderboardConfig)
            .addField("Players",
                ArrayCodec(LeaderboardPlayer.CODEC)
                { size -> arrayOfNulls<LeaderboardPlayer.LeaderboardPlayerConfig>(size) }
            )
            .build()
    }
}
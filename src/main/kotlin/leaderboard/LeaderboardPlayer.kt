package core.tastycake.leaderboard

import com.hypixel.hytale.codec.Codec
import core.tastycake.config.EasyConfig
import core.tastycake.config.EasyConfigBuilder
import java.util.UUID

/**
 * @author TastyCake
 * @date 2/6/2026
 */

class LeaderboardPlayer(

) {
    public class LeaderboardPlayerConfig: EasyConfig() {

    }

    companion object {
        val CODEC = EasyConfigBuilder
            .builder(::LeaderboardPlayerConfig)

            .addField("UUID", Codec.UUID_STRING)
            .addField("Username", Codec.STRING)
            .addField("Points", Codec.INTEGER)

            .build()
    }
}
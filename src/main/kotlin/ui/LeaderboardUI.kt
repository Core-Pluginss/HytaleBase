package core.tastycake.ui

import au.ellie.hyui.builders.ContainerBuilder
import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIPatchStyle
import au.ellie.hyui.builders.HyUIStyle
import au.ellie.hyui.builders.HyvatarImageBuilder
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.builders.PageBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import core.tastycake.leaderboard.Leaderboard
import core.tastycake.leaderboard.LeaderboardPlayer
import java.util.UUID

/**
 * @author TastyCake
 * @date 2/6/2026
 */

class LeaderboardUI(
    val leaderboard: Leaderboard
) {

    fun open(player: UIPlayer) {
        val scrollable = GroupBuilder.group()
            .withFlexWeight(1)
            .withLayoutMode(LayoutModeSupported.LayoutMode.TopScrolling)

        val players = leaderboard.config.get()
            .get<Array<LeaderboardPlayer.LeaderboardPlayerConfig>>("Players")?: return

        val sorted = players.sortedBy {
            it.getOrDefault("Points", 0)
        }

        sorted.forEach {
            val uuid = it.get<UUID>("UUID")?: return@forEach
            val username = it.get<String>("Username")?: return@forEach

            scrollable
                .addChild(
                    getPlacement(uuid, username)
                )
        }

        val pageBuilder = PageBuilder
            .pageForPlayer(player.playerRef)
            .withLifetime(CustomPageLifetime.CanDismiss)
            .addElement(
                ContainerBuilder.container()
                    .withTitleText(leaderboard.name)
                    .withAnchor(HyUIAnchor().setWidth(400).setHeight(600))
                    .withLayoutMode(LayoutModeSupported.LayoutMode.TopScrolling)
                    .addContentChild(
                        scrollable
                    )
            )

        val page = pageBuilder.open(player.store)
    }

    fun getPlacement(uuid: UUID, username: String): GroupBuilder {
        val player = leaderboard.getOrNew(uuid)
        val playerPoints = player.get<Int>("Points")

        return GroupBuilder.group()
            .withAnchor(HyUIAnchor().setWidth(300).setHeight(100).setBottom(20))
            .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
            .withBackground(HyUIPatchStyle().setColor("#444444"))
            .addChild(
                HyvatarImageBuilder.hyvatar()
                    .withUsername(player.get<String>("Username"))
                    .withAnchor(HyUIAnchor().setWidth(100).setHeight(100).setRight(10))
            )
            .addChild(
                GroupBuilder.group()
                    .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
                    .withAnchor(HyUIAnchor().setWidth(190).setHeight(100))
                    .addChild(
                        LabelBuilder.label()
                            .withText(player.get<String>("Username"))
                            .withStyle(
                                HyUIStyle()
                                    .setRenderBold(true)
                            )
                    )
                    .addChild(
                        LabelBuilder.label()
                            .withText("Points: $playerPoints")
                    )
            )
    }
}
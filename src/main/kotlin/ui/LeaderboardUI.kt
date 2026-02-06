package core.tastycake.ui

import au.ellie.hyui.builders.*
import au.ellie.hyui.elements.LayoutModeSupported
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import core.tastycake.leaderboard.Leaderboard
import core.tastycake.leaderboard.LeaderboardPlayer

class LeaderboardUI(
    val leaderboard: Leaderboard
) {

    fun open(player: UIPlayer) {
        val players = leaderboard.config.get()
            .get<Array<LeaderboardPlayer.LeaderboardPlayerConfig>>("Players")
            ?: return

        val sorted = players
            .sortedByDescending { (it.getOrDefault("Points", 0) ?: 0) }
            .toList()

        val viewerName = player.playerRef.getUsername() // adjust if needed
        val viewerIndex = sorted.indexOfFirst {
            val u = it.get<String>("Username")
            u != null && u.equals(viewerName, ignoreCase = true)
        }
        val viewerRank = if (viewerIndex >= 0) viewerIndex + 1 else null
        val viewerVotes = if (viewerIndex >= 0) (sorted[viewerIndex].getOrDefault("Points", 0) ?: 0) else null

        // Root is fixed size so scroll never “falls out” of the page
        val root = GroupBuilder.group()
            .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
            .withAnchor(HyUIAnchor().setWidth(560).setHeight(680))

        root.addChild(buildHeaderCard(viewerRank, viewerVotes))
        root.addChild(buildPodium(sorted))
        root.addChild(buildSpacer(10))

        val scrollable = GroupBuilder.group()
            .withLayoutMode(LayoutModeSupported.LayoutMode.TopScrolling)
            .withAnchor(HyUIAnchor().setWidth(560).setHeight(410)) // fixed!
        sorted.drop(3).forEachIndexed { idx, cfg ->
            val rank = idx + 4
            val username = cfg.get<String>("Username") ?: return@forEachIndexed
            val points = (cfg.getOrDefault("Points", 0) ?: 0)
            scrollable.addChild(buildRow(rank, username, points))
        }
        root.addChild(scrollable)

        PageBuilder.pageForPlayer(player.playerRef)
            .withLifetime(CustomPageLifetime.CanDismiss)
            .addElement(
                ContainerBuilder.container()
                    .withTitleText("Leaderboard")
                    .withAnchor(HyUIAnchor().setWidth(600).setHeight(740))
                    .addContentChild(
                        GroupBuilder.group()
                            .withLayoutMode(LayoutModeSupported.LayoutMode.MiddleCenter)
                            .addChild(root)
                    )
            )
            .open(player.store)
    }

    private fun buildHeaderCard(viewerRank: Int?, viewerVotes: Int?): GroupBuilder {
        val bg = HyUIPatchStyle().setColor("#101726")
        val neon = HyUIPatchStyle().setColor("#3CE6FF")

        return GroupBuilder.group()
            .withAnchor(HyUIAnchor().setWidth(560).setHeight(92))
            .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
            .withBackground(bg)
            .addChild(
                GroupBuilder.group()
                    .withAnchor(HyUIAnchor().setWidth(8).setHeight(92))
                    .withBackground(neon)
            )
            .addChild(
                GroupBuilder.group()
                    .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
                    .withAnchor(HyUIAnchor().setWidth(540).setHeight(92).setLeft(14))
                    .addChild(
                        LabelBuilder.label()
                            .withText("${leaderboard.name} Leaderboard")
                            .withStyle(
                                HyUIStyle()
                                    .setRenderBold(true)
                                    .setFontSize(18f)
                                    .setTextColor("#EAF2FF")
                            )
                    )
                    .addChild(
                        LabelBuilder.label()
                            .withText(
                                if (viewerRank != null && viewerVotes != null)
                                    "Your ${leaderboard.pointsName}: $viewerVotes  •  Your placement: #$viewerRank"
                                else
                                    "Your placement: Not ranked"
                            )
                            .withStyle(
                                HyUIStyle()
                                    .setFontSize(13f)
                                    .setTextColor("#9FB0CC")
                            )
                    )
            )
    }

    private fun buildPodium(sorted: List<LeaderboardPlayer.LeaderboardPlayerConfig>): GroupBuilder {
        val first = sorted.getOrNull(0)
        val second = sorted.getOrNull(1)
        val third = sorted.getOrNull(2)

        // Podium wrapper fixed width/height
        val wrap = GroupBuilder.group()
            .withAnchor(HyUIAnchor().setWidth(560).setHeight(160))
            .withLayoutMode(LayoutModeSupported.LayoutMode.MiddleCenter)

        // EXACT math: 175 + 5 + 200 + 5 + 175 = 560
        val row = GroupBuilder.group()
            .withAnchor(HyUIAnchor().setWidth(560).setHeight(160))
            .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
            .addChild(buildPodiumTile(rank = 2, cfg = second, width = 175, height = 140))
            .addChild(buildSpacerX(5, 160))
            .addChild(buildPodiumTile(rank = 1, cfg = first, width = 200, height = 160))
            .addChild(buildSpacerX(5, 160))
            .addChild(buildPodiumTile(rank = 3, cfg = third, width = 175, height = 140))

        wrap.addChild(row)
        return wrap
    }

    private fun buildPodiumTile(
        rank: Int,
        cfg: LeaderboardPlayer.LeaderboardPlayerConfig?,
        width: Int,
        height: Int
    ): GroupBuilder {
        val (bgColor, accent) = when (rank) {
            1 -> "#1A1522" to "#F6C344"
            2 -> "#141B22" to "#CFD6DD"
            else -> "#1E1713" to "#CD7F32"
        }

        val username = cfg?.get<String>("Username") ?: "—"
        val points = (cfg?.getOrDefault("Points", 0) ?: 0)

        // Centered tile with a top accent bar, then centered content block
        return GroupBuilder.group()
            .withAnchor(HyUIAnchor().setWidth(width).setHeight(height))
            .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
            .withBackground(HyUIPatchStyle().setColor(bgColor))
            .addChild(
                GroupBuilder.group()
                    .withAnchor(HyUIAnchor().setWidth(width).setHeight(6))
                    .withBackground(HyUIPatchStyle().setColor(accent))
            )
            .addChild(
                GroupBuilder.group()
                    .withAnchor(HyUIAnchor().setWidth(width).setHeight(height - 6))
                    .withLayoutMode(LayoutModeSupported.LayoutMode.MiddleCenter)
                    .addChild(
                        GroupBuilder.group()
                            .withAnchor(HyUIAnchor().setWidth(width).setHeight(height - 6).setTop(5))
                            .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
                            .addChild(
                                LabelBuilder.label()
                                    .withText("#$rank")
                                    .withStyle(
                                        HyUIStyle()
                                            .setRenderBold(true)
                                            .setFontSize(if (rank == 1) 28f else 22f)
                                            .setTextColor(accent)
                                            .setAlignment(HyUIStyle.Alignment.Center)
                                    )
                            )
                            .addChild(
                                LabelBuilder.label()
                                    .withText(username)
                                    .withStyle(
                                        HyUIStyle()
                                            .setRenderBold(true)
                                            .setFontSize(if (rank == 1) 16f else 15f)
                                            .setTextColor("#EAF2FF")
                                            .setAlignment(HyUIStyle.Alignment.Center)
                                    )
                            )
                            .addChild(
                                LabelBuilder.label()
                                    .withText("${leaderboard.pointsName}: $points")
                                    .withStyle(
                                        HyUIStyle()
                                            .setFontSize(14f)
                                            .setTextColor("#A7B1C6")
                                            .setAlignment(HyUIStyle.Alignment.Center)
                                    )
                            )
                    )
            )
    }

    private fun buildRow(rank: Int, username: String, points: Int): GroupBuilder {
        val bg = HyUIPatchStyle().setColor("#0D1320")
        val accent = when (rank % 3) {
            0 -> "#3CE6FF"
            1 -> "#9A6BFF"
            else -> "#2EE59D"
        }

        return GroupBuilder.group()
            .withAnchor(HyUIAnchor().setWidth(560).setHeight(54).setBottom(8))
            .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
            .withBackground(bg)
            .addChild(
                GroupBuilder.group()
                    .withAnchor(HyUIAnchor().setWidth(4).setHeight(54))
                    .withBackground(HyUIPatchStyle().setColor(accent))
            )
            .addChild(
                GroupBuilder.group()
                    .withAnchor(HyUIAnchor().setWidth(72).setHeight(54).setLeft(10))
                    .withLayoutMode(LayoutModeSupported.LayoutMode.MiddleCenter)
                    .addChild(
                        LabelBuilder.label()
                            .withText("#$rank")
                            .withStyle(
                                HyUIStyle()
                                    .setRenderBold(true)
                                    .setFontSize(14f)
                                    .setTextColor("#9FB0CC")
                            )
                    )
            )
            .addChild(
                GroupBuilder.group()
                    .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
                    .withAnchor(HyUIAnchor().setWidth(440).setHeight(54))
                    .addChild(
                        LabelBuilder.label()
                            .withText(username)
                            .withStyle(
                                HyUIStyle()
                                    .setRenderBold(true)
                                    .setFontSize(14f)
                                    .setTextColor("#EAF2FF")
                            )
                    )
                    .addChild(
                        LabelBuilder.label()
                            .withText("${leaderboard.pointsName}: $points")
                            .withStyle(
                                HyUIStyle()
                                    .setFontSize(13f)
                                    .setTextColor("#A7B1C6")
                            )
                    )
            )
    }

    private fun buildSpacer(h: Int): GroupBuilder =
        GroupBuilder.group().withAnchor(HyUIAnchor().setWidth(1).setHeight(h))

    private fun buildSpacerX(w: Int, h: Int): GroupBuilder =
        GroupBuilder.group().withAnchor(HyUIAnchor().setWidth(w).setHeight(h))
}

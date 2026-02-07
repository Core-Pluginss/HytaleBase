package core.tastycake.ui

import au.ellie.hyui.builders.ButtonBuilder
import au.ellie.hyui.builders.ContainerBuilder
import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIPadding
import au.ellie.hyui.builders.HyUIStyle
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.builders.PageBuilder
import au.ellie.hyui.builders.TextFieldBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import au.ellie.hyui.events.UIContext
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import core.tastycake.ui.runnables.InputCallback
import core.tastycake.ui.runnables.InputResult

/**
 * @author TastyCake
 * @date 2/7/2026
 */

class AcceptUI(
    val playerRef: PlayerRef,
    val store: Store<EntityStore>
) {
    fun open(request: String = "", backMenu: InteractiveCustomUIPage<*>? = null, callback: InputCallback) {
        val text = request.ifEmpty { "Are you sure you want to do this?" }

        val player = UIPlayer(
            playerRef,
            store,
            backMenu,
        )

        val pageBuilder = PageBuilder
            .pageForPlayer(playerRef)
            .withLifetime(CustomPageLifetime.CantClose)
            .addElement(
                ContainerBuilder.container()
                    .withTitleText("Waiting for your choice")
                    .withAnchor(HyUIAnchor().setWidth(500).setHeight(250))
                    .addContentChild(
                        GroupBuilder.group()
                            .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
                            .addChild(
                                LabelBuilder.label()
                                    .withText(text)
                                    .withStyle(
                                        HyUIStyle()
                                            .setRenderBold(true)
                                            .setAlignment(HyUIStyle.Alignment.Center)
                                    )
                                    .withPadding(HyUIPadding(10, 10, 20, 10))
                            )
                            .addChild(
                                GroupBuilder.group()
                                    .withFlexWeight(1)
                                    .withLayoutMode(LayoutModeSupported.LayoutMode.Center)
                                    .withPadding(HyUIPadding(10, 10, 10, 10))
                                    .withStyle(
                                        HyUIStyle()
                                            .setAlignment(HyUIStyle.Alignment.Center)
                                    )
                                    .addChild(
                                        ButtonBuilder.textButton()
                                            .withAnchor(HyUIAnchor().setWidth(150).setHeight(40).setRight(20))
                                            .withText("ACCEPT")
                                            .withPadding(HyUIPadding(0, 5, 0, 0))
                                            .addEventListenerWithContext(CustomUIEventBindingType.Activating, Void::class.java) { _, ctx ->
                                                callback.result("", InputResult.ACCEPTED)
                                                InputUI.goBack(player, ctx)
                                            }
                                    )
                                    .addChild(
                                        ButtonBuilder.cancelTextButton()
                                            .withAnchor(HyUIAnchor().setWidth(150).setHeight(40))
                                            .withText("DENY")
                                            .withPadding(HyUIPadding(5, 0, 0, 0))
                                            .addEventListenerWithContext(CustomUIEventBindingType.Activating, Void::class.java) { _, ctx ->
                                                callback.result("", InputResult.CANCELED)
                                                InputUI.goBack(player, ctx)
                                            }
                                    )
                            )
                    )
            )

        pageBuilder.open(store)
    }
}
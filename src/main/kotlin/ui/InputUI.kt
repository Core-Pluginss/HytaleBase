package core.tastycake.ui

import au.ellie.hyui.builders.*
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
 * @date 1/27/2026
 */

class InputUI(
    val playerRef: PlayerRef,
    val store: Store<EntityStore>
) {
    var currentInput: String = ""

    fun open(request: String = "", backMenu: InteractiveCustomUIPage<*>? = null, callback: InputCallback) {
        val text = request.ifEmpty { "Please enter your input" }

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
                    .withTitleText("Input request")
                    .withAnchor(HyUIAnchor().setWidth(500).setHeight(250))
                    .addContentChild(
                        GroupBuilder.group()
                            .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
                            .addChild(
                                LabelBuilder.label()
                                    .withText(text)
                                    .withPadding(HyUIPadding(10, 10, 20, 10))
                                    .withStyle(
                                        HyUIStyle()
                                            .setRenderBold(true)
                                            .setAlignment(Alignment.Center)
                                    )
                            )
                            .addChild(
                                TextFieldBuilder.textInput()
                                    .withAnchor(HyUIAnchor().setWidth(350).setHeight(40).setBottom(35))
                                    .withPadding(HyUIPadding(10, 10, 20, 10))
                                    .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                                        currentInput = v
                                    }
                            )
                            .addChild(
                                GroupBuilder.group()
                                    .withFlexWeight(1)
                                    .withLayoutMode(LayoutModeSupported.LayoutMode.Center)
                                    .withPadding(HyUIPadding(10, 10, 10, 10))
                                    .withStyle(
                                        HyUIStyle()
                                            .setAlignment(Alignment.Center)
                                    )
                                    .addChild(
                                        ButtonBuilder.textButton()
                                            .withAnchor(HyUIAnchor().setWidth(150).setHeight(40).setRight(20))
                                            .withText("DONE")
                                            .withPadding(HyUIPadding(0, 5, 0, 0))
                                            .addEventListenerWithContext(CustomUIEventBindingType.Activating, Void::class.java) { _, ctx ->
                                                callback.result(currentInput, InputResult.COMPLETED)
                                                goBack(player, ctx)
                                            }
                                    )
                                    .addChild(
                                        ButtonBuilder.cancelTextButton()
                                            .withAnchor(HyUIAnchor().setWidth(150).setHeight(40))
                                            .withText("CANCEL")
                                            .withPadding(HyUIPadding(5, 0, 0, 0))
                                            .addEventListenerWithContext(CustomUIEventBindingType.Activating, Void::class.java) { _, ctx ->
                                                callback.result(currentInput, InputResult.CANCELED)
                                                goBack(player, ctx)
                                            }
                                    )
                            )
                    )
            )

        pageBuilder.open(store)
    }

    companion object {
        fun goBack(uiplayer: UIPlayer, ctx: UIContext) {
            if (uiplayer.lastUI == null) {
                ctx.page.ifPresent { it.close() }
                return
            }

            val player = uiplayer.store.getComponent(uiplayer.playerRef.reference!!, Player.getComponentType())?: return

            player.pageManager.openCustomPage(uiplayer.playerRef.reference!!, uiplayer.store, uiplayer.lastUI)
        }
    }
}
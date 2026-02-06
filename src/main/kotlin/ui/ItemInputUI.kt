package core.tastycake.ui

import au.ellie.hyui.builders.ButtonBuilder
import au.ellie.hyui.builders.ContainerBuilder
import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIPadding
import au.ellie.hyui.builders.HyUIStyle
import au.ellie.hyui.builders.ItemGridBuilder
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.builders.PageBuilder
import au.ellie.hyui.builders.TextFieldBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import au.ellie.hyui.events.SlotClickingEventData
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.asset.type.item.config.Item
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.ui.ItemGridSlot
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import core.tastycake.ui.runnables.InputCallback
import core.tastycake.ui.runnables.InputResult

/**
 * @author TastyCake
 * @date 2/5/2026
 */

class ItemInputUI(
    val playerRef: PlayerRef,
    val store: Store<EntityStore>
) {
    var currentGrid = mutableListOf<String>()

    var currentInput = ""

    var grid: ItemGridBuilder = ItemGridBuilder.itemGrid()
        .withAnchor(HyUIAnchor().setWidth(900).setHeight(300))
        .withShowScrollbar(true)
        .withRenderItemQualityBackground(true)
        .withSlotsPerRow(6)
        .withHitTestVisible(false)
        .withStyle(
            HyUIStyle()
                .set("SlotSpacing", 8)
                .set("SlotSize", 100)
        )

    fun buildGrid(filter: (ItemStack) -> Boolean = { true }) {
        currentGrid.clear()
        grid.slots.clear()

        Item.getAssetMap().assetMap.forEach { (_, item) ->
            if (item.id.isNotEmpty() && !item.id.contains(currentInput)) return@forEach
            val stack = ItemStack(item.id)

            if (!filter(stack)) return@forEach

            currentGrid.add(stack.itemId)
            grid.addSlot(
                ItemGridSlot(stack)
            )
        }
    }

    fun open(filter: (ItemStack) -> Boolean = { true }, backMenu: InteractiveCustomUIPage<*>? = null, callback: InputCallback) {
        buildGrid(filter)

        grid.addEventListener(CustomUIEventBindingType.SlotClicking,
            SlotClickingEventData::class.java) { slot ->
            callback.result(currentGrid[slot.slotIndex], InputResult.COMPLETED)

            goBack(backMenu)
        }

        val pageBuilder = PageBuilder
            .pageForPlayer(playerRef)
            .withLifetime(CustomPageLifetime.CantClose)
            .addElement(
                ContainerBuilder.container()
                        .withTitleText("Item request")
                    .withAnchor(HyUIAnchor().setWidth(900).setHeight(500))
                    .addContentChild(
                        GroupBuilder.group()
                            .withLayoutMode(LayoutModeSupported.LayoutMode.Top)
                            .addChild(
                                LabelBuilder.label()
                                    .withText("Click on an item to select")
                                    .withStyle(
                                        HyUIStyle()
                                            .setRenderBold(true)
                                            .setAlignment(HyUIStyle.Alignment.Center)
                                    )
                                    .withPadding(HyUIPadding(10, 10, 20, 10))
                            )
                            .addChild(
                                grid
                            )
                            .addChild(
                                GroupBuilder.group()
                                    .withFlexWeight(1)
                                    .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                                    .withStyle(HyUIStyle().setAlignment(HyUIStyle.Alignment.Center))
                                    .addChild(
                                        LabelBuilder.label()
                                            .withText("Search for item:")
                                            .withAnchor(HyUIAnchor().setRight(5))
                                    )
                                    .addChild(
                                        TextFieldBuilder.textInput()
                                            .withAnchor(HyUIAnchor().setWidth(350).setHeight(40).setRight(15))
                                            .withPadding(HyUIPadding(10, 10, 20, 10))
                                            .addEventListenerWithContext(CustomUIEventBindingType.ValueChanged, String::class.java) { v, ctx ->
                                                currentInput = v

                                                buildGrid(filter)
                                                ctx.updatePage(true)
                                            }
                                    )
                                    .addChild(
                                        ButtonBuilder.cancelTextButton()
                                            .withAnchor(HyUIAnchor().setWidth(150).setHeight(40))
                                            .withText("CANCEL")
                                            .withPadding(HyUIPadding(5, 0, 0, 0))
                                            .addEventListener(CustomUIEventBindingType.Activating) { _ ->
                                                callback.result(currentInput, InputResult.CANCELED)
                                                goBack(backMenu)
                                            }
                                        )
                                    )
                            )
                    )

        pageBuilder.open(store)
    }

    private fun goBack(page: InteractiveCustomUIPage<*>?) {
        page?: return

        val player = store.getComponent(playerRef.reference!!, Player.getComponentType())?: return

        player.pageManager.openCustomPage(playerRef.reference!!, store, page)
    }
}
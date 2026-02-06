package core.tastycake.ui.codec

import au.ellie.hyui.builders.ButtonBuilder
import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIPadding
import au.ellie.hyui.builders.HyUIPatchStyle
import au.ellie.hyui.builders.HyUIStyle
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.builders.TextFieldBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import core.tastycake.ui.ItemInputUI
import core.tastycake.ui.UIPlayer
import core.tastycake.ui.runnables.FieldInputCallback
import core.tastycake.ui.runnables.InputCallback
import core.tastycake.ui.runnables.InputResult
import core.tastycake.ui.runnables.InputType

/**
 * @author TastyCake
 * @date 2/5/2026
 */

enum class FieldType(
    val group: (String, UIPlayer, FieldInputCallback) -> GroupBuilder
) {
    TEXT(
        { value, player, callback ->
            GroupBuilder.group()
                .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                .addChild(
                    TextFieldBuilder.textInput()
                        .withAnchor(HyUIAnchor().setWidth(350).setHeight(40).setBottom(35))
                        .withPadding(HyUIPadding(10, 10, 20, 10))
                        .withValue(value)
                        .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                            callback.input(v.toString())
                        }
                )
        }
    ),
    ITEM(
        { value, player, callback ->
            GroupBuilder.group()
                .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                .addChild(
                    GroupBuilder.group()
                        .withBackground(HyUIPatchStyle().setColor("#444444"))
                        .addChild(
                            LabelBuilder.label()
                                .withText(value)
                        )
                        .withAnchor(HyUIAnchor().setRight(10))
                )
                .addChild(
                    ButtonBuilder.textButton()
                        .withText("SELECT")
                        .addEventListener(CustomUIEventBindingType.Activating) {
                            ItemInputUI(player.playerRef, player.store)
                                .open(
                                    backMenu = player.lastUI,
                                    callback = object : InputCallback {
                                        override fun result(
                                            input: String,
                                            result: InputResult
                                        ) {
                                            if (result == InputResult.COMPLETED) {
                                                callback.input(input)
                                            }
                                        }
                                    }
                                )
                        }
                )
        }
    )
}
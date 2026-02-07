package core.tastycake.ui.codec

import au.ellie.hyui.builders.ButtonBuilder
import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIPadding
import au.ellie.hyui.builders.HyUIPatchStyle
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.builders.TextFieldBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import core.tastycake.ui.ItemInputUI
import core.tastycake.ui.UIPlayer
import core.tastycake.ui.runnables.FieldInputCallback
import core.tastycake.ui.runnables.InputCallback
import core.tastycake.ui.runnables.InputResult
import core.tastycake.utils.Vector3

/**
 * @author TastyCake
 * @date 2/5/2026
 */

enum class FieldType(
    val group: (Any, UIPlayer, FieldInputCallback) -> GroupBuilder,
) {
    TEXT(
        { value, _, callback ->
            GroupBuilder.group()
                .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                .addChild(
                    TextFieldBuilder.textInput()
                        .withAnchor(HyUIAnchor().setWidth(350).setHeight(40))
                        .withPadding(HyUIPadding(10, 10, 20, 10))
                        .withValue(value.toString())
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
                                .withText(value.toString())
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
    ),
    VECTOR(
        { value, player, callback ->
            try {
                val vector = value as Vector3

                GroupBuilder.group()
                    .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                    .addChild(
                        TextFieldBuilder.textInput()
                            .withPlaceholderText("x")
                            .withAnchor(HyUIAnchor().setWidth(100).setHeight(40).setRight(5))
                            .withPadding(HyUIPadding(10, 10, 20, 10))
                            .withValue(vector.getOrDefault("X", 0.0)!!.toString())
                            .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                                callback.input(v.toString(), "X")
                            }
                    )
                    .addChild(
                        TextFieldBuilder.textInput()
                            .withPlaceholderText("y")
                            .withAnchor(HyUIAnchor().setWidth(100).setHeight(40))
                            .withPadding(HyUIPadding(10, 10, 20, 10).setRight(5))
                            .withValue(vector.getOrDefault("Y", 0.0)!!.toString())
                            .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                                callback.input(v.toString(), "Y")
                            }
                    )
                    .addChild(
                        TextFieldBuilder.textInput()
                            .withPlaceholderText("z")
                            .withAnchor(HyUIAnchor().setWidth(100).setHeight(40))
                            .withPadding(HyUIPadding(10, 10, 20, 10))
                            .withValue(vector.getOrDefault("Z", 0.0)!!.toString())
                            .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                                callback.input(v.toString(), "Z")
                            }
                    )
            } catch (e: Exception) {
                GroupBuilder.group()
            }
        }
    )
}
package core.tastycake.ui.codec

import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIPadding
import au.ellie.hyui.builders.HyUIStyle
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.builders.TextFieldBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import core.tastycake.ui.runnables.FieldInputCallback
import core.tastycake.ui.runnables.InputCallback
import core.tastycake.ui.runnables.InputType

/**
 * @author TastyCake
 * @date 2/5/2026
 */

enum class FieldType(
    val group: (FieldInputCallback) -> GroupBuilder
) {
    TEXT(
        { callback ->
            GroupBuilder.group()
                .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                .addChild(
                    TextFieldBuilder.textInput()
                        .withAnchor(HyUIAnchor().setWidth(350).setHeight(40).setBottom(35))
                        .withPadding(HyUIPadding(10, 10, 20, 10))
                        .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                            callback.input(v.toString())
                        }
                )
        }
    );

    fun <T> addListener(listener: CustomUIEventBindingType, valueClass: Class<T>, setter: (T) -> Unit) {

    }
}
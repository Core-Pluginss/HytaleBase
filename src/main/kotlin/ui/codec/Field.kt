package core.tastycake.ui.codec

import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIStyle
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import core.tastycake.ui.UIPlayer
import core.tastycake.ui.runnables.FieldInputCallback

/**
 * @author TastyCake
 * @date 2/5/2026
 */

class Field(
    val key: String,
    val fieldType: FieldType,
    val setter: (String) -> Unit,
    val getter: () -> String,
    var update: () -> Unit = {}
) {
    fun getGroup(player: UIPlayer): GroupBuilder {
        return GroupBuilder.group()
            .withFlexWeight(1)
            .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
            .addChild(
                LabelBuilder.label()
                    .withText(key)
                    .withStyle(
                        HyUIStyle()
                            .setRenderBold(true)
                            .setAlignment(HyUIStyle.Alignment.Center)
                    )
                    .withAnchor(HyUIAnchor().setRight(5))
            )
            .addChild(
                fieldType.group.invoke(getter.invoke(),
                    player,
                    object : FieldInputCallback {
                        override fun input(input: String) {
                            setter.invoke(input)

                            update.invoke()
                        }
                    })
            )
    }
}
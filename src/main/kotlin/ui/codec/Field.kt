package core.tastycake.ui.codec

import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIPadding
import au.ellie.hyui.builders.HyUIStyle
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import core.tastycake.ui.runnables.FieldInputCallback
import core.tastycake.ui.runnables.InputCallback
import core.tastycake.ui.runnables.InputResult
import core.tastycake.ui.runnables.InputType

/**
 * @author TastyCake
 * @date 2/5/2026
 */

class Field(
    val key: String,
    val update: (String) -> Unit,
    val fieldType: FieldType
) {
    fun getGroup(): GroupBuilder {
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
                fieldType.group.invoke(object : FieldInputCallback {
                    override fun input(input: String) {
                        update.invoke(input)
                    }
                })
            )
    }
}
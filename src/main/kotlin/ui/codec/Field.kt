package core.tastycake.ui.codec

import au.ellie.hyui.builders.Alignment
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
    val setter: (String, String, (String) -> Any) -> Unit,
    val getter: () -> Any,
    var update: () -> Unit = {}
) {
    fun getGroup(player: UIPlayer): GroupBuilder {
        val regex = Regex("""^([A-Za-z]+)(\d+)(.+)$""")
        val match = regex.matchEntire(key)

        var fixedKey = key

        if (match != null) {
            fixedKey = match.groupValues[1]
        }

        return GroupBuilder.group()
            .withFlexWeight(1)
            .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
            .addChild(
                LabelBuilder.label()
                    .withText(fixedKey)
                    .withStyle(
                        HyUIStyle()
                            .setRenderBold(true)
                            .setAlignment(Alignment.Center)
                    )
                    .withAnchor(HyUIAnchor().setRight(5))
            )
            .addChild(
                fieldType.group.invoke(
                    key,
                    getter.invoke(),
                    player,
                    object : FieldInputCallback {
                        override fun input(input: String, variable: String) {
                            setter.invoke(input, variable, fieldType.cast)

                            update.invoke()
                        }
                    })
            )
    }
}
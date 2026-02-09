package core.tastycake.ui.codec

import au.ellie.hyui.builders.ContainerBuilder
import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.builders.PageBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import core.tastycake.config.EasyConfig
import core.tastycake.ui.UIPlayer
import core.tastycake.utils.Vector3

/**
 * @author TastyCake
 * @date 2/5/2026
 */

open class CodecUI<T: EasyConfig>(
    val label: String,
    val obj: T,
    val save: () -> Unit = {}
) {
    private val fields = mutableListOf<Field>()

    fun addField(field: Field): CodecUI<T> {
        field.update = save
        fields.add(field)

        return this
    }

    fun fillFields(vararg exceptions: String): CodecUI<T> {
        obj.data.forEach { (key, value) ->
            if (exceptions.contains(key)) return@forEach

            val type = if (value == null) FieldType.TEXT else FieldType.getCorrectType(value::class.java)
            val field = Field(
                key,
                type,
                { value, variable, cast ->
                    var old = obj.get<Any>(key)

                    old = if (old != null && old::class.java.isArray) {
                        var t = (old as Array<String>)

                        if (t.contains(value)) t = t.filter { it != value }.toTypedArray()
                        else {
                            t += value
                        }

                        t
                    } else {
                        try {
                            cast.invoke(value)
                        } catch (ignore: Exception) {
                            return@Field
                        }
                    }

                    println(old.toString())

                    if (variable.isEmpty()) obj.set(key, old)
                    else {
                        val o = obj.get<EasyConfig>(key)
                        o?.set(variable, old)
                    }
                },
                {
                    value?.let {
                        type.getter.invoke(
                            obj,
                            key,
                            it::class.java
                        )
                    } ?: ""
                }
            )

            addField(field)
        }

        return this
    }

    fun open(playerRef: PlayerRef,
             store: Store<EntityStore>) {
        val mainGroup = GroupBuilder.group()
            .withLayoutMode(LayoutModeSupported.LayoutMode.TopScrolling)

        val pageBuilder = PageBuilder
            .pageForPlayer(playerRef)
            .withLifetime(CustomPageLifetime.CanDismiss)
            .addElement(
                ContainerBuilder.container()
                    .withTitleText(label)
                    .withAnchor(HyUIAnchor().setWidth(900).setHeight(600))
                    .addContentChild(
                        mainGroup
                    )
            )

        val page = pageBuilder.open(store)

        fields.forEach {
            mainGroup
                .addChild(
                    GroupBuilder.group()
                        .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                        .withAnchor(HyUIAnchor().setBottom(10))
//                        .addChild(
//                            LabelBuilder.label()
//                                .withText("${it.key}:")
//                                .withAnchor(HyUIAnchor().setRight(10))
//                        )
                        .addChild(
                            it.getGroup(UIPlayer(playerRef, store, page))
                        )
                )
        }

        page.updatePage(true)
    }
}
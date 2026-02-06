package core.tastycake.ui.codec

import au.ellie.hyui.builders.ContainerBuilder
import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.PageBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import core.tastycake.config.EasyConfig
import core.tastycake.ui.UIPlayer

/**
 * @author TastyCake
 * @date 2/5/2026
 */

class CodecUI<T: EasyConfig>(
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

    fun fillFields(): CodecUI<T> {
        obj.data.forEach { (key, value) ->
            val type = if (value is ItemStack) FieldType.ITEM else FieldType.TEXT
            val field = Field(
                key,
                type,
                { value ->
                    obj.set(key, value)
                },
                {
                    value?.let {
                        when (it) {
                            is ItemStack ->
                                obj.get(key, ItemStack::class.java)?.itemId ?: ""

                            else ->
                                obj.get(key, it::class.java)?.toString() ?: ""
                        }
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
                    .withLayoutMode(LayoutModeSupported.LayoutMode.TopScrolling)
                    .addContentChild(
                        mainGroup
                    )
            )

        val page = pageBuilder.open(store)

        fields.forEach {
            mainGroup
                .addChild(it.getGroup(UIPlayer(playerRef, store, page)))
        }

        page.updatePage(true)
    }
}
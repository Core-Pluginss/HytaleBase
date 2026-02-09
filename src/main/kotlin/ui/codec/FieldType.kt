package core.tastycake.ui.codec

import au.ellie.hyui.builders.ButtonBuilder
import au.ellie.hyui.builders.DropdownBoxBuilder
import au.ellie.hyui.builders.GroupBuilder
import au.ellie.hyui.builders.HyUIAnchor
import au.ellie.hyui.builders.HyUIPadding
import au.ellie.hyui.builders.HyUIPatchStyle
import au.ellie.hyui.builders.LabelBuilder
import au.ellie.hyui.builders.TextFieldBuilder
import au.ellie.hyui.elements.LayoutModeSupported
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo
import com.hypixel.hytale.server.core.ui.LocalizableString
import core.tastycake.config.EasyConfig
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
    val use: (Class<*>) -> Boolean,
    val getter: (EasyConfig, String, Class<*>) -> Any,
    val cast: (String) -> Any,
    val group: (String, Any, UIPlayer, FieldInputCallback) -> GroupBuilder
) {
    TEXT(
        { _ ->
            false
        },
        { obj, key, c ->
            obj.get(key, c)?.toString()?: ""
        },
        { v ->
            v
        },
        { _, value, _, callback ->
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
    INTEGER(
        { c ->
            c == Int::class.javaPrimitiveType || c == Int::class.javaObjectType
        },
        { obj, key, c ->
            obj.get(key, c)?: 0
        },
        { v ->
            v.toInt()
        },
        { _, value, _, callback ->
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
        { c ->
            ItemStack::class.java.isAssignableFrom(c)
        },
        { obj, key, _ ->
            obj.get(key, ItemStack::class.java)?.itemId ?: ""
        },
        { v ->
            v
        },
        { _, value, player, callback ->
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
        { c ->
            Vector3::class.java.isAssignableFrom(c)
        },
        { obj, key, _ ->
            obj.get(key, Vector3::class.java)?: Vector3()
        },
        { v ->
            v.toDouble()
        },
        { _, value, _, callback ->
            try {
                val vector = value as Vector3

                GroupBuilder.group()
                    .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                    .addChild(
                        TextFieldBuilder.textInput()
                            .withPlaceholderText("x")
                            .withAnchor(HyUIAnchor().setWidth(100).setHeight(40).setRight(5))
                            .withPadding(HyUIPadding(10, 10, 20, 10))
                            .withValue(vector.getOrDefault("X", 0.0).toString())
                            .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                                callback.input(v.toString(), "X")
                            }
                    )
                    .addChild(
                        TextFieldBuilder.textInput()
                            .withPlaceholderText("y")
                            .withAnchor(HyUIAnchor().setWidth(100).setHeight(40))
                            .withPadding(HyUIPadding(10, 10, 20, 10).setRight(5))
                            .withValue(vector.getOrDefault("Y", 0.0).toString())
                            .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                                callback.input(v.toString(), "Y")
                            }
                    )
                    .addChild(
                        TextFieldBuilder.textInput()
                            .withPlaceholderText("z")
                            .withAnchor(HyUIAnchor().setWidth(100).setHeight(40))
                            .withPadding(HyUIPadding(10, 10, 20, 10))
                            .withValue(vector.getOrDefault("Z", 0.0).toString())
                            .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                                callback.input(v.toString(), "Z")
                            }
                    )
            } catch (e: Exception) {
                GroupBuilder.group()
            }
        }
    ),
    BOOLEAN(
        { c ->
            c == Boolean::class.javaPrimitiveType ||
                    c == Boolean::class.javaObjectType
        },
        { obj, key, _ ->
            obj.get(key, Boolean::class.java)?: false
        },
        { v ->
            v.toBoolean()
        },
        { _, value, player, callback ->
            GroupBuilder.group()
        }
    ),
    STRING_DROPDOWN(
    { c ->
        c.isArray && c.componentType == String::class.java
    },
    { obj, key, _ ->
        obj.get(key, Array<String>::class.java)?: arrayOf<String>()
    },
    { v ->
        v
    },
    { key, value, player, callback ->
        val regex = Regex("""^([A-Za-z]+)(\d+)(.+)$""")
        val match = regex.matchEntire(key)

        if (match == null) {
            GroupBuilder.group()
        } else {
            val key = match.groupValues[1]
            val number = match.groupValues[2].toInt()
            val className = match.groupValues[3]

            val dropdown = DropdownBoxBuilder.dropdownBox()
//                .withSelectedValues(
//                    (value as Array<String>).toList()
//                )
                .withMaxSelection(
                    let {
                        if (number == 0) {
                            (value as Array<String>).size + 1
                        } else {
                            number
                        }
                    }
                )
                .addEventListener(CustomUIEventBindingType.ValueChanged, String::class.java) { v ->
                    callback.input(v.toString())
                }

            val clazz = loadClassSmart(className)

            if (clazz.isEnum) {
                clazz.enumConstants.forEach { enumValue ->
                    val name = (enumValue as Enum<*>).name

                    dropdown.addEntry(
                        DropdownEntryInfo(
                            LocalizableString.fromString(name),
                            name
                        )
                    )
                }
            }

            GroupBuilder.group()
                .withLayoutMode(LayoutModeSupported.LayoutMode.Left)
                .addChild(
                    dropdown
                )
        }
    }
    )
    ;

    companion object {
        fun getCorrectType(c: Class<*>): FieldType {
            entries.forEach { entry ->
                if (entry.use.invoke(c)) return entry
            }

            return TEXT
        }

        fun loadClassSmart(className: String): Class<*> {
            val loaders = listOfNotNull(
                Thread.currentThread().contextClassLoader,
                ClassLoader.getSystemClassLoader(),
                this::class.java.classLoader
            ).distinct()

            var last: Throwable? = null

            for (cl in loaders) {
                try {
                    return Class.forName(className, true, cl)
                } catch (t: Throwable) {
                    last = t
                }
            }

            try {
                return Class.forName(className)
            } catch (t: Throwable) {
                last = t
            }

            throw ClassNotFoundException("Could not load class: $className", last)
        }
    }
}
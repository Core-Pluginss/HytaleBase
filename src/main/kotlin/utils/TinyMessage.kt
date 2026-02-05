package core.tastycake.utils

import com.hypixel.hytale.protocol.MaybeBool
import com.hypixel.hytale.server.core.Message
import java.awt.Color
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayDeque
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.dropLastWhile
import kotlin.collections.toTypedArray
import kotlin.math.max
import kotlin.math.min


/**
 * @author TastyCake
 * @date 1/24/2026
 */

object TinyMessage {
    val TAG_PATTERN: Pattern = Pattern.compile("<(/?)([a-zA-Z0-9_]+)(?::([^>]+))?>")

    val NAMED_COLORS = mutableMapOf<String, Color>()

    init {
        NAMED_COLORS["black"] = Color (0, 0, 0)
        NAMED_COLORS["dark_blue"] = Color (0, 0, 170)
        NAMED_COLORS["dark_green"] = Color (0, 170, 0)
        NAMED_COLORS["dark_aqua"] = Color (0, 170, 170)
        NAMED_COLORS["dark_red"] = Color (170, 0, 0)
        NAMED_COLORS["dark_purple"] = Color (170, 0, 170)
        NAMED_COLORS["gold"] = Color (255, 170, 0)
        NAMED_COLORS["gray"] = Color (170, 170, 170)
        NAMED_COLORS["dark_gray"] = Color (85, 85, 85)
        NAMED_COLORS["blue"] = Color (85, 85, 255)
        NAMED_COLORS["green"] = Color (85, 255, 85)
        NAMED_COLORS["aqua"] = Color (85, 255, 255)
        NAMED_COLORS["red"] = Color (255, 85, 85)
        NAMED_COLORS["light_purple"] = Color (255, 85, 255)
        NAMED_COLORS["yellow"] = Color (255, 255, 85)
        NAMED_COLORS["white"] = Color (255, 255, 255)
    }


    private class StyleState(
        color: Color?,
        gradient: List<Color>?,
        val bold: Boolean,
        val italic: Boolean,
        val underlined: Boolean,
        val monospace: Boolean, val link: String?
    ) {
        constructor() : this(null, null, false, false, false, false, null)

        fun copy(): StyleState {
            return StyleState(color, gradient, bold, italic, underlined, monospace, link)
        }

        fun withColor(color: Color?): StyleState {
            return StyleState(color, null, bold, italic, underlined, monospace, link)
        }

        fun withGradient(gradient: List<Color>?): StyleState {
            return StyleState(null, gradient, bold, italic, underlined, monospace, link)
        }

        fun withBold(bold: Boolean): StyleState {
            return StyleState(color, gradient, bold, italic, underlined, monospace, link)
        }

        fun withItalic(italic: Boolean): StyleState {
            return StyleState(color, gradient, bold, italic, underlined, monospace, link)
        }

        fun withUnderlined(underlined: Boolean): StyleState {
            return StyleState(color, gradient, bold, italic, underlined, monospace, link)
        }

        fun withMonospace(monospace: Boolean): StyleState {
            return StyleState(color, gradient, bold, italic, underlined, monospace, link)
        }

        fun withLink(link: String?): StyleState {
            return StyleState(color, gradient, bold, italic, underlined, monospace, link)
        }

        val color: Color? = color
        val gradient: List<Color>? = gradient
    }

    /**
     * Parses a string containing TinyMsg formatting tags and converts it into a Hytale Message.
     *
     *
     * This method processes all supported tags including colors, gradients, styles, and links.
     * Tags can be nested indefinitely for complex formatting.
     *
     *
     * @param text The string to parse, containing TinyMsg formatting tags
     * @return A formatted [Message] object ready to be sent to players
     * @throws NullPointerException if text is null
     * @see Message
     */
    fun parse(text: String): Message {
        if (!text.contains("<")) {
            return Message.raw(text)
        }

        val root: Message = Message.empty()

        // Stack keeps track of nested styles.
        // Example: Stack = [Base, Bold, Bold+Red]
        val stateStack = ArrayDeque<StyleState>()
        stateStack.addFirst(StyleState()) // Start with default empty state

        val matcher: Matcher = TAG_PATTERN.matcher(text)
        var lastIndex = 0

        while (matcher.find()) {
            val start: Int = matcher.start()
            val end: Int = matcher.end()

            // Handle text BEFORE this tag (using the state at the top of the stack)
            if (start > lastIndex) {
                val content = text.substring(lastIndex, start)
                val segmentMsg: Message = createStyledMessage(content, stateStack.first())
                root.insert(segmentMsg)
            }

            // Process the tag to update the Stack
            val isClosing = "/" == matcher.group(1)
            val tagName: String? = matcher.group(2)?.lowercase()
            val tagArg: String? = matcher.group(3)

            if (isClosing) {
                if (stateStack.size > 1) {
                    stateStack.removeFirst()
                }
            } else {
                // Start with the current state, and modify it
                val currentState = stateStack.first()
                var newState = currentState.copy()

                // If checking named colors directly
                if (NAMED_COLORS.containsKey(tagName)) {
                    newState = newState.withColor(NAMED_COLORS[tagName])
                } else {
                    when (tagName) {
                        "color", "c", "colour" -> {
                            val c: Color? = parseColorArg(tagArg)
                            if (c != null) newState = newState.withColor(c)
                        }

                        "grnt", "gradient" -> if (tagArg != null) {
                            val colors: List<Color> = parseGradientColors(tagArg)
                            if (!colors.isEmpty()) {
                                newState = newState.withGradient(colors)
                            }
                        }

                        "bold", "b" -> newState = newState.withBold(true)
                        "italic", "i", "em" -> newState = newState.withItalic(true)
                        "underline", "u" -> newState = newState.withUnderlined(true)
                        "monospace", "mono" -> newState = newState.withMonospace(true)
                        "link", "url" -> if (tagArg != null) newState = newState.withLink(tagArg)
                        "reset", "r" -> {
                            stateStack.clear()
                            newState = StyleState()
                        }
                    }
                }
                stateStack.addFirst(newState)
            }

            lastIndex = end
        }

        if (lastIndex < text.length) {
            val content = text.substring(lastIndex)
            val segmentMsg: Message = createStyledMessage(content, stateStack.first())
            root.insert(segmentMsg)
        }

        return root
    }

    private fun createStyledMessage(content: String, state: StyleState): Message {
        // If we have a gradient, we must return a container with char-by-char coloring
        if (state.gradient != null && !state.gradient.isEmpty()) {
            return applyGradient(content, state)
        }

        val msg: Message = Message.raw(content)

        if (state.color != null) msg.color(state.color)
        if (state.bold) msg.bold(true)
        if (state.italic) msg.italic(true)
        if (state.monospace) msg.monospace(true)
        if (state.underlined) msg.getFormattedMessage().underlined = MaybeBool.True
        if (state.link != null) msg.link(state.link)

        return msg
    }

    private fun applyGradient(text: String, state: StyleState): Message {
        val container: Message = Message.empty()
        val colors: List<Color> = state.gradient!!
        val length = text.length

        for (index in 0..<length) {
            val ch = text[index]
            val progress = index / max((length - 1).toDouble(), 1.0).toFloat()
            val color: Color = interpolateColor(colors, progress)

            val charMsg: Message = Message.raw(ch.toString()).color(color)

            if (state.bold) charMsg.bold(true)
            if (state.italic) charMsg.italic(true)
            if (state.monospace) charMsg.monospace(true)
            if (state.underlined) charMsg.getFormattedMessage().underlined = MaybeBool.True
            if (state.link != null) charMsg.link(state.link)

            container.insert(charMsg)
        }
        return container
    }

    private fun parseColorArg(arg: String?): Color? {
        if (arg == null) return null
        return if (NAMED_COLORS.containsKey(arg)) NAMED_COLORS[arg] else parseHexColor(arg)
    }

    private fun parseGradientColors(arg: String): List<Color> {
        val colors: MutableList<Color> = ArrayList<Color>()
        for (part in arg.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val c: Color? = parseColorArg(part)
            if (c != null) colors.add(c)
        }
        return colors
    }

    private fun parseHexColor(hex: String): Color? {
        try {
            val clean = hex.replace("#", "")
            if (clean.length == 6) {
                val r = clean.substring(0, 2).toInt(16)
                val g = clean.substring(2, 4).toInt(16)
                val b = clean.substring(4, 6).toInt(16)
                return Color(r, g, b)
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    private fun interpolateColor(colors: List<Color>, progress: Float): Color {
        val clampedProgress = max(0.0, min(1.0, progress.toDouble())).toFloat()
        val scaledProgress = clampedProgress * (colors.size - 1)
        val index = min(scaledProgress.toInt().toDouble(), (colors.size - 2).toDouble()).toInt()
        val localProgress = scaledProgress - index

        val c1: Color = colors[index]
        val c2: Color = colors[index + 1]

        val r = (c1.red + (c2.red - c1.red) * localProgress).toInt()
        val g = (c1.green + (c2.green - c1.green) * localProgress).toInt()
        val b = (c1.blue + (c2.blue - c1.blue) * localProgress).toInt()

        return Color(r, g, b)
    }
}
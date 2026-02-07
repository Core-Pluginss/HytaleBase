package core.tastycake.ui.runnables

/**
 * @author TastyCake
 * @date 1/27/2026
 */

enum class InputResult {
    COMPLETED,
    ACCEPTED,
    CANCELED
}

enum class InputType {
    TEXT,
    ITEM
}

interface InputCallback {
    fun result(input: String, result: InputResult)
}

interface FieldInputCallback {
    fun input(input: String, variable: String = "")
}
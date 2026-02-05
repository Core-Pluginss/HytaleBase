package core.tastycake.config

/**
 * @author TastyCake
 * @date 2/5/2026
 */

open class EasyConfig(
    val data: MutableMap<String, Any?> = mutableMapOf(),
) {
    fun <T> get(key: String): T? {
        val r = data.getOrDefault(key, null) ?: return null

        return r as T
    }

    fun <T> set(key: String, value: T) {
        data[key] = value
    }
}
package core.tastycake.config

/**
 * @author TastyCake
 * @date 2/5/2026
 */

@Suppress("UNCHECKED_CAST")
open class EasyConfig(
    val data: MutableMap<String, Any?> = mutableMapOf(),
) {
    fun <T> get(key: String): T? {
        val r = data.getOrDefault(key, null) ?: return null

        return r as T
    }

    fun <T> get(key: String, clazz: Class<T>): T? {
        val r = data.getOrDefault(key, null) ?: return null

        return clazz.cast(r)
    }

    fun <T> getOrDefault(key: String, default: T?): T? {
        val r = data.getOrDefault(key, null) ?: return default

        return r as T
    }

    fun <T> getOrDefault(key: String, clazz: Class<T>, default: T?): T? {
        val r = data.getOrDefault(key, null) ?: return default

        return clazz.cast(r)
    }

    fun <T> set(key: String, value: T): EasyConfig {
        data[key] = value

        return this
    }

    fun <T: EasyConfig> build(): T {
        return this as T
    }
}
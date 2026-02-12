package core.tastycake.config

import com.hypixel.hytale.server.core.util.Config

/**
 * @author TastyCake
 * @date 2/5/2026
 */

@Suppress("UNCHECKED_CAST")
open class EasyConfig(
    val data: MutableMap<String, Any?> = mutableMapOf(),
) {
    init {
        applyDefaults()
    }

    fun <T> get(key: String): T? {
        val r = data.getOrDefault(key, null) ?: return null

        return r as T
    }

    fun <T> get(key: String, clazz: Class<T>): T? {
        val r = data.getOrDefault(key, null) ?: return null

        return clazz.cast(r)
    }

    fun <T> getNonNull(key: String): T {
        val r = data.getOrDefault(key, null)!!

        return r as T
    }

    fun <T> getNonNull(key: String, clazz: Class<T>): T {
        val r = data.getOrDefault(key, null)!!

        return clazz.cast(r)
    }

    fun <T> getOrDefault(key: String, default: T): T {
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

    inline fun <reified T: EasyConfig, V> getOrNew(key: String,
                                                   value: V,
                                                   arrayKey: String,
                                                   config: Config<*>): T {
        var array = getOrDefault<Array<T>>(arrayKey, arrayOf())

        var data = array.firstOrNull { it.get<V>(key) == value }

        if (data == null) {
            data = T::class.java.newInstance()
            data.set(key, value)
            data.applyDefaults()

            array += data

            set(arrayKey, array)

            config.save()
        }

        return data
    }

    open fun applyDefaults() {

    }

    fun <T: EasyConfig> build(): T {
        return this as T
    }
}
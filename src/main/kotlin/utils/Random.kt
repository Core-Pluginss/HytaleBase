package core.tastycake.utils

import kotlin.math.max
import kotlin.random.Random

/**
 * @author TastyCake
 * @date 1/18/2026
 */

fun <T> Array<T>.weightedRandomOrNull(weightSelector: (T) -> Float): T? {
    if (isEmpty()) return null

    val weights = this.map { max(0f, weightSelector(it)) }
    val total = weights.sum()

    if (total <= 0f) return null

    val rnd = Random.nextFloat() * total
    var cumulative = 0f

    for (i in indices) {
        cumulative += weights[i]
        if (rnd < cumulative) {
            return this[i]
        }
    }

    return last()
}
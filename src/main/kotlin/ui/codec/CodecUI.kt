package core.tastycake.ui.codec

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.EmptyExtraInfo
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import org.bson.BsonDocument
import org.bson.BsonValue

/**
 * @author TastyCake
 * @date 2/5/2026
 */

class CodecUI<T>(
    val obj: T,
    val save: () -> Unit = {}
) {
    private val fields = mutableListOf<Field>()

    fun addField(field: Field): CodecUI<T> {
        fields.add(field)

        return this
    }

    fun open(playerRef: PlayerRef,
             store: Store<EntityStore>) {

    }
}
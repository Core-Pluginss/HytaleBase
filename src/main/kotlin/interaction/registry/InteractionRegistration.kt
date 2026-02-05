package core.tastycake.interaction.registry

import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

/**
 * @author TastyCake
 * @date 1/30/2026
 */

data class InteractionRegistration(
    var priority: Int = InteractionPriority.NORMAL,
    var handler: (player: Player, store: Store<EntityStore>, chain: SyncInteractionChain) -> Unit = { _, _, _ -> },
    var filter: (chain: SyncInteractionChain) -> Boolean = { _ -> true },
) {
    companion object {
        fun handle(handler: (player: Player, store: Store<EntityStore>, chain: SyncInteractionChain) -> Unit) : InteractionRegistration {
            val registration = InteractionRegistration()

            registration.handler = handler

            return registration
        }

        fun filter(filter: (chain: SyncInteractionChain) -> Boolean) : InteractionRegistration {
            val registration = InteractionRegistration()

            registration.filter = filter

            return registration
        }

        fun priority(priority: Int) : InteractionRegistration {
            val registration = InteractionRegistration()

            registration.priority = priority

            return registration
        }
    }

    fun handle(handler: (player: Player, store: Store<EntityStore>, chain: SyncInteractionChain) -> Unit) : InteractionRegistration {
        this.handler = handler

        return this
    }

    fun filter(filter: (chain: SyncInteractionChain) -> Boolean) : InteractionRegistration {
        this.filter = filter

        return this
    }

    fun priority(priority: Int) : InteractionRegistration {
        this.priority = priority

        return this
    }
}
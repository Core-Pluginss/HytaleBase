package core.tastycake.interaction

import com.hypixel.hytale.protocol.InteractionChainData
import com.hypixel.hytale.protocol.InteractionType
import com.hypixel.hytale.protocol.Packet
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.io.PacketHandler
import com.hypixel.hytale.server.core.io.adapter.PacketWatcher
import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler
import com.hypixel.hytale.server.core.universe.Universe
import me.tastycake.interaction.registry.InteractionRegistry

/**
 * @author TastyCake
 * @date 1/30/2026
 */

class InteractionWatcher : PacketWatcher {
    override fun accept(packetHandler: PacketHandler, packet: Packet) {
        if (packetHandler !is GamePacketHandler) return
        if (packet !is SyncInteractionChains) return
        if (packet.updates.isEmpty()) return

        val chain = packet.updates[0]
        val chainData = chain.data as? InteractionChainData ?: return

        if (chain.interactionType != InteractionType.Primary) return

        val playerRef = packetHandler.playerRef
        val worldUuid = playerRef.worldUuid ?: return

        val world = Universe.get().getWorld(worldUuid) ?: return
        val store = world.entityStore.store

        val ref = playerRef.reference ?: return
        val player = store.getComponent(ref, Player.getComponentType()) ?: return

        InteractionRegistry.getRegistrations().sortedBy { it.priority }.forEach { registration ->
            if (!registration.filter.invoke(chain)) return

            registration.handler.invoke(player, store, chain)
        }
    }
}
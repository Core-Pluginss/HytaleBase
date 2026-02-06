package core.tastycake.ui

import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

class UIPlayer(
    val playerRef: PlayerRef,
    val store: Store<EntityStore>,
    val lastUI: InteractiveCustomUIPage<*>? = null
) {
}
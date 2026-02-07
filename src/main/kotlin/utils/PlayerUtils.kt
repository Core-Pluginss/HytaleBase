package core.tastycake.utils

import com.hypixel.hytale.server.core.command.system.CommandSender
import com.hypixel.hytale.server.core.entity.entities.Player
import java.util.UUID

/**
 * @author TastyCake
 * @date 2/7/2026
 */

fun Player.getUUID(): UUID {
    return (this as CommandSender).uuid
}
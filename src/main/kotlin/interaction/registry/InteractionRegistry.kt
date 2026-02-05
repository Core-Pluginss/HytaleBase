package core.tastycake.interaction.registry

/**
 * @author TastyCake
 * @date 1/30/2026
 */

object InteractionRegistry {
    private val registrations = mutableListOf<InteractionRegistration>()

    public fun register(registration: InteractionRegistration) {
        registrations.add(registration)
    }

    public fun getRegistrations() : Set<InteractionRegistration> {
        return registrations.toSet()
    }
}
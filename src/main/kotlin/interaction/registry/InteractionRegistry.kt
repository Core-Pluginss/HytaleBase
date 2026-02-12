package core.tastycake.interaction.registry

/**
 * @author TastyCake
 * @date 1/30/2026
 */

object InteractionRegistry {
    private val registrations = mutableListOf<InteractionRegistration>()

    public fun register(registration: InteractionRegistration): InteractionRegistry {
        registrations.add(registration)

        return this
    }

    public fun getRegistrations() : Set<InteractionRegistration> {
        return registrations.toSet()
    }
}
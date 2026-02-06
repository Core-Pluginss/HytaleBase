package core.tastycake.leaderboard

/**
 * @author TastyCake
 * @date 2/6/2026
 */

object LeaderboardRegistry {
    private val leaderboards: MutableList<Leaderboard> = mutableListOf()

    fun register(leaderboard: Leaderboard) {
        leaderboards.add(leaderboard)
    }

    fun getByName(name: String): Leaderboard? {
        return leaderboards.firstOrNull { it.name == name }
    }

    fun getLeaderboards(): List<Leaderboard> {
        return leaderboards
    }
}
package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
import kotlinx.coroutines.flow.Flow

interface GameUseCase {

    suspend fun createGame(game: Game)

    fun getGames(): Flow<List<Game>>

    fun getGame(id: Int): Flow<Game>

    fun searchGamesByName(name: String): Flow<List<Game>>

    fun getGamesByQuery(query: GameQuery): Flow<List<Game>>

    fun findGames(friendIds: List<Int>, tagIds: List<Int>): Flow<List<Game>>

    suspend fun updateGame(game: Game)

    suspend fun deleteGame(id: Int)
}
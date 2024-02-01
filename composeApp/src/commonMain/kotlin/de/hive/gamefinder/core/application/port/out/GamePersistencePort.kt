package de.hive.gamefinder.core.application.port.out

import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
import kotlinx.coroutines.flow.Flow

interface GamePersistencePort {

    suspend fun createGame(game: Game)

    fun getGames(): Flow<List<Game>>

    fun getGame(id: Int): Flow<Game?>

    fun getGamesByName(name: String): Flow<List<Game>>

    fun getGamesByQuery(query: GameQuery): Flow<List<Game>>

    suspend fun updateGame(game: Game)

    suspend fun deleteGame(id: Int)
}
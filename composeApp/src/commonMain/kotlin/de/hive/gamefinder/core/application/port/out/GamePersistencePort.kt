package de.hive.gamefinder.core.application.port.out

import de.hive.gamefinder.core.domain.Game
import kotlinx.coroutines.flow.Flow

interface GamePersistencePort {

    suspend fun createGame(game: Game)

    fun getGames(): Flow<List<Game>>

    fun getGame(id: Int): Flow<Game?>

    fun searchGames(searchQuery: String): List<Game>

    suspend fun updateGame(game: Game)

    suspend fun deleteGame(id: Int)
}
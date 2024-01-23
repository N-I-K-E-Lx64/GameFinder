package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.Game
import kotlinx.coroutines.flow.Flow

interface GameUseCase {

    suspend fun createGame(game: Game)

    suspend fun getGames(): Flow<List<Game>>

    fun getGame(id: Int): Flow<Game>

    suspend fun updateGame(game: Game)

    suspend fun deleteGame(id: Int)
}
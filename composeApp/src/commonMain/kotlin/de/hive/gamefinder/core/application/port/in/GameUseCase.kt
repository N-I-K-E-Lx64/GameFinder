package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
import de.hive.gamefinder.core.domain.GameStatus
import de.hive.gamefinder.core.domain.MultiplayerMode
import kotlinx.coroutines.flow.Flow

interface GameUseCase {

    suspend fun createGame(game: Game)

    fun getGames(): Flow<List<Game>>

    fun getGame(id: Int): Flow<Game?>

    fun getGamesOnShortlist(): Flow<List<Game>>

    fun searchGamesByName(name: String): Flow<List<Game>>

    fun getGamesByQuery(query: GameQuery): Flow<List<Game>>

    fun findGames(friendIds: List<Int>, tagIds: List<Int>?): Flow<List<Game>>

    suspend fun updateMultiplayerMode(gameId: Int, multiplayerMode: MultiplayerMode)

    suspend fun updateGameStatus(gameId: Int, status: GameStatus)

    suspend fun updateShortlistPosition(shortlistUpdate: List<Game>)

    suspend fun updateShortlistStatus(gameId: Int, addToShortlist: Boolean)

    suspend fun deleteGame(id: Int)
}
package de.hive.gamefinder.core.application.port.out

import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
import de.hive.gamefinder.core.domain.GameStatus
import de.hive.gamefinder.core.domain.MultiplayerMode
import kotlinx.coroutines.flow.Flow

interface GamePersistencePort {

    suspend fun createGame(game: Game)

    fun getGames(): Flow<List<Game>>

    fun getGame(id: Int): Flow<Game?>

    fun getGamesByName(name: String): Flow<List<Game>>

    fun getGamesByQuery(query: GameQuery): Flow<List<Game>>

    fun getGamesOnShortlist(): Flow<List<Game>>

    fun findGamesByFriendsAndTags(friendIds: List<Int>, tagIds: List<Int>): Flow<List<Game>>

    fun findGamesByFriends(friendIds: List<Int>): Flow<List<Game>>

    suspend fun updateMultiplayerMode(gameId: Int, multiplayerMode: MultiplayerMode)

    suspend fun updateGameStatus(gameId: Int, status: GameStatus)

    suspend fun addGameToShortlist(gameId: Int)

    suspend fun removeGameFromShortlist(gameId: Int)

    suspend fun updateShortlistPosition(gameId: Int, shortlistPosition: Int)

    suspend fun deleteGame(id: Int)
}
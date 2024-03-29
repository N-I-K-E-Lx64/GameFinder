package de.hive.gamefinder.core.application

import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
import de.hive.gamefinder.core.domain.GameStatus
import de.hive.gamefinder.core.domain.MultiplayerMode
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

class GameService(private val persistencePort: GamePersistencePort) : GameUseCase {
    override suspend fun createGame(game: Game) {
        persistencePort.createGame(game)
    }

    override fun getGames(): Flow<List<Game>> {
        return persistencePort.getGames()
    }

    override fun getGame(id: Int): Flow<Game?> {
        return persistencePort.getGame(id)
    }

    override fun searchGamesByName(name: String): Flow<List<Game>> {
        return persistencePort.getGamesByName(name)
    }

    override fun getGamesByQuery(query: GameQuery): Flow<List<Game>> {
        return persistencePort.getGamesByQuery(query)
    }

    override fun findGames(friendIds: List<Int>, tagIds: List<Int>?): Flow<List<Game>> {
        return if (tagIds.isNullOrEmpty()) {
            Napier.d { "Searching games for friends $friendIds" }
            persistencePort.findGamesByFriends(friendIds)
        } else {
            Napier.d { "Searching games for friends $friendIds. Tags $tagIds are deselected"}
            persistencePort.findGamesByFriendsAndTags(friendIds, tagIds)
        }
    }

    override suspend fun updateMultiplayerMode(gameId: Int, multiplayerMode: MultiplayerMode) {
        persistencePort.updateMultiplayerMode(gameId, multiplayerMode)
    }

    override suspend fun updateGameStatus(gameId: Int, status: GameStatus) {
        persistencePort.updateGameStatus(gameId, status)
    }

    override suspend fun addGameToShortlist(gameId: Int) {
        persistencePort.addGameToShortlist(gameId)
    }

    override suspend fun deleteGame(id: Int) {
        persistencePort.deleteGame(id)
    }
}
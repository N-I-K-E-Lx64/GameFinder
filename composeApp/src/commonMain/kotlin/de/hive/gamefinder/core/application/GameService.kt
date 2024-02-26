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

    override fun getGamesOnShortlist(): Flow<List<Game>> {
        return persistencePort.getGamesOnShortlist()
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

    override suspend fun updateShortlistPosition(shortlistUpdate: List<Game>) {
        shortlistUpdate
            .filterIndexed { index, game -> game.shortlistPosition != index }
            .forEach { persistencePort.updateShortlistPosition(it.id, shortlistUpdate.indexOf(it)) }
    }

    override suspend fun updateShortlistStatus(gameId: Int, addToShortlist: Boolean) {
        if (addToShortlist) persistencePort.addGameToShortlist(gameId) else persistencePort.removeGameFromShortlist(gameId)
    }

    override suspend fun deleteGame(id: Int) {
        persistencePort.deleteGame(id)
    }
}
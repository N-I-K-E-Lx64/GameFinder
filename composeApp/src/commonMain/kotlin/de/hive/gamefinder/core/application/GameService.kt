package de.hive.gamefinder.core.application

import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
import de.hive.gamefinder.core.domain.Platform
import de.hive.gamefinder.core.domain.QueryType
import kotlinx.coroutines.flow.Flow

class GameService(private val persistencePort: GamePersistencePort) : GameUseCase {
    override suspend fun createGame(game: Game) {
        persistencePort.createGame(game)
    }

    override fun getGames(): Flow<List<Game>> {
        return persistencePort.getGames()
    }

    override fun getGame(id: Int): Flow<Game> {
        TODO("Not yet implemented")
    }

    override fun getGamesByQuery(query: GameQuery): Flow<List<Game>> {
        return when (query.queryType) {
            QueryType.NAME -> persistencePort.getGamesByName(query.value as String)
            QueryType.PLATFORM -> persistencePort.getGamesByPlatform(query.value as Platform)
        }
    }

    override suspend fun updateGame(game: Game) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGame(id: Int) {
        TODO("Not yet implemented")
    }
}
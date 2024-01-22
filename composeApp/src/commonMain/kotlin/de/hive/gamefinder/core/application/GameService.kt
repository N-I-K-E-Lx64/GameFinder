package de.hive.gamefinder.core.application

import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.domain.Game
import kotlinx.coroutines.flow.Flow

class GameService(private val persistencePort: GamePersistencePort) : GameUseCase {
    override suspend fun createGame(game: Game) {
        persistencePort.createGame(game)
    }

    override suspend fun getGames(): Flow<List<Game>> {
        return persistencePort.getGames()
    }

    override fun getGame(id: Int): Flow<Game> {
        TODO("Not yet implemented")
    }

    override suspend fun updateGame(game: Game) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGame(id: Int) {
        TODO("Not yet implemented")
    }
}
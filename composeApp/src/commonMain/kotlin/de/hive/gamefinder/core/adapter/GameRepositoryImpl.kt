package de.hive.gamefinder.core.adapter

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Platform
import de.hive.gamefinder.database.GameFinderDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(database: GameFinderDatabase) : GamePersistencePort {
    private val dbQueries = database.gameQueries

    override suspend fun createGame(game: Game) {
        dbQueries.addGame(game.toEntity())
    }

    override fun getGames(): Flow<List<Game>> {
        return dbQueries
            .getAllGames()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { games -> games.map { gameEntity -> gameEntity.toModel() } }
    }

    override fun getGame(id: Int): Flow<Game?> {
        return dbQueries
            .getGameById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toModel() }
    }

    override fun getGamesByName(name: String): Flow<List<Game>> {
        return dbQueries
            .searchGamesByName(query = name)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { games -> games.map { it.toModel() } }
    }

    override fun getGamesByPlatform(platform: Platform): Flow<List<Game>> {
        return dbQueries
            .getGamesByPlatform(platform.ordinal)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { games -> games.map { it.toModel() } }
    }

    override suspend fun updateGame(game: Game) {
        game.toEntity().let {
            dbQueries.updateGame(name = it.name, platform = it.platform)
        }
    }

    override suspend fun deleteGame(id: Int) {
        dbQueries.deleteGameById(id)
    }
}
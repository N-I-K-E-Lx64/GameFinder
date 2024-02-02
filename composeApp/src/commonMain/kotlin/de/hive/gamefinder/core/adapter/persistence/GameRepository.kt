package de.hive.gamefinder.core.adapter.persistence

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
import de.hive.gamefinder.core.domain.MultiplayerMode
import de.hive.gamefinder.core.domain.Tag
import de.hive.gamefinder.database.GameFinderDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(database: GameFinderDatabase) : GamePersistencePort {
    private val dbQueries = database.gameFinderQueries

    override suspend fun createGame(game: Game) {
        dbQueries.addGame(game.toEntity())
    }

    override fun getGames(): Flow<List<Game>> {
        return dbQueries
            .getAllGames()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.id }.values.map { games ->
                    val firstGame = games.first()

                    Game(
                        id = firstGame.id,
                        name = firstGame.name,
                        platform = firstGame.platform,
                        igdbGameId = firstGame.game_id,
                        coverImageId = firstGame.cover_image_id,
                        tags = games.filter { it.tag != null }.map { Tag(it.id_!!, it.tag!!) },
                        gameModes = firstGame.game_modes,
                        multiplayerMode = MultiplayerMode(
                            hasCampaignCoop = firstGame.campaign_coop ?: false,
                            hasOnlineCoop = firstGame.online_coop ?: false,
                            onlineCoopMaxPlayers = firstGame.online_max_players ?: 0
                        )
                    )
                }
            }
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

    override fun getGamesByQuery(query: GameQuery): Flow<List<Game>> {
        return dbQueries
            .getGamesByQuery(query.platform, query.onlineCoop, query.campaignCoop)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { games -> games.map { it.toModel() } }
    }

    override fun findGames(friendIds: List<Int>, tagIds: List<Int>): Flow<List<Game>> {
        return dbQueries
            .findMultiplayerGames(
                friendCount = friendIds.size,
                friendIds.map { it.toLong() },
                tagIds.map { it.toLong() }
            )
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
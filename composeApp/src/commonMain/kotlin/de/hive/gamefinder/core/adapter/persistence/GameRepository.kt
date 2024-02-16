package de.hive.gamefinder.core.adapter.persistence

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.domain.*
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
                        summary = firstGame.summary,
                        launcher = firstGame.launcher,
                        igdbGameId = firstGame.game_id,
                        coverImageId = firstGame.cover_image_id,
                        tags = games.filter { it.tag != null }.map { Tag(it.id_!!, it.tag!!) },
                        gameModes = firstGame.game_modes,
                        multiplayerMode = MultiplayerMode(
                            hasCampaignCoop = firstGame.campaign_coop ?: false,
                            hasOnlineCoop = firstGame.online_coop ?: false,
                            onlineCoopMaxPlayers = firstGame.online_max_players ?: 0
                        ),
                        isShortlist = firstGame.shortlist,
                        gameStatus = firstGame.game_status
                    )
                }
            }
    }

    override fun getGame(id: Int): Flow<Game?> {
        return dbQueries
            .getGameById(id)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows ->
                rows.groupBy { it.id }.firstNotNullOfOrNull { entry ->
                    val gameInformation = entry.value.first()
                    Game(
                        id = gameInformation.id,
                        name = gameInformation.name,
                        launcher = gameInformation.launcher,
                        igdbGameId = gameInformation.game_id,
                        coverImageId = gameInformation.cover_image_id,
                        tags = entry.value.filter { it.id_ != null }.map { Tag(it.id_!!, it.tag!!) },
                        gameModes = gameInformation.game_modes,
                        multiplayerMode = MultiplayerMode(
                            hasCampaignCoop = gameInformation.campaign_coop ?: false,
                            hasOnlineCoop = gameInformation.online_coop ?: false,
                            onlineCoopMaxPlayers = gameInformation.online_max_players ?: 0
                        ),
                        isShortlist = gameInformation.shortlist,
                        gameStatus = gameInformation.game_status,
                        summary = gameInformation.summary
                    )
                }
            }
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
            .getGamesByQuery(query.launcher, query.onlineCoop, query.campaignCoop)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { games -> games.map { it.toModel() } }
    }

    override fun findGamesByFriendsAndTags(friendIds: List<Int>, tagIds: List<Int>): Flow<List<Game>> {
        return dbQueries
            .findMultiplayerGamesByFriendsAndTags(
                friendCount = friendIds.size,
                friendIds.map { it.toLong() },
                tagIds.map { it.toLong() }
            )
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { games -> games.map { it.toModel() } }
    }

    override fun findGamesByFriends(friendIds: List<Int>): Flow<List<Game>> {
        return dbQueries
            .findMultiplayerGamesByFriends(
                friendCount = friendIds.size,
                friendIds.map { it.toLong() }
            )
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { games -> games.map { it.toModel() } }
    }

    override suspend fun updateMultiplayerMode(gameId: Int, multiplayerMode: MultiplayerMode) {
        dbQueries
            .updateMultiplayerParameters(
                gameId = gameId,
                onlineCoop = multiplayerMode.hasOnlineCoop,
                campaignCoop = multiplayerMode.hasCampaignCoop,
                onlineMaxPlayers = multiplayerMode.onlineCoopMaxPlayers
            )
    }

    override suspend fun updateGameStatus(gameId: Int, status: GameStatus) {
        dbQueries
            .updateGameStatus(gameId = gameId, status = status)
    }

    override suspend fun addGameToShortlist(gameId: Int) {
        dbQueries
            .addGameToShortlist(gameId = gameId)
    }

    override suspend fun deleteGame(id: Int) {
        dbQueries.deleteGameById(id)
    }
}
package de.hive.gamefinder.core.adapter.persistence

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import de.hive.gamefinder.core.application.port.out.FriendPersistencePort
import de.hive.gamefinder.core.domain.Friend
import de.hive.gamefinder.core.domain.FriendGameRelation
import de.hive.gamefinder.database.GameFinderDatabase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FriendRepository(database: GameFinderDatabase) : FriendPersistencePort {
    private val dbQueries = database.gameFinderQueries

    override suspend fun createFriend(friend: Friend) {
        dbQueries.createFriend(friend.toEntity())
    }

    override fun getFriends(): Flow<List<Friend>> {
        return dbQueries
            .getAllFriends()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { friends -> friends.map { it.toModel() } }
    }

    override fun getFriendsByGame(gameId: Int): Flow<List<FriendGameRelation>> {
        return dbQueries
            .getFriendsByGame(gameId.toLong(), mapper = { id, name, owning -> FriendGameRelation(id, name, owning.toBoolean()) })
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { result -> result.map { it } }
    }

    override fun getFriendByName(friendName: String): Friend? {
        val entity = dbQueries
            .getFriendByName(friendName)
            .executeAsOneOrNull()
        return entity?.toModel()
    }

    override suspend fun createGameFriendRelation(gameId: Int, friendId: Int) {
        dbQueries
            .createGameFriendRelation(gameId = gameId.toLong(), friendId = friendId.toLong())
        Napier.i("Add friend $friendId to game $gameId")
    }

    override suspend fun deleteGameFriendRelation(gameId: Int, friendId: Int) {
        dbQueries
            .removeGameFriendRelation(gameId = gameId.toLong(), friendId = friendId.toLong())
        Napier.i("Remove friend $friendId from game $gameId")
    }
}
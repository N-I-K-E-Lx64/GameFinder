package de.hive.gamefinder.core.application

import de.hive.gamefinder.core.adapter.objects.GameFriendRelation
import de.hive.gamefinder.core.application.port.`in`.FriendUseCase
import de.hive.gamefinder.core.application.port.out.FriendPersistencePort
import de.hive.gamefinder.core.domain.Friend
import kotlinx.coroutines.flow.Flow

class FriendService(private val persistencePort: FriendPersistencePort) : FriendUseCase {

    override suspend fun addFriend(friend: Friend) {
        persistencePort.createFriend(friend)
    }

    override fun getFriends(): Flow<List<Friend>> {
        return persistencePort.getFriends()
    }

    override fun getFriendByGame(gameId: Int): Flow<List<GameFriendRelation>> {
        return persistencePort.getFriendsByGame(gameId)
    }

    override fun checkFriendExistence(friendName: String): Boolean {
        return persistencePort.getFriendByName(friendName) != null
    }

    override suspend fun changeGameFriendRelation(gameId: Int, friendId: Int, update: Boolean) {
        if (update) persistencePort.createGameFriendRelation(gameId, friendId) else persistencePort.deleteGameFriendRelation(gameId, friendId)
    }
}
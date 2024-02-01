package de.hive.gamefinder.core.application.port.out

import de.hive.gamefinder.core.adapter.objects.GameFriendRelation
import de.hive.gamefinder.core.domain.Friend
import kotlinx.coroutines.flow.Flow

interface FriendPersistencePort {

    suspend fun createFriend(friend: Friend)

    fun getFriends(): Flow<List<Friend>>

    fun getFriendsByGame(gameId: Int): Flow<List<GameFriendRelation>>

    suspend fun createGameFriendRelation(gameId: Int, friendId: Int)

    suspend fun deleteGameFriendRelation(gameId: Int, friendId: Int)
}
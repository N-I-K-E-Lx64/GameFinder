package de.hive.gamefinder.core.application.port.out

import de.hive.gamefinder.core.domain.Friend
import de.hive.gamefinder.core.domain.FriendGameRelation
import kotlinx.coroutines.flow.Flow

interface FriendPersistencePort {

    suspend fun createFriend(friend: Friend)

    fun getFriends(): Flow<List<Friend>>

    fun getFriendsByGame(gameId: Int): Flow<List<FriendGameRelation>>

    fun getFriendByName(friendName: String): Friend?

    suspend fun createGameFriendRelation(gameId: Int, friendId: Int)

    suspend fun deleteGameFriendRelation(gameId: Int, friendId: Int)
}
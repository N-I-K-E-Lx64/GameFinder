package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.adapter.objects.GameFriendRelation
import de.hive.gamefinder.core.domain.Friend
import kotlinx.coroutines.flow.Flow

interface FriendUseCase {

    suspend fun addFriend(friend: Friend)

    fun getFriends(): Flow<List<Friend>>

    fun getFriendByGame(gameId: Int): Flow<List<GameFriendRelation>>

    fun checkFriendExistence(friendName: String): Boolean

    suspend fun changeGameFriendRelation(gameId: Int, friendId: Int, update: Boolean)
}
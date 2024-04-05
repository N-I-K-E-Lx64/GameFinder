package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.Friend
import de.hive.gamefinder.core.domain.FriendGameRelation
import kotlinx.coroutines.flow.Flow

interface FriendUseCase {

    suspend fun addFriend(friend: Friend)

    fun getFriends(): Flow<List<Friend>>

    fun getFriendByGame(gameId: Int): Flow<List<FriendGameRelation>>

    fun checkFriendExistence(friendName: String): Boolean

    suspend fun changeGameFriendRelation(gameId: Int, friendId: Int, update: Boolean)
}
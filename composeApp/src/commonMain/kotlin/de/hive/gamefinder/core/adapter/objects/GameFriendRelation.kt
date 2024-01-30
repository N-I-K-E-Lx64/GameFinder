package de.hive.gamefinder.core.adapter.objects

data class GameFriendRelation(
    val friendId: Int,
    val gameId: Int?,
    val name: String,
    val doesFriendOwnGame: Boolean
)

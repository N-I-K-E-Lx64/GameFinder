package de.hive.gamefinder.core.domain

data class Game(
    val id: Int = 0,
    val name: String,
    val platform: Platform = Platform.STEAM,
    val igdbGameId: Int,
    val coverImageId: String,
    val tags: List<Tag> = emptyList(),
    val gameModes: List<GameMode>?,
    val multiplayerMode: MultiplayerMode?
)
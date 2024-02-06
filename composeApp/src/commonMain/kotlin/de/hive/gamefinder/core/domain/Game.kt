package de.hive.gamefinder.core.domain

data class Game(
    val id: Int = 0,
    val name: String,
    val summary: String,
    val launcher: Launcher = Launcher.STEAM,
    val igdbGameId: Int,
    val coverImageId: String,
    val tags: List<Tag> = emptyList(),
    val gameModes: List<GameMode>?,
    val multiplayerMode: MultiplayerMode?,
    val isShortlist: Boolean,
    val gameStatus: GameStatus
)
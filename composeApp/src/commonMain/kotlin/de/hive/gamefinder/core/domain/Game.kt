package de.hive.gamefinder.core.domain

data class Game(
    val id: Int = 0,
    val name: String,
    val platform: Platform,
    val additionalGameInformation: AdditionalGameInformation
)

enum class Platform {
    STEAM,
    XBOX,
    XBOX_GAMEPASS,
    EPIC_GAMES,
    UBISOFT_CONNECT
}
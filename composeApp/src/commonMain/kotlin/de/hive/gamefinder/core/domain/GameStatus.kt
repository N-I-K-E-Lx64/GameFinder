package de.hive.gamefinder.core.domain

enum class GameStatus(val statusValue: String) {
    LIBRARY("Library"),
    INSTALLED("Installed"),
    PLAYING("Playing"),
    PAUSED("Paused"),
    PILE_OF_SHAME("Pile of shame"),
    WAITING("Waiting")
}
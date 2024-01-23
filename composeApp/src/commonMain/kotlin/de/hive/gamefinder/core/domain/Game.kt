package de.hive.gamefinder.core.domain

data class Game(
    val id: Int = 0,
    val name: String,
    val platform: Platform,
    val igdbGameId: Int,
    val coverImageId: String
)
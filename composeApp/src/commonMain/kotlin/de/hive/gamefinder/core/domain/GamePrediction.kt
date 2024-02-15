package de.hive.gamefinder.core.domain

import kotlinx.datetime.Instant

data class GamePrediction(
    val igdbGameId: Int,
    val name: String,
    val releaseDate: Instant
)

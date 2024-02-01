package de.hive.gamefinder.core.domain

data class GameQuery (
    val platform: Platform?,
    val onlineCoop: Boolean?,
    val campaignCoop: Boolean?
)
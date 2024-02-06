package de.hive.gamefinder.core.domain

data class GameQuery (
    val launcher: Launcher?,
    val onlineCoop: Boolean?,
    val campaignCoop: Boolean?
)
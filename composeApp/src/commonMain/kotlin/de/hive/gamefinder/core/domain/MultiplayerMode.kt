package de.hive.gamefinder.core.domain

data class MultiplayerMode (
    val hasCampaignCoop: Boolean,
    val hasOnlineCoop: Boolean,
    val onlineCoopMaxPlayers: Int
)
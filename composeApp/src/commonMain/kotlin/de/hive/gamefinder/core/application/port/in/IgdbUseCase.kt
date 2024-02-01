package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.Game

interface IgdbUseCase {

    suspend fun getGameDetails(gameName: String): Game
}
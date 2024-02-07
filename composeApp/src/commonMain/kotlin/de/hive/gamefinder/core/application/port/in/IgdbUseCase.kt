package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GamePrediction

interface IgdbUseCase {

    suspend fun getGameDetails(gameId: Int): Game

    suspend fun searchGamesByName(gameName: String): List<GamePrediction>
}
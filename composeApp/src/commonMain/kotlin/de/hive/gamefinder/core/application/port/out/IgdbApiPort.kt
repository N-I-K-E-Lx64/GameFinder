package de.hive.gamefinder.core.application.port.out

import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GamePrediction

interface IgdbApiPort {

    suspend fun getGameDetails(gameId: Int): Game

    suspend fun searchForGamesByName(gameName: String): List<GamePrediction>
}
package de.hive.gamefinder.core.application

import de.hive.gamefinder.core.application.port.`in`.IgdbUseCase
import de.hive.gamefinder.core.application.port.out.IgdbApiPort
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GamePrediction

class IgdbService(private val igdbApiPort: IgdbApiPort) : IgdbUseCase {

    override suspend fun getGameDetails(gameId: Int): Game {
        return igdbApiPort.getGameDetails(gameId)
    }

    override suspend fun searchGamesByName(gameName: String): List<GamePrediction> {
        return igdbApiPort.searchForGamesByName(gameName)
    }
}
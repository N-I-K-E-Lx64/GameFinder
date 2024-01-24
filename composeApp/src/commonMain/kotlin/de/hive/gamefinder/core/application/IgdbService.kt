package de.hive.gamefinder.core.application

import de.hive.gamefinder.core.application.port.`in`.IgdbUseCase
import de.hive.gamefinder.core.application.port.out.IgdbApiPort
import de.hive.gamefinder.core.domain.IgdbInformation

class IgdbService(private val igdbApiPort: IgdbApiPort) : IgdbUseCase {

    override suspend fun getGameDetails(gameName: String): IgdbInformation {
        return igdbApiPort.getGameDetails(gameName)
    }
}
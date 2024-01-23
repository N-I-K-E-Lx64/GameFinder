package de.hive.gamefinder.core.application.port.out

import de.hive.gamefinder.core.domain.IgdbInformation

interface IgdbApiPort {

    suspend fun getGameDetails(gameName: String): IgdbInformation
}
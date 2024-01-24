package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.IgdbInformation

interface IgdbUseCase {

    suspend fun getGameDetails(gameName: String): IgdbInformation
}
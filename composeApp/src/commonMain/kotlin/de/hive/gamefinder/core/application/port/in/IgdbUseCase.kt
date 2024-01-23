package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.AdditionalGameInformation

interface IgdbUseCase {

    suspend fun getGameDetails(gameName: String): AdditionalGameInformation
}
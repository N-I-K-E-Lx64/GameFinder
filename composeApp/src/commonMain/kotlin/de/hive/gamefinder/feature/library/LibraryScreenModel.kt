package de.hive.gamefinder.feature.library

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.`in`.IgdbUseCase
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Platform
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

class LibraryScreenModel(private val gameUseCase: GameUseCase, private val igdbUseCase: IgdbUseCase) : StateScreenModel<LibraryScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data class Result(val games: List<Game>) : State()
    }

    val platforms = Platform.entries.toTypedArray()

    fun loadGames() {
        screenModelScope.launch {
            mutableState.value = State.Loading

            gameUseCase.getGames().collect {
                value -> mutableState.value = State.Result(games = value)
            }
        }
    }

    fun addGame(gameName: String, selectedPlatform: Platform) {
        screenModelScope.launch {
            // Get additional information from IGDB
            val additionalGameInformation = igdbUseCase.getGameDetails(gameName)

            val game = Game(
                name = gameName,
                platform = selectedPlatform,
                additionalGameInformation = additionalGameInformation
            )

            Napier.i { game.toString() }

            gameUseCase.createGame(game)
        }
    }
}
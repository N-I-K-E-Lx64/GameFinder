package de.hive.gamefinder.feature.library

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Platform
import kotlinx.coroutines.launch

class LibraryStateScreenModel(private val gameUseCase: GameUseCase) : StateScreenModel<LibraryStateScreenModel.State>(State.Init) {

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
}
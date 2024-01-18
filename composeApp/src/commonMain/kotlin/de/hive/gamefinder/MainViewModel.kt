package de.hive.gamefinder

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.domain.Game
import kotlinx.coroutines.launch

class MainViewModel(private val useCase: GameUseCase) : StateScreenModel<MainViewModel.State>(State.Init) {

    sealed class State {
        object Init : State()
        object Loading : State()
        data class Result(val games: List<Game>) : State()
    }

    fun loadGames() {
        screenModelScope.launch {
            mutableState.value = State.Loading

            useCase.getGames().collect {
                value -> mutableState.value = State.Result(games = value)
            }
        }
    }
}
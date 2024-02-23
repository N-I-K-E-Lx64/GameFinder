package de.hive.gamefinder.feature.shortlist

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.domain.Game
import kotlinx.coroutines.launch

class ShortlistScreenModel(
    private val gameUseCase: GameUseCase
) : StateScreenModel<ShortlistScreenModel.State>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Result(val gamesOnShortlist: List<Game>) : State()
    }

    fun loadState() {
        screenModelScope.launch {
            mutableState.value = State.Loading

            gameUseCase.getGamesOnShortlist().collect {
                mutableState.value = State.Result(gamesOnShortlist = it)
            }
        }
    }
}
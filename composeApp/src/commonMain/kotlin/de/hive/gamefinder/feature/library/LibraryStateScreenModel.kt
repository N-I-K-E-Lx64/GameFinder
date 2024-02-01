package de.hive.gamefinder.feature.library

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
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
                mutableState.value = State.Result(games = it)
            }
        }
    }

    fun filterGamesByQuery(platformOrdinal: Int, online: Boolean, campaign: Boolean) {
        screenModelScope.launch {
            mutableState.value = State.Loading

            val platformFilter = if (platformOrdinal != -1) Platform.entries[platformOrdinal] else null
            val onlineMultiplayerFilter = if (online) true else null
            val campaignMultiplayerFilter = if (campaign) true else null

            val query = GameQuery(platformFilter, onlineMultiplayerFilter, campaignMultiplayerFilter)
            gameUseCase.getGamesByQuery(query).collect {
                mutableState.value = State.Result(games = it)
            }
        }
    }

    fun deleteGame(gameId: Int) {
        screenModelScope.launch {
            gameUseCase.deleteGame(gameId)
        }
    }
}
package de.hive.gamefinder.feature.library

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.adapter.exception.EmptySearchResultException
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.`in`.IgdbUseCase
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameQuery
import de.hive.gamefinder.core.domain.Platform
import de.hive.gamefinder.core.utils.UiEvents
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LibraryScreenModel(
    private val gameUseCase: GameUseCase,
    private val igdbUseCase: IgdbUseCase
) : StateScreenModel<LibraryScreenModel.State>(State.Init) {

    sealed class State {
        data object Init : State()
        data object Loading : State()
        data class Result(val games: List<Game>) : State()
    }

    val platforms = Platform.entries.toTypedArray()

    private val _eventsFlow = Channel<UiEvents>(Channel.UNLIMITED)
    val eventsFlow = _eventsFlow.receiveAsFlow()

    private val _searchResult = MutableStateFlow<List<Game>>(emptyList())
    val searchResult = _searchResult.asStateFlow()

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

    fun addGame(gameName: String, selectedPlatform: Platform) {
        screenModelScope.launch {
            try {
                // Get additional information from IGDB
                val igdbInformation = igdbUseCase.getGameDetails(gameName)

                val game = igdbInformation.copy(platform = selectedPlatform)
                gameUseCase.createGame(game)

                _eventsFlow.trySend(UiEvents.ShowSnackbar("$gameName has been successfully imported into the library."))

                Napier.i { "$gameName has been successfully imported into the library." }
            } catch (ex: EmptySearchResultException) {
                ex.message?.let {
                    _eventsFlow.trySend(UiEvents.ShowSnackbar(it))
                    Napier.e { it }
                }
            }
        }
    }

    fun searchGames(searchQuery: String) {
        screenModelScope.launch {
            gameUseCase.searchGamesByName(searchQuery).collect {
                _searchResult.value = it
            }
        }
    }

    fun resetSearchResults() {
        _searchResult.value = emptyList()
    }
}
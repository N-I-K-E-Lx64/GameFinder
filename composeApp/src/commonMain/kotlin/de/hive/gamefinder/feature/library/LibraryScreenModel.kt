package de.hive.gamefinder.feature.library

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.adapter.exception.EmptySearchResultException
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.`in`.IgdbUseCase
import de.hive.gamefinder.core.domain.Game
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
) : ScreenModel {

    private val _eventsFlow = Channel<UiEvents>(Channel.UNLIMITED)
    val eventsFlow = _eventsFlow.receiveAsFlow()

    private val _searchResult = MutableStateFlow<List<Game>>(emptyList())
    val searchResult = _searchResult.asStateFlow()

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
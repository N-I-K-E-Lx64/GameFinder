package de.hive.gamefinder.feature.game_finder

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.FriendUseCase
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.`in`.TagUseCase
import de.hive.gamefinder.core.domain.Friend
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Tag
import de.hive.gamefinder.core.utils.UiEvents
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameFinderScreenModel(
    private val gameUseCase: GameUseCase,
    private val friendUseCase: FriendUseCase,
    private val tagUseCase: TagUseCase
) : StateScreenModel<GameFinderScreenModel.State>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Result(val friends: List<Friend>, val tags: List<Tag>, val games: List<Game>) : State()
    }

    private val _eventsFlow = Channel<UiEvents>(Channel.UNLIMITED)
    val eventsFlow = _eventsFlow.receiveAsFlow()

    fun loadSelectionOptions() {
        screenModelScope.launch {
            mutableState.value = State.Result(emptyList(), emptyList(), emptyList())

            val friendsFlow = friendUseCase.getFriends()
            val tagFlow = tagUseCase.getTags()

            friendsFlow.combine(tagFlow) { friends, tags ->
                State.Result(friends, tags, emptyList())
            }.collect { combinedState ->
                mutableState.value = combinedState
            }
        }
    }

    fun findGames(selectedFriends: List<Int>, deselectedTags: List<Int>) {
        println(deselectedTags)
        screenModelScope.launch {
            if (selectedFriends.isEmpty()) {
                _eventsFlow.trySend(UiEvents.ShowSnackbar("You must select at least one of your friends!"))
            } else {
                gameUseCase.findGames(selectedFriends, deselectedTags).collect {
                    mutableState.update { currentState ->
                        if (currentState is State.Result) currentState.copy(games = it) else currentState
                    }

                    Napier.i { "Based on your selected friends (${selectedFriends}) the following games were found $it" }
                }
            }
        }
    }
}
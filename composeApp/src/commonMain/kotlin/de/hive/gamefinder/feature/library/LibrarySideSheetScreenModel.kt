package de.hive.gamefinder.feature.library

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.adapter.objects.GameFriendRelation
import de.hive.gamefinder.core.application.port.`in`.FriendUseCase
import de.hive.gamefinder.core.domain.Game
import kotlinx.coroutines.launch

class LibrarySideSheetScreenModel(private val friendUseCase: FriendUseCase) : StateScreenModel<LibrarySideSheetScreenModel.State>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Result(val game: Game, val friendsOwningGame: List<GameFriendRelation>) : State()
    }

    fun loadFriends(game: Game) {
        screenModelScope.launch {
            friendUseCase.getFriendByGame(game.id).collect {
                mutableState.value = State.Result(game = game, friendsOwningGame = it)
            }
        }
    }

    fun updateFriendRelations(gameId: Int, relation: GameFriendRelation, change: Boolean) {
        screenModelScope.launch {
            friendUseCase.changeGameFriendRelation(gameId, relation.friendId, change)
        }
    }
}
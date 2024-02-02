package de.hive.gamefinder.feature.library.details

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.adapter.objects.GameFriendRelation
import de.hive.gamefinder.core.application.port.`in`.FriendUseCase
import de.hive.gamefinder.core.application.port.`in`.TagUseCase
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailsScreenModel(
    private val friendUseCase: FriendUseCase,
    private val tagUseCase: TagUseCase
) : StateScreenModel<GameDetailsScreenModel.State>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Result(val game: Game, val friendsOwningGame: List<GameFriendRelation>) : State()
    }

    private val _searchResults = MutableStateFlow<List<Tag>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

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

    fun searchTags(searchQuery: String) {
        screenModelScope.launch {
            tagUseCase.getGamesByQuery(searchQuery).collect {
                _searchResults.value = it
            }
        }
    }

    fun addTagToGame(gameId: Int, tagId: Int) {
        screenModelScope.launch {
            tagUseCase.addTagToGame(gameId, tagId)
        }
    }

    fun removeTagFromGame(gameId: Int, tagId: Int) {
        screenModelScope.launch {
            tagUseCase.removeTagFromGame(gameId, tagId)
        }
    }

    fun createTag(gameId: Int, tagValue: String) {
        screenModelScope.launch {
            tagUseCase.createTag(gameId, tagValue)
        }
    }
}
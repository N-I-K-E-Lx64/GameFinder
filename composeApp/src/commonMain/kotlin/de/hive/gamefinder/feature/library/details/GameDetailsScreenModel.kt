package de.hive.gamefinder.feature.library.details

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.FriendUseCase
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.`in`.TagUseCase
import de.hive.gamefinder.core.domain.FriendGameRelation
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.MultiplayerMode
import de.hive.gamefinder.core.domain.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailsScreenModel(
    private val friendUseCase: FriendUseCase,
    private val tagUseCase: TagUseCase,
    private val gameUseCase: GameUseCase
) : StateScreenModel<GameDetailsScreenModel.State>(State.Loading) {

    sealed class State {
        data object Loading : State()
        data class Result(val friendsOwningGame: Map<Int, List<FriendGameRelation>>) : State()
    }

    private val _game = MutableStateFlow<Game?>(null)
    val game = _game.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Tag>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _updateButtonVisibility = MutableStateFlow(false)
    val updateButtonVisibility = _updateButtonVisibility.asStateFlow()

    private val _onlineCoopState = MutableStateFlow(false)
    val onlineCoopState = _onlineCoopState.asStateFlow()
    fun updateOnlineCoopState(update: Boolean) {
        _onlineCoopState.value = update
        _updateButtonVisibility.value = true
    }

    private val _campaignCoopState = MutableStateFlow(false)
    val campaignCoopState = _campaignCoopState.asStateFlow()
    fun updateCampaignCoopState(update: Boolean) {
        _campaignCoopState.value = update
        _updateButtonVisibility.value = true
    }

    private val _maxOnlineCoopPlayers = MutableStateFlow(0)
    val maxOnlineCoopPlayers = _maxOnlineCoopPlayers.asStateFlow()
    fun updateMaxOnlineCoopPlayers(update: Int) {
        _maxOnlineCoopPlayers.value = update
        _updateButtonVisibility.value = true
    }

    private fun initializeParameterStates(game: Game) {
        // If IGDB has no data about multiplayer modes they are initialized as false / 0
        _onlineCoopState.value = game.multiplayerMode?.hasOnlineCoop ?: false
        _campaignCoopState.value = game.multiplayerMode?.hasCampaignCoop ?: false
        _maxOnlineCoopPlayers.value = game.multiplayerMode?.onlineCoopMaxPlayers ?: 0

        _updateButtonVisibility.value = false
    }

    fun loadState(gameId: Int) {
        screenModelScope.launch {
            mutableState.value = State.Loading

            launch {
                gameUseCase.getGame(gameId).collect {
                    _game.value = it
                    it?.let { initializeParameterStates(it) }
                }
            }

            launch {
                friendUseCase.getFriendByGame(gameId).collect {
                    when (mutableState.value) {
                        is State.Loading -> {
                            mutableState.value = State.Result(mapOf(gameId to it))
                        }
                        is State.Result -> {
                            val state = mutableState.value as State.Result
                            val relationMap = state.friendsOwningGame.toMutableMap()
                            relationMap[gameId] = it
                            mutableState.value = State.Result(friendsOwningGame = relationMap)
                        }
                    }
                }
            }
        }
    }

    fun updateFriendRelations(gameId: Int, friendId: Int, change: Boolean) {
        screenModelScope.launch {
            friendUseCase.changeGameFriendRelation(gameId, friendId, change)
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

    fun updateMultiplayerParameters(gameId: Int) {
        screenModelScope.launch {
            val multiplayerUpdate = MultiplayerMode(
                hasOnlineCoop = onlineCoopState.value,
                hasCampaignCoop = campaignCoopState.value,
                onlineCoopMaxPlayers = maxOnlineCoopPlayers.value
            )

            gameUseCase.updateMultiplayerMode(gameId, multiplayerUpdate)
        }
    }

    fun deleteGame(gameId: Int) {
        screenModelScope.launch {
            gameUseCase.deleteGame(gameId)
        }
    }
}
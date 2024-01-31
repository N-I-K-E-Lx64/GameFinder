package de.hive.gamefinder.feature.library.details

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import de.hive.gamefinder.core.application.port.`in`.TagUseCase
import de.hive.gamefinder.core.domain.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailsScreenModel(private val tagUseCase: TagUseCase) : ScreenModel {

    private val _searchResults = MutableStateFlow<List<Tag>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

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
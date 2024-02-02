package de.hive.gamefinder.core.application.port.`in`

import de.hive.gamefinder.core.domain.Tag
import kotlinx.coroutines.flow.Flow

interface TagUseCase {

    suspend fun createTag(gameId: Int, tagValue: String)

    fun getTags(): Flow<List<Tag>>

    fun getGamesByQuery(query: String): Flow<List<Tag>>

    suspend fun deleteTag(id: Int)

    suspend fun addTagToGame(gameId: Int, tagId: Int)

    suspend fun removeTagFromGame(gameId: Int, tagId: Int)
}
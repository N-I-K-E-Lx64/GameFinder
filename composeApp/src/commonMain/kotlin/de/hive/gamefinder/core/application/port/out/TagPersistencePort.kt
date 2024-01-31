package de.hive.gamefinder.core.application.port.out

import de.hive.gamefinder.core.domain.Tag
import kotlinx.coroutines.flow.Flow

interface TagPersistencePort {

    suspend fun createTag(tag: Tag): Int

    fun getTagsByValue(value: String): Flow<List<Tag>>

    suspend fun deleteTag(id: Int)

    suspend fun createGameTagRelation(gameId: Int, tagId: Int)

    suspend fun removeGameTagRelation(gameId: Int, tagId: Int)
}
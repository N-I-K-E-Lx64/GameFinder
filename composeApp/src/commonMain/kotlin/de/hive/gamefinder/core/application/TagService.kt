package de.hive.gamefinder.core.application

import de.hive.gamefinder.core.application.port.`in`.TagUseCase
import de.hive.gamefinder.core.application.port.out.TagPersistencePort
import de.hive.gamefinder.core.domain.Tag
import kotlinx.coroutines.flow.Flow

class TagService(private val persistencePort: TagPersistencePort) : TagUseCase {
    override suspend fun createTag(gameId: Int, tagValue: String) {
        val tagId = persistencePort.createTag(Tag(tag = tagValue))
        persistencePort.createGameTagRelation(gameId, tagId)
    }

    override fun getGamesByQuery(query: String): Flow<List<Tag>> {
        return persistencePort.getTagsByValue(query)
    }

    override suspend fun deleteTag(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun addTagToGame(gameId: Int, tagId: Int) {
        persistencePort.createGameTagRelation(gameId, tagId)
    }

    override suspend fun removeTagFromGame(gameId: Int, tagId: Int) {
        persistencePort.removeGameTagRelation(gameId, tagId)
    }
}
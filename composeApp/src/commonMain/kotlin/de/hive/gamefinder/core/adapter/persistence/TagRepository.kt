package de.hive.gamefinder.core.adapter.persistence

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import de.hive.gamefinder.core.application.port.out.TagPersistencePort
import de.hive.gamefinder.core.domain.Tag
import de.hive.gamefinder.database.GameFinderDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepository(database: GameFinderDatabase) : TagPersistencePort {
    private val dbQueries = database.gameFinderQueries

    override suspend fun createTag(tag: Tag): Int {
        val tagId = dbQueries.transactionWithResult {
            dbQueries.createTag(tag.toEntity())
            dbQueries.getLastInsertedRowId().executeAsOne()
        }.toInt()

        Logger.i { "Created tag $tagId with value ${tag.tag}" }
        return tagId
    }

    override fun getTags(): Flow<List<Tag>> {
        return dbQueries
            .getAllTags()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { tags -> tags.map { it.toModel() } }
    }

    override fun getTagsByValue(value: String): Flow<List<Tag>> {
        return dbQueries
            .searchTags(value)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { tags -> tags.map { it.toModel() } }
    }

    override suspend fun deleteTag(id: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun createGameTagRelation(gameId: Int, tagId: Int) {
        dbQueries.createGameTagRelation(gameId = gameId.toLong(), tagId = tagId.toLong())
        Logger.i { "Add tag $tagId to game $gameId" }
    }

    override suspend fun removeGameTagRelation(gameId: Int, tagId: Int) {
        dbQueries.removeSingleGameTagRelation(gameId = gameId.toLong(), tagId = tagId.toLong())
        Logger.i { "Remove tag $tagId from game $gameId" }
    }
}
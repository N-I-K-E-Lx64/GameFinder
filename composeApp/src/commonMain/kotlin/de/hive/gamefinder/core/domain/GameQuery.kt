package de.hive.gamefinder.core.domain

data class GameQuery (
    val queryType: QueryType,
    val value: Any
)

enum class QueryType {
    NAME,
    PLATFORM
}
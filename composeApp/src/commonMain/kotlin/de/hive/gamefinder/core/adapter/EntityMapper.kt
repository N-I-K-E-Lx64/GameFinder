package de.hive.gamefinder.core.adapter

import database.Game_entity
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Platform

fun Game_entity.toModel() = Game(
    id = id,
    name = name,
    platform = Platform.entries[platform]
)

fun Game.toEntity() = Game_entity(
    id = id,
    name = name,
    platform = platform.ordinal
)
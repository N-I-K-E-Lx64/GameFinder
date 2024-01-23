package de.hive.gamefinder.core.adapter

import database.Game_entity
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Platform

fun Game_entity.toModel() = Game(
    id = id,
    name = name,
    platform = Platform.entries[platform],
    igdbGameId = game_id,
    coverImageId = cover_image_id,
)

fun Game.toEntity() = Game_entity(
    id = id,
    name = name,
    platform = platform.ordinal,
    game_id = igdbGameId,
    cover_image_id = coverImageId
)
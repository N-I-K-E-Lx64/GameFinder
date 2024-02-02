package de.hive.gamefinder.core.adapter.persistence

import database.Friend_entity
import database.Game_entity
import database.Tag_entity
import de.hive.gamefinder.core.domain.Friend
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.MultiplayerMode
import de.hive.gamefinder.core.domain.Tag

fun Game_entity.toModel() = Game(
    id = id,
    name = name,
    platform = platform,
    igdbGameId = game_id,
    coverImageId = cover_image_id,
    gameModes = game_modes,
    multiplayerMode = if (online_coop != null && campaign_coop != null && online_max_players != null) MultiplayerMode(campaign_coop, online_coop, online_max_players) else null
)

fun Game.toEntity() = Game_entity(
    id = id,
    name = name,
    platform = platform,
    game_id = igdbGameId,
    cover_image_id = coverImageId,
    game_modes = gameModes,
    online_coop = multiplayerMode?.hasOnlineCoop,
    campaign_coop = multiplayerMode?.hasCampaignCoop,
    online_max_players = multiplayerMode?.onlineCoopMaxPlayers
)

fun Friend_entity.toModel() = Friend(
    id = id,
    name = name
)

fun Friend.toEntity() = Friend_entity(
    id = id,
    name = name
)

fun Tag_entity.toModel() = Tag(
    id = id,
    tag = tag
)

fun Tag.toEntity() = Tag_entity(
    id = id,
    tag = tag
)
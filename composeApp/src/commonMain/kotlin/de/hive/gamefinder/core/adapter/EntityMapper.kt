package de.hive.gamefinder.core.adapter

import database.Game_entity
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.MultiplayerMode

fun Game_entity.toModel() = Game(
    id = id,
    name = name,
    platform = platform,
    igdbGameId = game_id,
    coverImageId = cover_image_id,
    gameModes = game_modes,
    multiplayerMode = if (campaign_coop != null && online_coop != null && online_max_players != null) MultiplayerMode(campaign_coop, online_coop, online_max_players) else null
)

fun Game.toEntity() = Game_entity(
    id = id,
    name = name,
    platform = platform,
    game_id = igdbGameId,
    cover_image_id = coverImageId,
    game_modes = gameModes,
    campaign_coop = multiplayerMode?.hasCampaignCoop,
    online_coop = multiplayerMode?.hasOnlineCoop,
    online_max_players = multiplayerMode?.onlineCoopMaxPlayers
)
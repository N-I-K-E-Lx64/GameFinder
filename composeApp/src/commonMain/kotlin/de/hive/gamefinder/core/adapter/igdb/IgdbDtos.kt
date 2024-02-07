package de.hive.gamefinder.core.adapter.igdb

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
data class AuthenticationToken(
    @JsonNames("access_token") val accessToken: String,
    @JsonNames("expires_in") val exiresIn: Long,
    @JsonNames("token_type") val tokenType: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class IgdbMultiplayerMode(
    @JsonNames("id") val id: Int,
    @JsonNames("campaigncoop") val campaignCoop: Boolean,
    @JsonNames("onlinecoop") val onlineCoop: Boolean,
    @JsonNames("onlinecoopmax") val onlineCoopMax: Int
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class IgdbCoverInformationDto(
    @JsonNames("id") val coverId: Int,
    @JsonNames("image_id") val imageId: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class IgdbGameInformationDto(
    @JsonNames("id") val gameId: Int,
    @JsonNames("name") val name: String,
    @JsonNames("cover") val cover: IgdbCoverInformationDto,
    @JsonNames("game_modes") val gameModes: List<Int>? = null,
    @JsonNames("multiplayer_modes") val multiplayerModes: List<Int>? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class IgdbGameImportPredictionDto(
    @JsonNames("id") val gameId: Int,
    @JsonNames("name") val name: String,
    @JsonNames("first_release_date") val releaseDateTimeStamp: Long? = null
)
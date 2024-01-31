package de.hive.gamefinder.core.adapter.igdb

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import de.hive.gamefinder.BuildKonfig
import de.hive.gamefinder.core.adapter.exception.EmptySearchResultException
import de.hive.gamefinder.core.application.port.out.IgdbApiPort
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GameMode
import de.hive.gamefinder.core.domain.MultiplayerMode
import de.hive.gamefinder.core.utils.levenshteinSimilarity
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

class IgdbApiAdapter(private val settings: Settings) : IgdbApiPort {

    companion object {
        const val IGDB_AUTHENTICATION_URL = "https://id.twitch.tv/oauth2/token"
        const val IGDB_GAME_ENDPOINT = "https://api.igdb.com/v4/games"
        const val IGDB_MULTIPLAYER_MODE_ENDPOINT = "https://api.igdb.com/v4/multiplayer_modes"
        const val IGDB_MICROSOFT_WINDOWS_PLATFORM = 6
    }

    private val client = HttpClient {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v("HTTP Client", null, message)
                }
            }
            level = LogLevel.HEADERS
        }.also { Napier.base(DebugAntilog()) }

        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(settings["access_token", "n8oqxzh9gavp952ss0uc4f6tqko7d5"], "")
                }
                refreshTokens {
                    val token: AuthenticationToken = client.post(IGDB_AUTHENTICATION_URL) {
                        url {
                            parameters.append("client_id", BuildKonfig.CLIENT_ID)
                            parameters.append("client_secret", BuildKonfig.CLIENT_SECRET)
                            parameters.append("grant_type", "client_credentials")
                        }
                        markAsRefreshTokenRequest()
                    }.body()

                    settings.putString("access_token", token.accessToken)
                    return@refreshTokens BearerTokens(token.accessToken, "")
                }
            }
        }
    }

    private suspend fun getMultiplayerInfos(igdbGameId: Int): MultiplayerMode? {
        val igdbMultiplayerMode = client.post(IGDB_MULTIPLAYER_MODE_ENDPOINT) {
            setBody("fields campaigncoop, onlinecoop, onlinecoopmax; where game = $igdbGameId & platform = $IGDB_MICROSOFT_WINDOWS_PLATFORM;")
            headers {
                append("Client-Id", BuildKonfig.CLIENT_ID)
            }
        }.body<Array<IgdbMultiplayerMode>>()

        if (igdbMultiplayerMode.isEmpty()) {
            return null
        }

        return MultiplayerMode(igdbMultiplayerMode[0].campaignCoop, igdbMultiplayerMode[0].onlineCoop, igdbMultiplayerMode[0].onlineCoopMax)
    }

    override suspend fun getGameDetails(gameName: String): Game {
        val igdbResult = client.post(IGDB_GAME_ENDPOINT) {
            setBody("""
                fields name, cover.image_id, multiplayer_modes, game_modes;
                search "$gameName";
                where version_parent = null;
                where parent_game = null;
                limit 20;
            """.trimIndent()
            )
            headers {
                append("Client-Id", BuildKonfig.CLIENT_ID)
            }
        }.body<Array<IgdbGameInformationDto>>()

        if (igdbResult.isEmpty()) {
            throw EmptySearchResultException("$gameName could not be found!")
        }

        val desiredGame = igdbResult.maxBy { levenshteinSimilarity(it.name, gameName) }

        val gameModes = desiredGame.gameModes?.let { gameModes -> gameModes.map { GameMode.entries[it] } }
        val multiplayerMode = desiredGame.multiplayerModes?.let { getMultiplayerInfos(desiredGame.gameId) }

        return Game(
            name = desiredGame.name,
            igdbGameId = desiredGame.gameId,
            coverImageId = desiredGame.cover.imageId,
            gameModes = gameModes,
            tags = emptyList(),
            multiplayerMode = multiplayerMode
        )
    }
}


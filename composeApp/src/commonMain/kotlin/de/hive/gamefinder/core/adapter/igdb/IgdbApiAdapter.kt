package de.hive.gamefinder.core.adapter.igdb

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import de.hive.gamefinder.BuildKonfig
import de.hive.gamefinder.core.adapter.exception.EmptySearchResultException
import de.hive.gamefinder.core.application.port.out.IgdbApiPort
import de.hive.gamefinder.core.domain.IgdbInformation
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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

class IgdbApiAdapter(private val settings: Settings) : IgdbApiPort {

    companion object {
        const val IGDB_AUTHENTICATION_URL = "https://id.twitch.tv/oauth2/token"
        const val IGDB_GAME_ENDPOINT = "https://api.igdb.com/v4/games"
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

    override suspend fun getGameDetails(gameName: String): IgdbInformation {
        val igdbResult = client.post(IGDB_GAME_ENDPOINT) {
            setBody("fields name, cover.image_id; search \"${gameName}\";")
            headers {
                append("Client-Id", BuildKonfig.CLIENT_ID)
            }
        }.body<Array<IgdbGameInformationDto>>()

        if (igdbResult.isEmpty()) {
            throw EmptySearchResultException("$gameName could not be found!")
        }

        val desiredGame = igdbResult.maxBy { levenshteinSimilarity(it.name, gameName) }

        return IgdbInformation(desiredGame.name, desiredGame.gameId, desiredGame.cover.imageId)
    }
}

@OptIn(ExperimentalSerializationApi::class)
data class AuthenticationToken(
    @JsonNames("access_token") val accessToken: String,
    @JsonNames("expires_in") val exiresIn: Long,
    @JsonNames("token_type") val tokenType: String
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
    @JsonNames("cover") val cover: IgdbCoverInformationDto
)


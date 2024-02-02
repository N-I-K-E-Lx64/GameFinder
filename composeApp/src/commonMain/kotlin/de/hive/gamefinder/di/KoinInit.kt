package de.hive.gamefinder.di

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import com.russhwolf.settings.Settings
import database.Friend_entity
import database.Game_entity
import database.Tag_entity
import de.hive.gamefinder.core.adapter.igdb.IgdbApiAdapter
import de.hive.gamefinder.core.adapter.persistence.*
import de.hive.gamefinder.core.application.FriendService
import de.hive.gamefinder.core.application.GameService
import de.hive.gamefinder.core.application.IgdbService
import de.hive.gamefinder.core.application.TagService
import de.hive.gamefinder.core.application.port.`in`.FriendUseCase
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.`in`.IgdbUseCase
import de.hive.gamefinder.core.application.port.`in`.TagUseCase
import de.hive.gamefinder.core.application.port.out.FriendPersistencePort
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.application.port.out.IgdbApiPort
import de.hive.gamefinder.core.application.port.out.TagPersistencePort
import de.hive.gamefinder.database.GameFinderDatabase
import de.hive.gamefinder.feature.game_finder.GameFinderScreenModel
import de.hive.gamefinder.feature.library.LibraryScreenModel
import de.hive.gamefinder.feature.library.details.GameDetailsScreenModel
import de.hive.gamefinder.feature.navigation.NavigationScreenModel
import de.hive.gamefinder.platform.DatabaseDriverFactory
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

class KoinInit {
    fun init(appDeclaration: KoinAppDeclaration = {}): Koin {
        return startKoin {
            modules(
                listOf(
                    platformModule(),
                    coreModule
                )
            )
            appDeclaration()
        }.koin
    }
}

val coreModule = module {
    single<GameFinderDatabase> {
        GameFinderDatabase(
            driver = get<DatabaseDriverFactory>().createDriver(),
            game_entityAdapter = Game_entity.Adapter(
                idAdapter = IntColumnAdapter,
                platformAdapter = platformAdapter,
                game_idAdapter = IntColumnAdapter,
                game_modesAdapter = gameModeAdapter,
                online_max_playersAdapter = IntColumnAdapter
            ),
            friend_entityAdapter = Friend_entity.Adapter(
                idAdapter = IntColumnAdapter
            ),
            tag_entityAdapter = Tag_entity.Adapter(
                idAdapter = IntColumnAdapter
            )
        )
    }

    single<Settings> { Settings() }

    /**
     * Screen modules
     */
    single { LibraryScreenModel(get(), get()) }
    single { GameDetailsScreenModel(get(), get()) }
    single { NavigationScreenModel(get()) }
    single { GameFinderScreenModel(get(), get(), get()) }

    /**
     * Adapters
     */
    single<IgdbApiPort> { IgdbApiAdapter(get()) }
    single<GamePersistencePort> { GameRepository(get()) }
    single<FriendPersistencePort> { FriendRepository(get()) }
    single<TagPersistencePort> { TagRepository(get()) }

    /**
     * Ports
     */
    single<IgdbUseCase> { IgdbService(get()) }
    single<GameUseCase> { GameService(get()) }
    single<FriendUseCase> { FriendService(get()) }
    single<TagUseCase> { TagService(get()) }
}

expect fun platformModule(): Module


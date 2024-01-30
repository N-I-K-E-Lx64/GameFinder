package de.hive.gamefinder.di

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import com.russhwolf.settings.Settings
import database.Friend_entity
import database.Game_entity
import de.hive.gamefinder.core.adapter.FriendRepository
import de.hive.gamefinder.core.adapter.GameRepository
import de.hive.gamefinder.core.adapter.gameModeAdapter
import de.hive.gamefinder.core.adapter.igdb.IgdbApiAdapter
import de.hive.gamefinder.core.adapter.platformAdapter
import de.hive.gamefinder.core.application.FriendService
import de.hive.gamefinder.core.application.GameService
import de.hive.gamefinder.core.application.IgdbService
import de.hive.gamefinder.core.application.port.`in`.FriendUseCase
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.`in`.IgdbUseCase
import de.hive.gamefinder.core.application.port.out.FriendPersistencePort
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.application.port.out.IgdbApiPort
import de.hive.gamefinder.database.GameFinderDatabase
import de.hive.gamefinder.feature.library.LibraryScreenModel
import de.hive.gamefinder.feature.library.LibrarySideSheetScreenModel
import de.hive.gamefinder.feature.library.LibraryStateScreenModel
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
            )
        )
    }

    /*single { GameRepository(database = get()) }
    single { FriendRepository(database = get()) }*/
    single<Settings> { Settings() }

    /**
     * Screen modules
     */
    single { LibraryStateScreenModel(get()) }
    single { LibraryScreenModel(get(), get()) }
    single { LibrarySideSheetScreenModel(get()) }
    single { NavigationScreenModel(get()) }

    /**
     * Adapters
     */
    single<IgdbApiPort> { IgdbApiAdapter(get()) }
    single<GamePersistencePort> { GameRepository(get()) }
    single<FriendPersistencePort> { FriendRepository(get()) }

    /**
     * Ports
     */
    single<IgdbUseCase> { IgdbService(get()) }
    single<GameUseCase> { GameService(get()) }
    single<FriendUseCase> { FriendService(get()) }
}

expect fun platformModule(): Module


package de.hive.gamefinder.di

import com.russhwolf.settings.Settings
import database.Game_entity
import de.hive.gamefinder.MainViewModel
import de.hive.gamefinder.core.adapter.GameRepository
import de.hive.gamefinder.core.adapter.gameIdAdapter
import de.hive.gamefinder.core.adapter.idAdapter
import de.hive.gamefinder.core.adapter.igdb.IgdbApiAdapter
import de.hive.gamefinder.core.adapter.platformAdapter
import de.hive.gamefinder.core.application.GameService
import de.hive.gamefinder.core.application.IgdbService
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.`in`.IgdbUseCase
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.core.application.port.out.IgdbApiPort
import de.hive.gamefinder.database.GameFinderDatabase
import de.hive.gamefinder.feature.create_game.CreateGameViewModel
import de.hive.gamefinder.feature.library.LibraryScreenModel
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
                idAdapter = idAdapter,
                platformAdapter = platformAdapter,
                game_idAdapter = gameIdAdapter
            )
        )
    }

    single { GameRepository(database = get()) }

    single<GamePersistencePort> { GameRepository(get()) }

    single<GameUseCase> { GameService(get()) }

    single<Settings> { Settings() }

    /**
     * Screen modules
     */
    single { MainViewModel(get()) }
    single { LibraryScreenModel(get(), get()) }


    single { CreateGameViewModel(get(), get()) }

    /**
     * Adapters
     */
    single<IgdbApiPort> { IgdbApiAdapter(get()) }

    /**
     * Ports
     */
    single<IgdbUseCase> { IgdbService(get()) }
}

expect fun platformModule(): Module


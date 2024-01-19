package de.hive.gamefinder.di

import database.Game_entity
import de.hive.gamefinder.MainViewModel
import de.hive.gamefinder.core.adapter.GameRepository
import de.hive.gamefinder.core.adapter.idAdapter
import de.hive.gamefinder.core.adapter.platformAdapter
import de.hive.gamefinder.core.application.GameService
import de.hive.gamefinder.core.application.port.`in`.GameUseCase
import de.hive.gamefinder.core.application.port.out.GamePersistencePort
import de.hive.gamefinder.database.GameFinderDatabase
import de.hive.gamefinder.feature.create_game.CreateGameViewModel
import de.hive.gamefinder.platform.DatabaseDriverFactory
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

class KoinInit {
    fun init(appDeclaration: KoinAppDeclaration = {}): Koin {
        println("Koin Function")
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
                platformAdapter = platformAdapter
            )
        )
    }

    single { GameRepository(database = get()) }

    single<GamePersistencePort> { GameRepository(get()) }

    single<GameUseCase> { GameService(get()) }

    /**
     * Screen modules
     */
    single { MainViewModel(get()) }
    single { CreateGameViewModel(get()) }
}

expect fun platformModule(): Module


package de.hive.gamefinder.di

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
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
import de.hive.gamefinder.feature.shortlist.ShortlistScreenModel
import de.hive.gamefinder.platform.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

fun getPlatformModule(): Module {
    return platformModule()
}

fun createDatabase(driver: SqlDriver): GameFinderDatabase {
    //val driver = driverFactory.createDriver()
    val database = GameFinderDatabase(
        driver = driver,
        game_entityAdapter = Game_entity.Adapter(
            idAdapter = IntColumnAdapter,
            launcherAdapter = launcherAdapter,
            game_idAdapter = IntColumnAdapter,
            game_modesAdapter = gameModeAdapter,
            online_max_playersAdapter = IntColumnAdapter,
            game_statusAdapter = gameStatusAdapter,
            shortlist_positionAdapter = IntColumnAdapter
        ),
        friend_entityAdapter = Friend_entity.Adapter(
            idAdapter = IntColumnAdapter
        ),
        tag_entityAdapter = Tag_entity.Adapter(
            idAdapter = IntColumnAdapter
        )
    )

    return database
}

val coreModule = module {
    single<GameFinderDatabase> { createDatabase(get<DatabaseDriverFactory>().createDriver()) }

    single<Settings> { Settings() }
    
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

    /**
     * Screen modules
     */
    single { LibraryScreenModel(get(), get()) }
    single { GameDetailsScreenModel(get(), get(), get()) }
    single { NavigationScreenModel(get(), get()) }
    single { GameFinderScreenModel(get(), get(), get()) }
    single { ShortlistScreenModel(get()) }
}

expect fun platformModule(): Module


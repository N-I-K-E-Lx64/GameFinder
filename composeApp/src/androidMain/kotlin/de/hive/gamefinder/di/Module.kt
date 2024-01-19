package de.hive.gamefinder.di

import de.hive.gamefinder.platform.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory(context = get()) }
}
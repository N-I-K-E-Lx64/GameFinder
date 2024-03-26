package de.hive.gamefinder.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import de.hive.gamefinder.BuildKonfig
import de.hive.gamefinder.database.GameFinderDatabase
import org.jetbrains.jewel.window.utils.DesktopPlatform

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return if (!BuildKonfig.DEVELOPMENT) {
            if (DesktopPlatform.Current == DesktopPlatform.Windows) {
                JdbcSqliteDriver("jdbc:sqlite:${System.getenv("APPDATA")}/gamefinder.db")
                    .also { GameFinderDatabase.Schema.create(it) }
            } else {
                JdbcSqliteDriver("jdbc:sqlite:gamefinder.db")
                    .also { GameFinderDatabase.Schema.create(it) }
            }
        } else {
            val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            GameFinderDatabase.Schema.create(driver)
            return driver
        }
    }
}
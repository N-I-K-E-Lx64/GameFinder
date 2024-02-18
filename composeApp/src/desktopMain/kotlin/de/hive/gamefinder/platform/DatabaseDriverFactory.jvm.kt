package de.hive.gamefinder.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import de.hive.gamefinder.database.GameFinderDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            .also { GameFinderDatabase.Schema.create(it) }
        /*return JdbcSqliteDriver("jdbc:sqlite:gamefinder.db")
            .also { GameFinderDatabase.Schema.create(it) }*/
    }
}
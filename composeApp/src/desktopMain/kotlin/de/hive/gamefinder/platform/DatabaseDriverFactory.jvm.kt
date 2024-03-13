package de.hive.gamefinder.platform

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import de.hive.gamefinder.database.GameFinderDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver("jdbc:sqlite:gamefinder.db")
            .also { GameFinderDatabase.Schema.create(it) }
        /*val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        GameFinderDatabase.Schema.create(driver)
        return driver*/
    }
}
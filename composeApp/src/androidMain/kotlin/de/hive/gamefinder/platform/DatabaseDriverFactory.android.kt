package de.hive.gamefinder.platform

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import de.hive.gamefinder.database.GameFinderDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(GameFinderDatabase.Schema, context, "gamefinder.db")
    }
}
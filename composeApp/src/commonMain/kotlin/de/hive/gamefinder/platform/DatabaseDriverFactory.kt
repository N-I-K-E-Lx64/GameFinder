package de.hive.gamefinder.platform

import app.cash.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

/*
fun createDatabase(driverFactory: DatabaseDriverFactory): GameFinderDatabase {
    val driver = driverFactory.createDriver()
    val database = GameFinderDatabase(driver)
}*/

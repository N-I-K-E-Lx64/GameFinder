package de.hive.gamefinder.core.adapter.persistence

import app.cash.sqldelight.ColumnAdapter
import de.hive.gamefinder.core.domain.GameMode
import de.hive.gamefinder.core.domain.GameStatus
import de.hive.gamefinder.core.domain.Launcher

val launcherAdapter = object : ColumnAdapter<Launcher, Long> {
    override fun decode(databaseValue: Long): Launcher {
        return Launcher.entries[databaseValue.toInt()]
    }

    override fun encode(value: Launcher): Long {
        return value.ordinal.toLong()
    }
}

val gameModeAdapter = object : ColumnAdapter<List<GameMode>, String> {
    override fun decode(databaseValue: String): List<GameMode> {
        if (databaseValue.isEmpty()) {
            return emptyList<GameMode>()
        } else {
            val enumValues = databaseValue.split(",").map { it.toInt() }
            return enumValues.map { GameMode.entries[it] }
        }
    }

    override fun encode(value: List<GameMode>): String {
        return value.map { it.ordinal }.joinToString(separator = ",")
    }
}

val gameStatusAdapter = object : ColumnAdapter<GameStatus, Long> {
    override fun decode(databaseValue: Long): GameStatus {
        return GameStatus.entries[databaseValue.toInt()]
    }

    override fun encode(value: GameStatus): Long {
        return value.ordinal.toLong()
    }
}

fun Boolean.toLong() = if (this) 1L else 0L
fun Long.toBoolean() = this == 1L
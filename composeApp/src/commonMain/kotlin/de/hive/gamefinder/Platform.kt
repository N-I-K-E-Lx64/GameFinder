package de.hive.gamefinder

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
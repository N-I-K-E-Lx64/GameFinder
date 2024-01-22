package de.hive.gamefinder

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        Navigator(MainScreen())
    }
}
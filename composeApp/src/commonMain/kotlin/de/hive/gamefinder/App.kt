package de.hive.gamefinder

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import de.hive.gamefinder.feature.library.LibraryScreen
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        //Navigator(MainScreen())
        Navigator(LibraryScreen())
    }
}
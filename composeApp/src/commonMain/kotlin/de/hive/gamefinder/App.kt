package de.hive.gamefinder

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            Surface (color = MaterialTheme.colorScheme.background) {
                Navigator(screen = MainScreen())
            }
        }
    }
}
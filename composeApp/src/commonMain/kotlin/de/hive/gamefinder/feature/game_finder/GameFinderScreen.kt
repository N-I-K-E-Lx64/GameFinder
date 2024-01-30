package de.hive.gamefinder.feature.game_finder


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen

class GameFinderScreen : Screen {

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().consumeWindowInsets(innerPadding)
            ) {
                Text(
                    text = "GameFinder",
                    style = MaterialTheme.typography.displayMedium
                )

                FilledTonalButton(
                    onClick = { }
                ) {
                    Text("Find Game")
                }
            }
        }
    }
}
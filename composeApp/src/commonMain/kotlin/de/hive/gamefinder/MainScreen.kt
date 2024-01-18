package de.hive.gamefinder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.hive.gamefinder.feature.create_game.CreateGameScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

class MainScreen : Screen {

    @OptIn(ExperimentalResourceApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<MainViewModel>()
        val state by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.loadGames()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = { Text("GameFinder") }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { navigator.push(CreateGameScreen()) },
                    icon = { Icon(Icons.Filled.Edit, "Test") },
                    text = { Text(text = "Import Game") }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                when (state) {
                    is MainViewModel.State.Init -> {
                        Text("Init")
                    }

                    is MainViewModel.State.Loading -> {
                        Text("loading ...")
                    }

                    is MainViewModel.State.Result -> {
                        Text(text = (state as MainViewModel.State.Result).games.toString())

                        var showContent by remember { mutableStateOf(false) }
                        val greeting = remember { Greeting().greet() }
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { showContent = !showContent }) {
                                Text("Click me!")
                            }
                            AnimatedVisibility(showContent) {
                                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(painterResource("compose-multiplatform.xml"), null)
                                    Text("Compose: $greeting")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
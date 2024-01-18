package de.hive.gamefinder.feature.create_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.hive.gamefinder.core.domain.Game

class CreateGameScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<CreateGameViewModel>()

        val gameName = screenModel.gameName.value
        val platform = screenModel.platform.value

        fun onClickAddGame() {
            screenModel.addGame(game = Game(
                name = gameName,
                platform = platform
            ))
            navigator.pop()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = {
                        Text("Import a new Game")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop() }
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Go back")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { onClickAddGame() }
                        ) {
                            Text("Save")
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = screenModel.gameName.value,
                        onValueChange = { screenModel.setGameName(it) },
                        label = {
                            Text(
                                text = "Name"
                            )
                        },
                        placeholder = {
                            Text(
                                text = "Anno 1800"
                            )
                        }
                    )
                }

                item {
                    Text("Platform")
                    Column {
                        screenModel.platforms.forEach { platform ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = (platform == screenModel.platform.value),
                                        onClick = { screenModel.setPlatform(platform) },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (platform == screenModel.platform.value),
                                    onClick = null
                                )
                                Text(
                                    text = platform.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
    }
}
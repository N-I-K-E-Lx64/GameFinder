package de.hive.gamefinder.feature.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.hive.gamefinder.core.domain.Platform
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

class LibraryScreen : Screen {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LibraryScreenModel>()
        val state by screenModel.state.collectAsState()

        val windowSizeClass = calculateWindowSizeClass()

        println(windowSizeClass.widthSizeClass)

        val snackbarHostState = remember { SnackbarHostState() }
        val openDialog = remember { mutableStateOf(false) }
        val gameName = remember { mutableStateOf("") }
        val selectedPlatform = remember { mutableStateOf(Platform.STEAM) }

        LaunchedEffect(Unit) {
            screenModel.loadGames()
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { openDialog.value = true },
                    icon = { Icon(Icons.Filled.Add, "Import a new game") },
                    text = { Text(text = "Import Game") }
                )
            }
        ) { innerPadding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (state) {
                    is LibraryScreenModel.State.Init -> {
                        Text("Init")
                    }
                    is LibraryScreenModel.State.Loading -> {
                        Text("Loading")
                    }
                    is LibraryScreenModel.State.Result -> {
                        val games = (state as LibraryScreenModel.State.Result).games

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 250.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(games) {
                                ElevatedCard(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        KamelImage(
                                            resource = asyncPainterResource("https://images.igdb.com/igdb/image/upload/t_cover_big/${it.additionalGameInformation.coverImageId}.jpg"),
                                            contentDescription = "${it.name} - Cover",
                                            contentScale = ContentScale.Crop,
                                            onLoading = { progress -> CircularProgressIndicator(progress) },
                                            modifier = Modifier
                                                //.aspectRatio(1.0f)
                                                .clip(RoundedCornerShape(16.dp))
                                        )
                                        Text(
                                            text = it.name,
                                            modifier = Modifier.padding(16.dp),
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                }
                            }
                        }

                        when {
                            openDialog.value -> {
                                CreateGameDialog(
                                    onDismissRequest = { openDialog.value = false },
                                    onSave = {
                                        screenModel.addGame(gameName.value, selectedPlatform.value)
                                        gameName.value = ""
                                        selectedPlatform.value = Platform.STEAM
                                        // Close the dialog
                                        openDialog.value = false
                                    },
                                    onUpdateName = { gameName.value = it },
                                    onSelectPlatform = { selectedPlatform.value = it },
                                    gameName = gameName.value,
                                    selectedPlatform = selectedPlatform.value,
                                    platforms = screenModel.platforms
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateGameDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onUpdateName: (gameName: String) -> Unit,
    onSelectPlatform: (platform: Platform) -> Unit,
    gameName: String,
    selectedPlatform: Platform,
    platforms: Array<Platform>) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column (
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text (
                    "Import a game",
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = gameName,
                    onValueChange = { onUpdateName(it) },
                    label = {
                        Text(text = "Game name")
                    },
                    placeholder = {
                        Text(text = "Anno 1800")
                    }
                )

                Text("Platform", style = MaterialTheme.typography.titleMedium)

                Column(modifier = Modifier.selectableGroup()) {
                    platforms.forEach { platform ->
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (platform == selectedPlatform),
                                    onClick = { onSelectPlatform(platform) },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (platform == selectedPlatform),
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = { onDismissRequest() } ) {
                        Text("Cancel")
                    }

                    TextButton(onClick = { onSave() } ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
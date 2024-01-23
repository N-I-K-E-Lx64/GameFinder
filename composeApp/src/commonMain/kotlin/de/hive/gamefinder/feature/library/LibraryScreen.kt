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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.hive.gamefinder.core.domain.Platform
import de.hive.gamefinder.core.utils.UiEvents
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibraryScreen : Screen {

    companion object {
        const val IGDB_IMAGE_ENDPOINT = "https://images.igdb.com/igdb/image/upload/t_cover_big_2x/"
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val stateScreenModel = getScreenModel<LibraryStateScreenModel>()
        val screenModel = getScreenModel<LibraryScreenModel>()
        val state by stateScreenModel.state.collectAsState()

        val windowSizeClass = calculateWindowSizeClass()

        println(windowSizeClass.widthSizeClass)

        val snackbarHostState = remember { SnackbarHostState() }
        var openDialog by remember { mutableStateOf(false) }
        var searchText by remember { mutableStateOf("") }
        var active by remember { mutableStateOf(false) }

        var gameName by remember { mutableStateOf("") }
        var selectedPlatform by remember { mutableStateOf(Platform.STEAM) }

        LaunchedEffect(Unit) {
            stateScreenModel.loadGames()

            withContext(Dispatchers.Main.immediate) {
                screenModel.eventsFlow.collect { event ->
                    when (event) {
                        is UiEvents.ShowSnackbar -> {
                            snackbarHostState.showSnackbar(
                                message = event.message
                            )
                        }
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { openDialog = true },
                    icon = { Icon(Icons.Filled.Add, "Import a new game") },
                    text = { Text(text = "Import Game") }
                )
            }
        ) { innerPadding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
                    .padding(16.dp)
            ) {
                when (state) {
                    is LibraryStateScreenModel.State.Init -> {
                        Text("Init")
                    }
                    is LibraryStateScreenModel.State.Loading -> {
                        Text("Loading")
                    }
                    is LibraryStateScreenModel.State.Result -> {
                        val games = (state as LibraryStateScreenModel.State.Result).games

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Games",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            /*Box(
                                modifier = Modifier.semantics { isTraversalGroup = true }
                            ) {
                                SearchBar(
                                    query = searchText,
                                    onQueryChange = { searchText = it },
                                    onSearch = { active = false },
                                    active = active,
                                    onActiveChange = { active = it },
                                    placeholder = { Text("Search") },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                ) {
                                    repeat(4)
                                }
                            }*/
                        }

                        Text("${games.size} Imported Games", modifier = Modifier.padding(bottom = 8.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 250.dp),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(games) {
                                ElevatedCard(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(bottom = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        KamelImage(
                                            resource = asyncPainterResource("$IGDB_IMAGE_ENDPOINT${it.coverImageId}.jpg"),
                                            contentDescription = "${it.name} - Cover",
                                            contentScale = ContentScale.Crop,
                                            onLoading = { progress -> CircularProgressIndicator(progress) },
                                            modifier = Modifier
                                                //.aspectRatio(1.0f)
                                                .clip(RoundedCornerShape(16.dp))
                                        )
                                        Text(
                                            text = it.name,
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = it.platform.platform,
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                        )
                                    }
                                }
                            }
                        }

                        when {
                            openDialog -> {
                                CreateGameDialog(
                                    onDismissRequest = { openDialog = false },
                                    onSave = {
                                        screenModel.addGame(gameName, selectedPlatform)
                                        // Reset form values
                                        gameName = ""
                                        selectedPlatform = Platform.STEAM
                                        // Close the dialog
                                        openDialog = false
                                    },
                                    onUpdateName = { gameName = it },
                                    onSelectPlatform = { selectedPlatform = it },
                                    gameName = gameName,
                                    selectedPlatform = selectedPlatform,
                                    platforms = stateScreenModel.platforms
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
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
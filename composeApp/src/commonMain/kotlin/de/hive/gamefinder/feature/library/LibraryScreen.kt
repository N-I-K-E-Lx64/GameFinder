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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Platform
import de.hive.gamefinder.core.utils.UiEvents
import de.hive.gamefinder.utils.HorizontalTwoPaneStrategy
import de.hive.gamefinder.utils.TwoPane
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibraryScreen(val filter: Platform?) : Screen {

    companion object {
        const val IGDB_IMAGE_ENDPOINT = "https://images.igdb.com/igdb/image/upload/t_cover_big_2x/"
    }

    @OptIn(
        ExperimentalLayoutApi::class,
        ExperimentalMaterial3Api::class
    )
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LibraryScreenModel>()
        val searchResultState by screenModel.searchResult.collectAsState()

        val stateScreenModel = getScreenModel<LibraryStateScreenModel>()
        val state by stateScreenModel.state.collectAsState()

        val sideSheetScreenModel = getScreenModel<LibrarySideSheetScreenModel>()
        val sideSheetState by sideSheetScreenModel.state.collectAsState()

        // UI relevant state
        val snackbarHostState = remember { SnackbarHostState() }
        var openDialog by remember { mutableStateOf(false) }
        var selectedGame by remember { mutableStateOf(0) }

        var splitFraction by remember { mutableStateOf(1f) }

        var gameName by remember { mutableStateOf("") }
        var selectedPlatform by remember { mutableStateOf(Platform.STEAM) }

        // When the Screen is replaced (due to a navigation event) load the data
        if (navigator.lastEvent == StackEvent.Replace) {
            if (filter == null) {
                stateScreenModel.loadGames()
            } else {
                stateScreenModel.loadGamesForPlatform(filter)
            }
        }

        LaunchedEffect(Unit) {
            // Load the data initially
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

        when (state) {
            is LibraryStateScreenModel.State.Init -> {
                Text("Init")
            }

            is LibraryStateScreenModel.State.Loading -> {
                // TODO : Implement a custom loading animation
                Text("Loading")
            }

            is LibraryStateScreenModel.State.Result -> {
                println(MaterialTheme.colorScheme.background)
                println(MaterialTheme.colorScheme.surface)
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = { AppBar(
                        searchResultState = searchResultState,
                        onQueryChange = { screenModel.searchGames(it) }
                    ) },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            onClick = { openDialog = true },
                            icon = { Icon(Icons.Filled.Add, "Import a new game") },
                            text = { Text(text = "Import Game") }
                        )
                    }
                ) { innerPadding ->
                    TwoPane(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        first = {
                            val games = (state as LibraryStateScreenModel.State.Result).games

                            LazyVerticalGrid(
                                contentPadding = PaddingValues(16.dp),
                                columns = GridCells.Adaptive(minSize = 200.dp),
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(games) {
                                    ElevatedCard(
                                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                        //colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        onClick = {
                                            selectedGame = it.id
                                            sideSheetScreenModel.loadFriends(it)
                                            splitFraction = 2f / 3f
                                        },
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
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        second = { LibrarySideSheet(
                            state = sideSheetState,
                            onSideSheetClosed = { splitFraction = 1f },
                            onDeleteGame = { stateScreenModel.deleteGame(selectedGame) },
                            onFriendRelationUpdated = { relation, change ->
                                sideSheetScreenModel.updateFriendRelations(
                                    selectedGame,
                                    relation,
                                    change
                                )
                            }
                        )},
                        strategy = HorizontalTwoPaneStrategy(
                            splitFraction = splitFraction
                        )
                    )

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

@Composable
private fun CreateGameDialog(
    onDismissRequest: () -> Unit,
    onSave: () -> Unit,
    onUpdateName: (gameName: String) -> Unit,
    onSelectPlatform: (platform: Platform) -> Unit,
    gameName: String,
    selectedPlatform: Platform,
    platforms: Array<Platform>
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
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
                        Row(
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
                    TextButton(onClick = { onDismissRequest() }) {
                        Text("Cancel")
                    }

                    TextButton(onClick = { onSave() }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    searchResultState: List<Game>,
    onQueryChange: (queryText: String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(top = 8.dp, start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Library", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "99 imported games",
                style = MaterialTheme.typography.titleSmall
            )
        }

        Box(
            modifier = Modifier
                .semantics { isTraversalGroup = true }
                .zIndex(1f)
                //.align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 16.dp),
        ) {
            DockedSearchBar(
                modifier = Modifier.semantics { traversalIndex = -1f },
                query = searchText,
                onQueryChange = { searchText = it; onQueryChange(it) },
                onSearch = { active = false },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text("Search") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = {
                        active = false
                        searchText = ""
                        //screenModel.resetSearchResults()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            ) {
                val searchResults = searchResultState.take(4)
                searchResults.forEach { game ->
                    ListItem(
                        headlineContent = { Text(game.name) },
                        supportingContent = { Text(game.platform.name) },
                        leadingContent = { Icon(Icons.Filled.VideogameAsset, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
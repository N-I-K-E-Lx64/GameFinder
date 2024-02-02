package de.hive.gamefinder.feature.library

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import de.hive.gamefinder.components.FormIconHeader
import de.hive.gamefinder.components.HorizontalTwoPaneStrategy
import de.hive.gamefinder.components.TwoPane
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.Platform
import de.hive.gamefinder.core.utils.UiEvents
import de.hive.gamefinder.feature.library.details.GameDetailsScreenModel
import de.hive.gamefinder.feature.library.details.LibrarySideSheet
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibraryScreen(val filter: Platform?) : Screen {

    companion object {
        const val IGDB_IMAGE_ENDPOINT = "https://images.igdb.com/igdb/image/upload/t_cover_big_2x/"
    }

    @OptIn(
        ExperimentalMaterial3Api::class
    )
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = getScreenModel<LibraryScreenModel>()
        val state by screenModel.state.collectAsState()
        val searchResultState by screenModel.searchResult.collectAsState()

        val gameDetailsScreenModel = getScreenModel<GameDetailsScreenModel>()
        val sideSheetState by gameDetailsScreenModel.state.collectAsState()

        // UI relevant state
        val snackbarHostState = remember { SnackbarHostState() }
        var openDialog by remember { mutableStateOf(false) }
        var selectedGame by remember { mutableStateOf(0) }

        var splitFraction by remember { mutableStateOf(1f) }

        // Filter states
        var filterPlatform by remember { mutableStateOf(-1) }
        var filterOnlineMultiplayer by remember { mutableStateOf(false) }
        var filterCampaignMultiplayer by remember { mutableStateOf(false) }

        var gameName by remember { mutableStateOf("") }
        var selectedPlatform by remember { mutableStateOf(Platform.STEAM) }

        fun applyFilter() {
            screenModel.filterGamesByQuery(filterPlatform, filterOnlineMultiplayer, filterCampaignMultiplayer)
        }

        // When the Screen is replaced (due to a navigation event) load the data
        if (navigator.lastEvent == StackEvent.Replace) {
            if (filter == null) {
                screenModel.loadGames()
            }
        }

        LaunchedEffect(Unit) {
            // Load the data initially
            screenModel.loadGames()

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
            is LibraryScreenModel.State.Init -> {
                Text("Init")
            }

            is LibraryScreenModel.State.Loading -> {
                // TODO : Implement a custom loading animation
                Text("Loading")
            }

            is LibraryScreenModel.State.Result -> {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        AppBar(
                            searchResultState = searchResultState,
                            onQueryChange = { screenModel.searchGames(it) },
                            onQueryDismissed = { screenModel.resetSearchResults() }
                        )
                    },
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
                            val games = (state as LibraryScreenModel.State.Result).games

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ){
                                FormIconHeader(
                                    Icons.Filled.RocketLaunch,
                                    contentDescription = "Launcher Filter Icon",
                                    headerText = "Launcher"
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    screenModel.platforms.forEach {
                                        FilterChip(
                                            selected = filterPlatform == it.ordinal,
                                            onClick = {
                                                filterPlatform = if (filterPlatform == it.ordinal) -1 else it.ordinal
                                                applyFilter()
                                            },
                                            label = { Text(it.platform) },
                                            leadingIcon = {
                                                if (filterPlatform == it.ordinal) {
                                                    Icon(
                                                        Icons.Filled.Done,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }

                                Divider(modifier = Modifier.padding(8.dp))

                                FormIconHeader(
                                    Icons.Filled.Groups,
                                    contentDescription = "Multiplayer Parameter Filter Icon",
                                    headerText = "Multiplayer"
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    FilterChip(
                                        selected = filterOnlineMultiplayer,
                                        onClick = { filterOnlineMultiplayer = !filterOnlineMultiplayer; applyFilter() },
                                        label = { Text("Online Multiplayer") },
                                        leadingIcon = {
                                            if (filterOnlineMultiplayer) {
                                                Icon(
                                                    Icons.Filled.Done,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                )
                                            }
                                        }
                                    )
                                    FilterChip(
                                        selected = filterCampaignMultiplayer,
                                        onClick = {
                                            filterCampaignMultiplayer = !filterCampaignMultiplayer; applyFilter()
                                        },
                                        label = { Text("Campaign Multiplayer") },
                                        leadingIcon = {
                                            if (filterCampaignMultiplayer) {
                                                Icon(
                                                    Icons.Filled.Done,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                )
                                            }
                                        }
                                    )
                                    /*Box {
                                        FilterChip(
                                            selected = filterOnlineMaxPlayers != 0,
                                            onClick = { filterCampaignMultiplayer = !filterCampaignMultiplayer },
                                            label = { if (filterOnlineMaxPlayers == 0) Text("Players") else Text(filterOnlineMaxPlayers.toString()) },
                                            leadingIcon = {
                                                if (filterCampaignMultiplayer) {
                                                    Icon(
                                                        Icons.Filled.Done,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                    )
                                                }
                                            },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.ArrowDropDown,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                )
                                            }
                                        )
                                    }*/
                                }

                                // TODO : Different card view - https://m3.material.io/foundations/layout/applying-layout/window-size-classes
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    val lazyGridState = rememberLazyGridState()

                                    LazyVerticalGrid(
                                        contentPadding = PaddingValues(16.dp),
                                        columns = GridCells.Adaptive(minSize = 200.dp),
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        state = lazyGridState
                                    ) {
                                        items(games) {
                                            ElevatedCard(
                                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                                onClick = {
                                                    selectedGame = it.id
                                                    splitFraction = 2f / 3f
                                                    // Initialize state in the game details screen model
                                                    gameDetailsScreenModel.loadFriends(it)
                                                    gameDetailsScreenModel.initializeParameterStates(it)
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

                                    VerticalScrollbar(
                                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                        adapter = rememberScrollbarAdapter(
                                            scrollState = lazyGridState
                                        ),
                                        //style = ScrollbarStyle(unhoverColor = MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        },
                        second = {
                            LibrarySideSheet(
                                state = sideSheetState,
                                screenModel = gameDetailsScreenModel,
                                onSideSheetClosed = { splitFraction = 1f },
                                onFriendRelationUpdated = { relation, change ->
                                    gameDetailsScreenModel.updateFriendRelations(
                                        selectedGame,
                                        relation,
                                        change
                                    )
                                }
                            )
                        },
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
                                platforms = screenModel.platforms
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
    onQueryChange: (queryText: String) -> Unit,
    onQueryDismissed: () -> Unit = {}
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
                        onQueryDismissed()
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
package de.hive.gamefinder.feature.library

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import de.hive.gamefinder.components.*
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.domain.GamePrediction
import de.hive.gamefinder.core.domain.GameStatus
import de.hive.gamefinder.core.domain.Launcher
import de.hive.gamefinder.core.utils.UiEvents
import de.hive.gamefinder.feature.library.details.GameDetailsScreenModel
import de.hive.gamefinder.feature.library.details.LibrarySideSheet
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class LibraryScreen(val filter: Launcher?) : Screen {

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class
    )
    @Composable
    override fun Content() {
        val windowSize = calculateWindowSizeClass()
        val cardOrientation: CardOrientation =
            when (windowSize.widthSizeClass) {
                WindowWidthSizeClass.Compact, WindowWidthSizeClass.Medium -> CardOrientation.VERTICAL
                WindowWidthSizeClass.Expanded -> CardOrientation.HORIZONTAL
                else -> CardOrientation.VERTICAL
            }

        val screenModel = getScreenModel<LibraryScreenModel>()
        val state by screenModel.state.collectAsState()
        val searchResultState by screenModel.searchResult.collectAsState()
        val gamePredictionState by screenModel.gamePredictions.collectAsState()

        val gameDetailsScreenModel = getScreenModel<GameDetailsScreenModel>()
        val sideSheetState by gameDetailsScreenModel.state.collectAsState()

        // UI relevant state
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val bottomSheetState = rememberModalBottomSheetState()
        var openImportGameDialog by remember { mutableStateOf(false) }
        var openChangeStateBottomSheet by remember { mutableStateOf(false) }
        var selectedGame by remember { mutableStateOf(0) }
        var statusChangeGameId by remember { mutableStateOf(-1) }

        var splitFraction by remember { mutableStateOf(1f) }

        var gameCount by remember { mutableStateOf(0) }

        // Filter states
        var filterPlatform by remember { mutableStateOf(-1) }
        var filterOnlineMultiplayer by remember { mutableStateOf(false) }
        var filterCampaignMultiplayer by remember { mutableStateOf(false) }

        var gameName by remember { mutableStateOf("") }
        var selectedLauncher by remember { mutableStateOf(Launcher.STEAM) }

        fun applyFilter() {
            screenModel.filterGamesByQuery(filterPlatform, filterOnlineMultiplayer, filterCampaignMultiplayer)
        }

        LaunchedEffect(Unit) {
            // Load the data initially
            screenModel.loadState()

            withContext(Dispatchers.Main.immediate) {
                screenModel.eventsFlow.collect { event ->
                    when (event) {
                        is UiEvents.ShowSnackbar -> {
                            snackbarHostState.showSnackbar(
                                message = event.message
                            )
                        }

                        is UiEvents.ShowSnackbarWithAction -> {
                            val result = snackbarHostState.showSnackbar(
                                message = event.message,
                                actionLabel = event.actionLabel,
                                withDismissAction = true
                            )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    val gameId = event.additionalData as Int
                                    // Open side sheet
                                    selectedGame = gameId
                                    splitFraction = 2f / 3f
                                    // Initialize state in the game details screen model
                                    gameDetailsScreenModel.loadState(gameId)
                                }
                                SnackbarResult.Dismissed -> {
                                    Napier.d { "Snackbar dismissed" }
                                }
                            }
                        }
                    }
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                AppBar(
                    gameCount = gameCount,
                    searchResultState = searchResultState,
                    onQueryChange = { screenModel.searchGames(it) },
                    onQueryDismissed = { screenModel.resetSearchResults() }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { openImportGameDialog = true },
                    icon = { Icon(Icons.Filled.Add, "Import a new game") },
                    text = { Text(text = "Import Game") }
                )
            }
        ) { innerPadding ->
            when (state) {
                is LibraryScreenModel.State.Init -> {
                }
                is LibraryScreenModel.State.Loading -> {
                }
                is LibraryScreenModel.State.Result -> {
                    val games = (state as LibraryScreenModel.State.Result).games
                    gameCount = games.size

                    TwoPane(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        first = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                FormIconHeader(
                                    Icons.Filled.RocketLaunch,
                                    contentDescription = "Launcher Filter Icon",
                                    headerText = "Launcher"
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    screenModel.launchers.forEach {
                                        FilterChip(
                                            selected = filterPlatform == it.ordinal,
                                            onClick = {
                                                filterPlatform = if (filterPlatform == it.ordinal) -1 else it.ordinal
                                                applyFilter()
                                            },
                                            label = { Text(it.launcher) },
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
                                    val columnMinSize = if (cardOrientation == CardOrientation.VERTICAL) 300.dp else 500.dp

                                    LazyVerticalGrid(
                                        contentPadding = PaddingValues(8.dp),
                                        columns = GridCells.Adaptive(minSize = columnMinSize),
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        state = lazyGridState
                                    ) {
                                        items(games) {game ->
                                            CoverImageCard(
                                                game = game,
                                                orientation = cardOrientation,
                                                isSelected = selectedGame == game.id,
                                                onCardClick = {
                                                    // Initialize state in the game details screen model
                                                    gameDetailsScreenModel.loadState(game.id)
                                                    // Open the side sheet
                                                    selectedGame = game.id
                                                    splitFraction = 2f / 3f
                                                },
                                                onChangeStateAction = {
                                                    openChangeStateBottomSheet = true
                                                    statusChangeGameId = game.id
                                                },
                                                onUpdateShortlistStatus = { screenModel.updateShortlistStatus(game.id, it) },
                                            )
                                        }
                                    }

                                    VerticalScrollbar(
                                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                        adapter = rememberScrollbarAdapter(
                                            scrollState = lazyGridState
                                        ),
                                        style = defaultScrollbarStyle().copy(
                                            unhoverColor = MaterialTheme.colorScheme.surfaceVariant,
                                            hoverColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        },
                        second = {
                            LibrarySideSheet(
                                state = sideSheetState,
                                screenModel = gameDetailsScreenModel,
                                onSideSheetClosed = { splitFraction = 1f },
                                onFriendRelationUpdated = { friendId, change ->
                                    gameDetailsScreenModel.updateFriendRelations(
                                        selectedGame,
                                        friendId,
                                        change
                                    )
                                }
                            )
                        },
                        strategy = HorizontalTwoPaneStrategy(
                            splitFraction = splitFraction
                        )
                    )

                    if (openImportGameDialog) {
                        ImportGameDialog(
                            onDismissRequest = { openImportGameDialog = false },
                            onSave = {
                                screenModel.addGame(it, selectedLauncher)
                                // Reset form values
                                gameName = ""
                                selectedLauncher = Launcher.STEAM
                                // Close the dialog
                                openImportGameDialog = false
                            },
                            onUpdateNameQuery = { gameName = it },
                            onSelectPlatform = { selectedLauncher = it },
                            onSearchForGamesAction = { screenModel.getGamePredictions(gameName) },
                            gameName = gameName,
                            selectedLauncher = selectedLauncher,
                            gamePredictions = gamePredictionState,
                            launchers = screenModel.launchers
                        )
                    }

                    // TODO : Approve design decision (Bottom Sheet)!
                    if (openChangeStateBottomSheet) {
                        ChangeStateBottomSheet(
                            sheetState = bottomSheetState,
                            onDismissRequest = { openChangeStateBottomSheet = false },
                            onListItemClick = {
                                screenModel.updateGameStatus(statusChangeGameId, GameStatus.entries[it])
                                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        openChangeStateBottomSheet = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImportGameDialog(
    onDismissRequest: () -> Unit,
    onSave: (gameId: Int) -> Unit,
    onUpdateNameQuery: (gameName: String) -> Unit,
    onSelectPlatform: (launcher: Launcher) -> Unit,
    onSearchForGamesAction: () -> Unit,
    gameName: String,
    selectedLauncher: Launcher,
    gamePredictions: List<GamePrediction>,
    launchers: Array<Launcher>
) {
    var desiredGameId by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Import a game",
                    style = MaterialTheme.typography.headlineSmall
                )

                AutoCompleteTextView(
                    query = gameName,
                    queryLabel = "Game name",
                    queryPlaceholder = "Anno 1800",
                    onQueryChanged = { onUpdateNameQuery(it) },
                    predictions = gamePredictions,
                    onDoneAction = { onSearchForGamesAction() },
                    onItemClick = {
                        desiredGameId = it.igdbGameId
                        onUpdateNameQuery(it.name)
                    }
                ) {
                    val releaseDate = it.releaseDate.toLocalDateTime(TimeZone.UTC).date
                    ListItem(
                        headlineContent = { Text(text = it.name) },
                        supportingContent = { Text(text = "Released $releaseDate") },
                        leadingContent = {
                            Icon(
                                if (it.igdbGameId == desiredGameId) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                                contentDescription = "is game selected for import"
                            )
                        }
                    )
                }

                Text("Platform", style = MaterialTheme.typography.titleMedium)

                Column(modifier = Modifier.selectableGroup()) {
                    launchers.forEach { platform ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (platform == selectedLauncher),
                                    onClick = { onSelectPlatform(platform) },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (platform == selectedLauncher),
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = { onDismissRequest() }) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = { onSave(desiredGameId) },
                        enabled = desiredGameId != 0
                    ) {
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
    gameCount: Int,
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
                text = "$gameCount imported games",
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
                        supportingContent = { Text(game.launcher.name) },
                        leadingContent = { Icon(Icons.Filled.VideogameAsset, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
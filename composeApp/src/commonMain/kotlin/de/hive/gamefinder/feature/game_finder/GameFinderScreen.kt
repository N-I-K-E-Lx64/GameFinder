package de.hive.gamefinder.feature.game_finder


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.hive.gamefinder.components.FormIconHeader
import de.hive.gamefinder.core.utils.ImageSize
import de.hive.gamefinder.core.utils.UiEvents
import de.hive.gamefinder.core.utils.getImageEndpoint
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameFinderScreen : Screen {

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val screenModel = getScreenModel<GameFinderScreenModel>()
        val state = screenModel.state.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val selectedFriends = remember { mutableStateListOf<Int>() }
        val deselectedTags = remember { mutableStateListOf<Int>() }

        var showNoFilterOptionsAlertDialog by remember { mutableStateOf(false) }

        fun selectFriend(friendId: Int) {
            if (friendId in selectedFriends) selectedFriends.remove(friendId) else selectedFriends.add(friendId)
        }
        fun selectTag(tagId: Int) {
            if (tagId in deselectedTags) deselectedTags.remove(tagId) else deselectedTags.add(tagId)
        }

        LaunchedEffect(Unit) {
            screenModel.loadSelectionOptions()

            withContext(Dispatchers.Main.immediate) {
                screenModel.eventsFlow.collect {
                    when (it) {
                        is UiEvents.ShowSnackbar -> {
                            snackbarHostState.showSnackbar(
                                message = it.message
                            )
                        }

                        is UiEvents.ShowSnackbarWithAction -> { }
                    }
                }
            }
        }

        Scaffold (
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "GameFinder",
                    style = MaterialTheme.typography.headlineLarge
                )

                when (state.value) {
                    is GameFinderScreenModel.State.Loading -> { }
                    is GameFinderScreenModel.State.Result -> {
                        val selectionState = state.value as GameFinderScreenModel.State.Result
                        val friends = selectionState.friends
                        val tags = selectionState.tags
                        val games = selectionState.games

                        showNoFilterOptionsAlertDialog = friends.isEmpty()

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (friends.isNotEmpty()) {
                                FormIconHeader(
                                    Icons.Filled.Group,
                                    contentDescription = "Friend Selection Header Icon",
                                    headerText = "Friends"
                                )

                                Column {
                                    friends.forEach { friend ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .toggleable(
                                                    value = (friend.id in selectedFriends),
                                                    onValueChange = { selectFriend(friend.id) },
                                                    role = Role.Checkbox
                                                )
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = (friend.id in selectedFriends),
                                                onCheckedChange = null
                                            )
                                            Text(
                                                text = friend.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.padding(start = 16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (tags.isNotEmpty()) {
                                FormIconHeader(
                                    Icons.Filled.Label,
                                    contentDescription = "Tag Selection Header Icon",
                                    headerText = "Tags"
                                )

                                FlowRow (
                                    modifier = Modifier.wrapContentHeight(align = Alignment.Top),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    tags.forEach {
                                        FilterChip(
                                            selected = (it.id !in deselectedTags),
                                            onClick = { selectTag(it.id) },
                                            label = { Text(it.tag) },
                                            leadingIcon = {
                                                if (it.id !in deselectedTags) {
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
                            }
                        }

                        FilledTonalButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = { screenModel.findGames(selectedFriends.toList(), deselectedTags.toList()) }
                        ) {
                            Text("Find Game")
                        }

                        LazyRow(
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(games) {
                                Box(
                                    modifier = Modifier
                                        .width(300.dp)
                                        .wrapContentHeight()
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    KamelImage(
                                        resource = asyncPainterResource(getImageEndpoint(it.coverImageId, ImageSize.COVER_BIG)),
                                        contentDescription = "${it.name} - Cover",
                                        contentScale = ContentScale.FillWidth,
                                        onLoading = { progress -> CircularProgressIndicator(progress) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showNoFilterOptionsAlertDialog) {
                // TODO : Replace this with BasicAlertDialog if compose multiplatform 1.6 arrives
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onDismissRequest.
                        showNoFilterOptionsAlertDialog = false
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .width(350.dp)
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.FilterListOff, contentDescription = "Alert dialog header icon")

                            Text(
                                text = "No filter Options",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Text(
                                text = "Since you haven't added any friends yet, I can't help you find the right games.",
                            )

                            TextButton(
                                onClick = {
                                    showNoFilterOptionsAlertDialog = false
                                    navigator.pop()
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
        }
    }
}

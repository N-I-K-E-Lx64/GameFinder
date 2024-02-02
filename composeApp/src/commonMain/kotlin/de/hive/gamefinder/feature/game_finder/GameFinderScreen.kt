package de.hive.gamefinder.feature.game_finder


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import de.hive.gamefinder.components.FormIconHeader
import de.hive.gamefinder.core.utils.UiEvents
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameFinderScreen : Screen {

    companion object {
        const val IGDB_IMAGE_ENDPOINT = "https://images.igdb.com/igdb/image/upload/t_cover_big_2x/"
    }

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<GameFinderScreenModel>()
        val state = screenModel.state.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val selectedFriends = remember { mutableStateListOf<Int>() }
        val deselectedTags = remember { mutableStateListOf<Int>() }

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
            ) {
                Text(
                    text = "GameFinder",
                    style = MaterialTheme.typography.displayMedium
                )

                when (state.value) {
                    is GameFinderScreenModel.State.Loading -> { }
                    is GameFinderScreenModel.State.Result -> {
                        val selectionState = state.value as GameFinderScreenModel.State.Result
                        val friends = selectionState.friends
                        val tags = selectionState.tags
                        val games = selectionState.games

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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

                        FilledTonalButton(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = { screenModel.findGames(selectedFriends.toList(), deselectedTags.toList()) }
                        ) {
                            Text("Find Game")
                        }

                        LazyRow(
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(games) {
                                KamelImage(
                                    resource = asyncPainterResource("$IGDB_IMAGE_ENDPOINT${it.coverImageId}.jpg"),
                                    contentDescription = "${it.name} - Cover",
                                    modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                                    //contentScale = ContentScale.Crop,
                                    onLoading = { progress -> CircularProgressIndicator(progress) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

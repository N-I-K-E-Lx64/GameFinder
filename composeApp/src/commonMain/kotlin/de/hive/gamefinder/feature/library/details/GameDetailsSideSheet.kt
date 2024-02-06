package de.hive.gamefinder.feature.library.details

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.hive.gamefinder.components.FormIconHeader
import de.hive.gamefinder.components.FormSliderRow
import de.hive.gamefinder.components.FormSwitchRow
import de.hive.gamefinder.core.adapter.objects.GameFriendRelation
import de.hive.gamefinder.core.domain.Tag

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun LibrarySideSheet(
    state: GameDetailsScreenModel.State,
    screenModel: GameDetailsScreenModel,
    onSideSheetClosed: () -> Unit,
    onFriendRelationUpdated: (relation: GameFriendRelation, change: Boolean) -> Unit
) {
    var tagQuery by rememberSaveable { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf(0) }
    val predictions by screenModel.searchResults.collectAsState()

    val hasOnlineCoop by screenModel.onlineCoopState.collectAsState()
    val hasCampaignCoop by screenModel.campaignCoopState.collectAsState()
    val onlineCoopMaxPlayers by screenModel.maxOnlineCoopPlayers.collectAsState()
    val updateButtonVisibility by screenModel.updateButtonVisibility.collectAsState()

    when (state) {
        is GameDetailsScreenModel.State.Loading -> {}
        is GameDetailsScreenModel.State.Result -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val game = state.game
                val friends = state.friendsOwningGame

                val lazyColumnState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 80.dp)
                        //.background(MaterialTheme.colorScheme.inverseOnSurface)
                        .clip(RoundedCornerShape(16.dp)),
                    contentPadding = PaddingValues(8.dp),
                    state = lazyColumnState
                ) {
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillParentMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = game.name,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Row {
                                    IconButton(
                                        onClick = { screenModel.deleteGame(game.id) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                    IconButton(
                                        onClick = { onSideSheetClosed() }
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Close side sheet")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        FormIconHeader(
                            Icons.Filled.PersonAdd,
                            contentDescription = "Add Friend Icon",
                            headerText = "Friends"
                        )

                        Column {
                            friends.forEach { relation ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .toggleable(
                                            value = relation.doesFriendOwnGame,
                                            onValueChange = { onFriendRelationUpdated(relation, it) },
                                            role = Role.Checkbox
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = relation.doesFriendOwnGame,
                                        onCheckedChange = null
                                    )
                                    Text(
                                        text = relation.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(16.dp))
                    }

                    item {
                        FormIconHeader(
                            Icons.Filled.Label,
                            contentDescription = "Tag List Icon",
                            headerText = "Tags"
                        )

                        FlowRow(
                            modifier = Modifier.wrapContentHeight(align = Alignment.Top),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            state.game.tags.forEach {
                                InputChip(
                                    label = { Text(it.tag) },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Delete Category",
                                            modifier = Modifier
                                                .size(InputChipDefaults.AvatarSize)
                                                .clickable(onClick = {
                                                    screenModel.removeTagFromGame(
                                                        state.game.id,
                                                        it.id
                                                    )
                                                }),
                                        )
                                    },
                                    selected = it.id == selectedTag,
                                    onClick = { selectedTag = it.id }
                                )
                            }
                            AutoCompleteTextView(
                                query = tagQuery,
                                queryLabel = "Tags",
                                onQueryChanged = { tagQuery = it; screenModel.searchTags(tagQuery) },
                                onDoneAction = {
                                    screenModel.createTag(state.game.id, tagQuery)
                                    tagQuery = ""
                                },
                                predictions = predictions,
                                onItemClick = { screenModel.addTagToGame(state.game.id, it.id) }
                            )
                        }

                        Divider(modifier = Modifier.padding(16.dp))
                    }

                    item {
                        FormIconHeader(
                            Icons.Filled.ConnectWithoutContact,
                            contentDescription = "Multiplayer Options Icon",
                            headerText = "Multiplayer"
                        )

                        FormSwitchRow(
                            headlineText = "Online Coop",
                            switchValue = hasOnlineCoop,
                            onSwitchValueChange = { screenModel.updateOnlineCoopState(it) }
                        )

                        FormSwitchRow(
                            headlineText = "Campaign Coop",
                            switchValue = hasCampaignCoop,
                            switchEnabled = hasOnlineCoop,
                            onSwitchValueChange = { screenModel.updateCampaignCoopState(it) }
                        )

                        FormSliderRow(
                            headlineText = "Online Coop Max. Players",
                            sliderValue = onlineCoopMaxPlayers,
                            sliderSteps = 13,
                            sliderValueRange = 2f..16f,
                            onSliderValueChangeFinished = { screenModel.updateMaxOnlineCoopPlayers(it) },
                            sliderEnabled = hasOnlineCoop
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            FilledTonalButton(
                                onClick = { screenModel.updateMultiplayerParameters(game.id) },
                                enabled = updateButtonVisibility
                            ) {
                                Text("Update")
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .padding(end = 4.dp),
                    adapter = rememberScrollbarAdapter(
                        scrollState = lazyColumnState
                    ),
                    style = defaultScrollbarStyle().copy(
                        unhoverColor = MaterialTheme.colorScheme.surfaceVariant,
                        hoverColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
fun <T> AutoCompleteTextView(
    modifier: Modifier = Modifier,
    query: String,
    queryLabel: String,
    onQueryChanged: (String) -> Unit = {},
    predictions: List<T>,
    onDoneAction: () -> Unit = {},
    onItemClick: (T) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier.heightIn(max = TextFieldDefaults.MinHeight * 4)
    ) {
        item {
            QuerySearch(
                query = query,
                label = queryLabel,
                onDoneAction = onDoneAction,
                onQueryChanged = onQueryChanged
            )
        }

        if (predictions.isNotEmpty()) {
            items(predictions) {
                val tag = it as Tag
                ListItem(
                    headlineContent = { Text(tag.tag) },
                    leadingContent = { Icon(Icons.Filled.NewLabel, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(it) }
                )
            }
        }
    }

}

@Composable
fun QuerySearch(
    modifier: Modifier = Modifier,
    query: String,
    label: String,
    onDoneAction: () -> Unit = {},
    onQueryChanged: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = { onDoneAction() }
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        )
    )
}
package de.hive.gamefinder.feature.library.details

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.hive.gamefinder.components.AutoCompleteTextView
import de.hive.gamefinder.components.FormIconHeader
import de.hive.gamefinder.components.FormSliderRow
import de.hive.gamefinder.components.FormSwitchRow
import gamefinder.composeapp.generated.resources.*
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class, ExperimentalResourceApi::class)
@Composable
fun LibrarySideSheet(
    state: GameDetailsScreenModel.State,
    screenModel: GameDetailsScreenModel,
    onSideSheetClosed: () -> Unit,
    onFriendRelationUpdated: (friendId: Int, change: Boolean) -> Unit
) {
    var tagQuery by rememberSaveable { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf(0) }
    val predictions by screenModel.searchResults.collectAsState()

    val hasOnlineCoop by screenModel.onlineCoopState.collectAsState()
    val hasCampaignCoop by screenModel.campaignCoopState.collectAsState()
    val onlineCoopMaxPlayers by screenModel.maxOnlineCoopPlayers.collectAsState()
    val updateButtonVisibility by screenModel.updateButtonVisibility.collectAsState()
    val game by screenModel.game.collectAsState()

    when (state) {
        is GameDetailsScreenModel.State.Loading -> {}
        is GameDetailsScreenModel.State.Result -> {
            game?.let {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val friends = state.friends
                    val relations = state.friendsOwningGame

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
                                Row {
                                    Text(
                                        text = it.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        softWrap = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { screenModel.deleteGame(it.id) }
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

                        item {
                            FormIconHeader(
                                Icons.Filled.PersonAdd,
                                contentDescription = "Add Friend Icon",
                                headerText = stringResource(Res.string.friends_form_header)
                            )
                            Column {
                                friends.forEach { friend ->
                                    val checkboxValue = relations.first { it.friendId == friend.id }.doesFriendOwnGame
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .toggleable(
                                                value = checkboxValue,
                                                onValueChange = { onFriendRelationUpdated(friend.id, it) },
                                                role = Role.Checkbox
                                            )
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = checkboxValue,
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

                            HorizontalDivider(modifier = Modifier.padding(16.dp))
                        }

                        item {
                            FormIconHeader(
                                Icons.AutoMirrored.Filled.Label,
                                contentDescription = "Tag List Icon",
                                headerText = stringResource(Res.string.tags_form_header)
                            )

                            FlowRow(
                                modifier = Modifier.wrapContentHeight(align = Alignment.Top),
                                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                it.tags.forEach {
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
                                                            game!!.id,
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
                                    queryLabel = stringResource(Res.string.details_tag_textview_label),
                                    queryPlaceholder = "Survival",
                                    onQueryChanged = {
                                        tagQuery = it
                                        screenModel.searchTags(tagQuery)
                                    },
                                    onDoneAction = {
                                        screenModel.createTag(it.id, tagQuery)
                                        tagQuery = ""
                                    },
                                    predictions = predictions,
                                    onItemClick = { screenModel.addTagToGame(it.id, it.id) }
                                ) {
                                    ListItem(
                                        headlineContent = { Text(it.tag) },
                                        leadingContent = { Icon(Icons.Filled.NewLabel, contentDescription = null) },
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(16.dp))
                        }

                        item {
                            FormIconHeader(
                                Icons.Filled.ConnectWithoutContact,
                                contentDescription = "Multiplayer Options Icon",
                                headerText = stringResource(Res.string.multiplayer_form_header)
                            )

                            FormSwitchRow(
                                headlineText = stringResource(Res.string.online_coop_form_header),
                                switchValue = hasOnlineCoop,
                                onSwitchValueChange = { screenModel.updateOnlineCoopState(it) }
                            )

                            FormSwitchRow(
                                headlineText = stringResource(Res.string.campaign_coop_form_header),
                                switchValue = hasCampaignCoop,
                                switchEnabled = hasOnlineCoop,
                                onSwitchValueChange = { screenModel.updateCampaignCoopState(it) }
                            )

                            FormSliderRow(
                                headlineText = stringResource(Res.string.maxplayers_form_header),
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
                                    onClick = { screenModel.updateMultiplayerParameters(it.id) },
                                    enabled = updateButtonVisibility
                                ) {
                                    Text(stringResource(Res.string.details_update_button_label))
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
}
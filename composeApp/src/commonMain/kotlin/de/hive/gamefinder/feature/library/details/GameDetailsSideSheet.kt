package de.hive.gamefinder.feature.library.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.hive.gamefinder.core.adapter.objects.GameFriendRelation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LibrarySideSheet(
    state: GameDetailsStateScreenModel.State,
    screenModel: GameDetailsScreenModel,
    onSideSheetClosed: () -> Unit,
    onDeleteGame: () -> Unit,
    onFriendRelationUpdated: (relation: GameFriendRelation, change: Boolean) -> Unit
) {
    //var text by remember { mutableStateOf("") }
    var tagQuery by rememberSaveable { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf(0) }
    val predictions by screenModel.searchResults.collectAsState()

    when (state) {
        is GameDetailsStateScreenModel.State.Loading -> {}
        is GameDetailsStateScreenModel.State.Result -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.game.name,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Row {
                        IconButton(
                            onClick = { onDeleteGame() }
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

                Text(
                    text = "Friends",
                    style = MaterialTheme.typography.titleSmall
                )

                Column {
                    state.friendsOwningGame.forEach { relation ->
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

                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.titleSmall
                )

                FlowRow(
                    modifier = Modifier.wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(4.dp
                    )
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
                                        .clickable(onClick = { screenModel.removeTagFromGame(state.game.id, it.id)}),
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
                    ) {
                        Text(it.tag)
                    }
                }
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
    itemContent: @Composable (T) -> Unit = {}
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onItemClick(it) }
                ) {
                    itemContent(it)
                }
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
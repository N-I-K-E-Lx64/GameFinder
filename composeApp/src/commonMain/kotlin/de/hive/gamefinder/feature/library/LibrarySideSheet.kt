package de.hive.gamefinder.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.hive.gamefinder.core.adapter.objects.GameFriendRelation

@Composable
fun LibrarySideSheet(
    state: LibrarySideSheetScreenModel.State,
    onSideSheetClosed: () -> Unit,
    onDeleteGame: () -> Unit,
    onFriendRelationUpdated: (relation: GameFriendRelation, change: Boolean) -> Unit
) {
    when (state) {
        is LibrarySideSheetScreenModel.State.Loading -> {}
        is LibrarySideSheetScreenModel.State.Result -> {
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
            }
        }
    }
}
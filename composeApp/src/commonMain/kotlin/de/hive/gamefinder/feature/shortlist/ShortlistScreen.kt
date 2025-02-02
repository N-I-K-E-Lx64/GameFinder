package de.hive.gamefinder.feature.shortlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import coil3.compose.AsyncImage
import de.hive.gamefinder.core.utils.ImageSize
import de.hive.gamefinder.core.utils.getImageEndpoint
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyColumnState

class ShortlistScreen : Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ShortlistScreenModel>()
        val state by screenModel.state.collectAsState()

        val lazyListState = rememberLazyListState()

        LaunchedEffect(Unit) {
            // Load the data initially
            screenModel.loadState()
        }

        Scaffold { innerPadding ->
            when (state) {
                is ShortlistScreenModel.State.Loading -> {
                }
                is ShortlistScreenModel.State.Result -> {
                    val gamesOnShortlist = (state as ShortlistScreenModel.State.Result).gamesOnShortlist

                    var shortlist by remember { mutableStateOf(gamesOnShortlist) }
                    val reorderableLazyColumnState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
                        shortlist = shortlist.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                    }

                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Shortlist", style = MaterialTheme.typography.headlineLarge)

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(2/3f),
                            state = lazyListState,
                        ) {
                            items(shortlist, key = { it.id }) { game ->
                                ReorderableItem(reorderableLazyColumnState, game.id) {
                                    val interactionSource = remember { MutableInteractionSource() }

                                    ListItem(
                                        headlineContent = { Text(text = game.name) },
                                        supportingContent = { Text(text = game.launcher.launcher) },
                                        leadingContent = {
                                            Box(
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .height(56.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            ) {
                                                AsyncImage(
                                                    model = getImageEndpoint(game.coverImageId, ImageSize.LOGO_MED),
                                                    contentDescription = "${game.name} - Thumb",
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        },
                                        trailingContent = {
                                            IconButton(
                                                onClick = {},
                                                modifier = Modifier.draggableHandle(
                                                    onDragStopped = { screenModel.updateShortlistPosition(shortlist) },
                                                    interactionSource = interactionSource
                                                )
                                            ) {
                                                Icon(Icons.Default.DragHandle, contentDescription = "Reorder")
                                            }
                                        }
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
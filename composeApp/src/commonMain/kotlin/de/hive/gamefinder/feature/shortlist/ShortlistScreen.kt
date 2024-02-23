package de.hive.gamefinder.feature.shortlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.valentinilk.shimmer.shimmer
import de.hive.gamefinder.core.utils.ImageSize
import de.hive.gamefinder.core.utils.getImageEndpoint
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
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

        Scaffold{innerPadding ->
            when (state) {
                is ShortlistScreenModel.State.Loading -> {
                }
                is ShortlistScreenModel.State.Result -> {
                    val gamesOnShortlist = (state as ShortlistScreenModel.State.Result).gamesOnShortlist

                    // TODO : Optimize this!
                    var list by remember { mutableStateOf(gamesOnShortlist) }
                    val reorderableLazyColumnState = rememberReorderableLazyColumnState(lazyListState) { from, to ->
                        list = list.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                            // TODO : Update Shortlist position
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        state = lazyListState,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(list, key = { it.id }) { game ->
                            ReorderableItem(reorderableLazyColumnState, game.id) {
                                val interactionSource = remember { MutableInteractionSource() }

                                ListItem(
                                    headlineContent = { Text(text = game.name) },
                                    supportingContent = { Text(text = game.launcher.launcher) },
                                    leadingContent = {
                                        Box(
                                            modifier = Modifier
                                                .width(64.dp)
                                                .height(56.dp)
                                        ) {
                                            KamelImage(
                                                resource = asyncPainterResource(getImageEndpoint(game.coverImageId, ImageSize.COVER_SMALL)),
                                                contentDescription = "${game.name} - Thumb",
                                                contentScale = ContentScale.Fit,
                                                onLoading = {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .shimmer()
                                                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                                                    )
                                                }
                                            )
                                        }
                                    },
                                    trailingContent = {
                                        IconButton(
                                            onClick = {},
                                            modifier = Modifier.draggableHandle(
                                                interactionSource = interactionSource
                                            )
                                        ) {
                                            Icon(Icons.Default.DragHandle, contentDescription = "Reorder")
                                        }
                                    }
                                )
                                Divider(modifier = Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
package de.hive.gamefinder.feature.shortlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

class ShortlistScreen : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ShortlistScreenModel>()
        val state by screenModel.state.collectAsState()

        val lazyListState = rememberLazyListState()

        LaunchedEffect(Unit) {
            // Load the data initially
            screenModel.loadState()
        }

        Scaffold{
            when (state) {
                is ShortlistScreenModel.State.Loading -> {
                }
                is ShortlistScreenModel.State.Result -> {
                    val gamesOnShortlist = (state as ShortlistScreenModel.State.Result).gamesOnShortlist

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        state = lazyListState,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(gamesOnShortlist) {
                            ListItem(
                                headlineContent = { Text(text = it.name) },
                                supportingContent = { Text(text = it.launcher.launcher) },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .width(64.dp)
                                            .height(56.dp)
                                    ) {
                                        KamelImage(
                                            resource = asyncPainterResource(getImageEndpoint(it.coverImageId, ImageSize.COVER_SMALL)),
                                            contentDescription = "${it.name} - Thumb",
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
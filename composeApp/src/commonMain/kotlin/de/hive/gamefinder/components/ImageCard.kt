package de.hive.gamefinder.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.feature.library.LibraryScreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.text.Typography.middleDot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoverImageCard(
    game: Game,
    orientation: CardOrientation,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onChangeStateAction: () -> Unit,
    onAddToShortlistAction: () -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor =
            if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = { onCardClick() },
    ) {
        when (orientation) {
            CardOrientation.VERTICAL -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    KamelImage(
                        resource = asyncPainterResource("${LibraryScreen.IGDB_IMAGE_ENDPOINT}${game.coverImageId}.jpg"),
                        contentDescription = "${game.name} - Cover",
                        contentScale = ContentScale.Crop,
                        onLoading = { CircularProgressIndicator(progress = { it }) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = game.name,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "${game.launcher.launcher} $middleDot ${game.gameStatus.statusValue}",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.titleSmall
                        )

                        LibraryEntryAssistantChipRow(
                            onChangeStateAction = { onChangeStateAction() },
                            onAddToShortlistAction = { onAddToShortlistAction() }
                        )
                    }
                }
            }

            CardOrientation.HORIZONTAL -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    KamelImage(
                        resource = asyncPainterResource("${LibraryScreen.IGDB_IMAGE_ENDPOINT}${game.coverImageId}.jpg"),
                        contentDescription = "${game.name} - Cover",
                        contentScale = ContentScale.Crop,
                        onLoading = { CircularProgressIndicator(progress = { it }) },
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = game.name,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "${game.launcher.launcher} $middleDot ${game.gameStatus.statusValue}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = game.summary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        LibraryEntryAssistantChipRow(
                            onChangeStateAction = { onChangeStateAction() },
                            onAddToShortlistAction = { onAddToShortlistAction() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryEntryAssistantChipRow(
    onChangeStateAction: () -> Unit,
    onAddToShortlistAction: () -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalArrangement = Arrangement.Bottom
    ) {
        AssistChip(
            onClick = { onChangeStateAction() },
            label = { Text("Change state") },
            leadingIcon = {
                Icon(
                    Icons.Default.ChangeCircle,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )

        AssistChip(
            onClick = { onAddToShortlistAction() },
            label = { Text("Add to shortlist") },
            leadingIcon = {
                Icon(
                    Icons.Default.BookmarkAdd,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
    }
}
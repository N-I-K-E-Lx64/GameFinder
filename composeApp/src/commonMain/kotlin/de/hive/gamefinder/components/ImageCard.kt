package de.hive.gamefinder.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import de.hive.gamefinder.core.domain.Game
import de.hive.gamefinder.core.utils.ImageSize
import de.hive.gamefinder.core.utils.getImageEndpoint
import gamefinder.composeapp.generated.resources.Res
import gamefinder.composeapp.generated.resources.game_action_addToShortlist
import gamefinder.composeapp.generated.resources.game_action_changeState
import gamefinder.composeapp.generated.resources.game_action_removeFromShortlist
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import kotlin.text.Typography.middleDot

@Composable
fun CoverImageCard(
    game: Game,
    orientation: CardOrientation,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onChangeStateAction: () -> Unit,
    onUpdateShortlistStatus: (addToShortlist: Boolean) -> Unit
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
                    AsyncImage(model = getImageEndpoint(game.coverImageId, ImageSize.COVER_BIG),
                        contentDescription = "${game.name} - Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)))

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
                            isGameOnShortlist = game.isShortlist,
                            onChangeStateAction = { onChangeStateAction() },
                            onUpdateShortlistStatus = { onUpdateShortlistStatus(it) }
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
                    AsyncImage(
                        model = getImageEndpoint(game.coverImageId, ImageSize.COVER_BIG),
                        contentDescription = "${game.name} - Cover",
                        contentScale = ContentScale.Crop,
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
                            isGameOnShortlist = game.isShortlist,
                            onChangeStateAction = { onChangeStateAction() },
                            onUpdateShortlistStatus = { onUpdateShortlistStatus(it) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalResourceApi::class)
@Composable
fun LibraryEntryAssistantChipRow(
    isGameOnShortlist: Boolean,
    onChangeStateAction: () -> Unit,
    onUpdateShortlistStatus: (addToShortlist: Boolean) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalArrangement = Arrangement.Bottom
    ) {
        AssistChip(
            onClick = { onChangeStateAction() },
            label = { Text(stringResource(Res.string.game_action_changeState)) },
            leadingIcon = {
                Icon(
                    Icons.Default.ChangeCircle,
                    contentDescription = null,
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )

        if (!isGameOnShortlist) {
            AssistChip(
                onClick = { onUpdateShortlistStatus(true) },
                label = { Text(stringResource(Res.string.game_action_addToShortlist)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.BookmarkAdd,
                        contentDescription = "Add game to shortlist",
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            )
        } else {
            AssistChip(
                onClick = { onUpdateShortlistStatus(false) },
                label = { Text(stringResource(Res.string.game_action_removeFromShortlist)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.BookmarkRemove,
                        contentDescription = "Remove game from shortlist",
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            )
        }
    }
}
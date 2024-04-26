package de.hive.gamefinder.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import gamefinder.composeapp.generated.resources.Res
import gamefinder.composeapp.generated.resources.state_selection_body
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun ChangeStateBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onListItemClick: (Int) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(stringResource(Res.string.state_selection_body), style = MaterialTheme.typography.titleLarge)

            CHANGE_STATE_ITEMS.forEachIndexed { index, item ->
                ListItem(
                    headlineContent = { Text(item.headingText) },
                    supportingContent = { Text(item.supportingText) },
                    leadingContent = {
                        Icon(item.leadingIcon, contentDescription = "${item.headingText} - Icon")
                    },
                    modifier = Modifier.clickable { onListItemClick(index) }
                )
                HorizontalDivider()
            }
        }
    }
}

private data class BottomSheetListItem(
    val headingText: String,
    val supportingText: String,
    val leadingIcon: ImageVector
)

private val CHANGE_STATE_ITEMS: List<BottomSheetListItem> = listOf(
    BottomSheetListItem("Library", "The game is part of your library, but currently not installed", Icons.Default.VideoLibrary),
    BottomSheetListItem("Installed", "The game is installed on your PC", Icons.Default.InstallDesktop),
    BottomSheetListItem("Playing", "You are currently playing the game", Icons.Default.SmartDisplay),
    BottomSheetListItem("Paused", "You have stopped actively playing the game, but it is still installed", Icons.Default.PausePresentation),
    BottomSheetListItem("Pile of shame", "Games that you bought but haven't played since", Icons.Default.Layers),
    //BottomSheetListItem("Waiting", "For the release, a certain update, mod or DLC", Icons.Filled.HourglassTop),
)
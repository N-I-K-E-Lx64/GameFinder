package de.hive.gamefinder.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CoverImageCard(
    game: Game,
    orientation: CardOrientation,
    isSelected: Boolean,
    onCardClick: () -> Unit
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
                        onLoading = { progress -> CircularProgressIndicator(progress) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Text(
                        text = game.name,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = game.platform.platform,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            CardOrientation.HORIZONTAL -> {
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    KamelImage(
                        resource = asyncPainterResource("${LibraryScreen.IGDB_IMAGE_ENDPOINT}${game.coverImageId}.jpg"),
                        contentDescription = "${game.name} - Cover",
                        contentScale = ContentScale.Crop,
                        onLoading = { progress -> CircularProgressIndicator(progress) },
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = game.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = game.platform.platform,
                            style = MaterialTheme.typography.titleMedium
                        )
                        // TODO : Think about adding this text!
                        Text(text = "Awaken as a vampire. Hunt for blood in nearby settlements to regain your strength and evade the scorching sun to survive. Raise your castle and thrive in an ever-changing open world full of mystery. Gain allies online and conquer the land of the living.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
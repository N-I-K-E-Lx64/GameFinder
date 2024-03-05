package de.hive.gamefinder.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import gamefinder.composeapp.generated.resources.BebasNeue_Regular
import gamefinder.composeapp.generated.resources.Res
import gamefinder.composeapp.generated.resources.app_name
import gamefinder.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Name and logo
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                modifier = Modifier.width(40.dp).height(40.dp)
            )
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily(Font(Res.font.BebasNeue_Regular)),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Helpful options
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.Publish, contentDescription = "Export game library")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Download, contentDescription = "Import game library")
            }
            IconButton(onClick = {}) {
                Icon(Icons.AutoMirrored.Filled.Help, contentDescription = "Explanation")
            }
        }
    }
}
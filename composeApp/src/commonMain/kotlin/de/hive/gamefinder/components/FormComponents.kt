package de.hive.gamefinder.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FormIconHeader(
    icon: ImageVector,
    contentDescription: String,
    headerText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
        Text(text = headerText, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun FormSwitchRow(
    headlineText: String,
    switchValue: Boolean,
    switchEnabled: Boolean = true,
    onSwitchValueChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(headlineText) },
        trailingContent = {
            Switch(
                checked = switchValue,
                onCheckedChange = { onSwitchValueChange(it) },
                enabled = switchEnabled
            )
        }
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSliderRow(
    headlineText: String,
    sliderValue: Int,
    sliderSteps: Int,
    sliderValueRange: ClosedFloatingPointRange<Float>,
    sliderEnabled: Boolean = true,
    onSliderValueChangeFinished: (Int) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(sliderValue.toFloat()) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(headlineText, style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            steps = sliderSteps,
            valueRange = sliderValueRange,
            onValueChangeFinished = { onSliderValueChangeFinished(sliderPosition.toInt()) },
            enabled = sliderEnabled,
            /*thumb = {
                Label(
                    label = {
                        PlainTooltip(
                            modifier = Modifier
                                .requiredSize(45.dp, 25.dp)
                                .wrapContentWidth()
                        ) {
                            Text("%.2f".format(sliderPosition))
                        }
                    },
                    interactionSource = interactionSource
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }
            }*/
        )
    }
}
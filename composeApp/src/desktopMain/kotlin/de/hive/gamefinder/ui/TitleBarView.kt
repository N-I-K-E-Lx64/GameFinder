package de.hive.gamefinder.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DecoratedWindowScope.TitleBarView() {
    TitleBar(
        Modifier.newFullscreenControls(),
        gradientStartColor = DesktopViewModel.projectColor,
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    "icons/kotlin.svg",
                    "Logo",
                    StandaloneSampleIcons.javaClass
                )
                Text("GameFinder", fontSize = 16.sp)
            }
        }

        Row(Modifier.align(Alignment.End)) {
            Tooltip({
                when (DesktopViewModel.theme) {
                    IntUiThemes.Light -> Text("Switch to light theme with light header")
                    IntUiThemes.Dark, IntUiThemes.System -> Text("Switch to light theme")
                }
            }) {
                IconButton({
                    DesktopViewModel.theme = when (DesktopViewModel.theme) {
                        IntUiThemes.Light -> IntUiThemes.Dark
                        IntUiThemes.Dark, IntUiThemes.System -> IntUiThemes.Light
                    }
                }, Modifier.size(40.dp).padding(5.dp)) {
                    when (DesktopViewModel.theme) {
                        IntUiThemes.Light -> Icon(
                            "icons/lightTheme@20x20.svg",
                            "Themes",
                            StandaloneSampleIcons::class.java,
                        )

                        IntUiThemes.Dark -> Icon(
                            "icons/darkTheme@20x20.svg",
                            "Themes",
                            StandaloneSampleIcons::class.java,
                        )

                        IntUiThemes.System -> Icon(
                            "icons/systemTheme@20x20.svg",
                            "Themes",
                            StandaloneSampleIcons::class.java,
                        )
                    }
                }
            }
        }
    }
}
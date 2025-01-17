package de.hive.gamefinder

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import de.hive.gamefinder.ui.DesktopViewModel
import de.hive.gamefinder.ui.IntUiThemes
import de.hive.gamefinder.ui.TitleBarView
import de.hive.gamefinder.ui.theme.AppTheme
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToSvgPainter
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.styling.TitleBarStyle

fun main() {
    //KoinInit().init()

    val icon = svgResource("icons/kotlin.svg")

    return application {
        val themeDefinition =
            if (DesktopViewModel.theme.isDark()) {
                JewelTheme.darkThemeDefinition()
            } else {
                JewelTheme.lightThemeDefinition()
            }

        IntUiTheme(
            themeDefinition,
            ComponentStyling.decoratedWindow(
                titleBarStyle = when (DesktopViewModel.theme) {
                    IntUiThemes.Light -> TitleBarStyle.light()
                    IntUiThemes.Dark -> TitleBarStyle.dark()
                    IntUiThemes.System -> if (DesktopViewModel.theme.isDark()) {
                        TitleBarStyle.dark()
                    } else {
                        TitleBarStyle.light()
                    }
                }
            )
        ) {
            DecoratedWindow(
                onCloseRequest = { exitApplication() },
                state = rememberWindowState(width = 1400.dp, height = 960.dp),
                title = "GameFinder",
                icon = icon
            ) {
                TitleBarView()

                AppTheme(DesktopViewModel.theme.isDark()) {
                    App()
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
private fun svgResource(resourcePath: String): Painter =
    checkNotNull(ResourceLoader.javaClass.classLoader.getResourceAsStream(resourcePath)) {
        "Could not load resource $resourcePath: it does not exist or can't be read."
    }
        .readAllBytes()
        .decodeToSvgPainter(Density(1f))

private object ResourceLoader

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}
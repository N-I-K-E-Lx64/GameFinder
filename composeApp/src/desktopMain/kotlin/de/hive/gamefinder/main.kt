package de.hive.gamefinder

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.application
import de.hive.gamefinder.di.KoinInit
import de.hive.gamefinder.ui.DesktopViewModel
import de.hive.gamefinder.ui.IntUiThemes
import de.hive.gamefinder.ui.TitleBarView
import de.hive.gamefinder.ui.theme.AppTheme
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
import org.koin.core.Koin

lateinit var koin: Koin

fun main() {

    koin = KoinInit().init()

    return application {

        // val useDarkTheme = true

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
                title = "GameFinder"
            ) {
                TitleBarView()

                AppTheme(DesktopViewModel.theme.isDark()) {
                    App()
                }
            }
        }
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}
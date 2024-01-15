package de.hive.gamefinder

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.hive.gamefinder.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "GameFinder") {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}
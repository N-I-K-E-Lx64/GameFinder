package de.hive.gamefinder.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object DesktopViewModel {

    var theme: IntUiThemes by mutableStateOf(IntUiThemes.System)

    val projectColor = Color(0xff654b40)
}
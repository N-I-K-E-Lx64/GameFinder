package de.hive.gamefinder.core.utils

sealed class UiEvents {
    data class ShowSnackbar(val message: String) : UiEvents()
}
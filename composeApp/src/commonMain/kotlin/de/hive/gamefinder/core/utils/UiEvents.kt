package de.hive.gamefinder.core.utils

sealed class UiEvents {
    data class ShowSnackbar(val message: String) : UiEvents()
    data class ShowSnackbarWithAction(val message: String, val actionLabel: String, val additionalData: Any) : UiEvents()
}
package de.hive.gamefinder.components

/**
 * Different type of navigation supported by the app depending on device size and state.
 */
enum class NavigationType {
    BOTTOM_NAVIGATION,
    NAVIGATION_RAIL,
    PERMANENT_NAVIGATION_DRAWER
}

enum class LayoutType {
    HEADER,
    CONTENT
}

enum class CardOrientation {
    VERTICAL,
    HORIZONTAL
}
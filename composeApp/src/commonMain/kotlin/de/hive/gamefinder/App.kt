package de.hive.gamefinder

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import de.hive.gamefinder.components.NavigationType
import de.hive.gamefinder.di.coreModule
import de.hive.gamefinder.di.getPlatformModule
import de.hive.gamefinder.feature.library.LibraryScreen
import de.hive.gamefinder.feature.navigation.NavigationWrapper
import org.koin.compose.KoinApplication

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App() {
    KoinApplication(application = {
        modules(
            coreModule,
            getPlatformModule())
    }) {
        val windowSize = calculateWindowSizeClass()
        val navigationType: NavigationType

        // Use standard navigation drawer on large and extra large screens
        when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                navigationType = NavigationType.BOTTOM_NAVIGATION
            }

            WindowWidthSizeClass.Medium -> {
                navigationType = NavigationType.NAVIGATION_RAIL
            }

            WindowWidthSizeClass.Expanded -> {
                // TODO : Use Permanent Navigation Drawer for Fullscreen
                navigationType = NavigationType.NAVIGATION_RAIL
            }

            else -> {
                navigationType = NavigationType.BOTTOM_NAVIGATION
            }
        }

        Navigator(LibraryScreen(filter = null)) { navigator ->
            NavigationWrapper(
                navigationType = navigationType,
                navigator = navigator
            )
        }
    }
}


package de.hive.gamefinder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import de.hive.gamefinder.feature.library.LibraryScreen
import de.hive.gamefinder.feature.navigation.AppNavigationRail
import de.hive.gamefinder.feature.navigation.ModalNavigationDrawerContent
import de.hive.gamefinder.feature.navigation.NavigationRoutes
import de.hive.gamefinder.feature.navigation.PermanentNavigationDrawerContent
import de.hive.gamefinder.utils.NavigationType
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App() {
    KoinContext {
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
                navigationType = NavigationType.PERMANENT_NAVIGATION_DRAWER
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

@Composable
fun NavigationWrapper(
    navigationType: NavigationType,
    navigator: Navigator
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedRoute by remember { mutableStateOf(NavigationRoutes.LIBRARY) }

    if (navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentNavigationDrawerContent(
                    selectedRoute = selectedRoute,
                    onDrawerItemClicked = { selectedRoute = it.name; navigator.replaceAll(it.destination) }
                )
            },
        ) {
            AppContent(navigationType = navigationType, selectedRoute = selectedRoute)
        }
    } else {
        ModalNavigationDrawer(
            drawerContent = {
                ModalNavigationDrawerContent(
                    selectedRoute = selectedRoute,
                    onDrawerClicked = {
                        scope.launch { drawerState.close() }
                    },
                    onDrawerItemClicked = { selectedRoute = it.name }
                )
            },
            drawerState = drawerState
        ) {
            AppContent(navigationType = navigationType, selectedRoute = selectedRoute) {
                scope.launch { drawerState.open() }
            }
        }
    }
}

@Composable
fun AppContent(
    navigationType: NavigationType,
    selectedRoute: String,
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
            AppNavigationRail(
                selectedRoute = selectedRoute,
                onDrawerClicked = onDrawerClicked
            )
        }
        CurrentScreen()
    }
}
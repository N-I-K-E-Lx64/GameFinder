package de.hive.gamefinder.feature.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import cafe.adriel.voyager.core.screen.Screen
import de.hive.gamefinder.core.domain.Platform
import de.hive.gamefinder.feature.game_finder.GameFinderScreen
import de.hive.gamefinder.feature.library.LibraryScreen
import de.hive.gamefinder.utils.LayoutType
import io.github.aakira.napier.Napier

@Composable
fun AppNavigationRail(
    selectedRoute: String,
    onDrawerClicked: () -> Unit = {}
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        NavigationRailItem(
            modifier = Modifier.layoutId(LayoutType.HEADER),
            selected = false,
            onClick = onDrawerClicked,
            icon = {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        )

        Column(
           modifier = Modifier.layoutId(LayoutType.CONTENT),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SCREEN_NAVIGATION.forEach {
                NavigationRailItem(
                    selected = selectedRoute == it.name,
                    onClick = { println(it.name) },
                    icon = {
                        Icon(it.icon, contentDescription = null)
                    }
                )
            }
        }
    }
}



@Composable
fun PermanentNavigationDrawerContent(
    selectedRoute: String,
    onDrawerItemClicked: (NavigationElement) -> Unit
) {
    PermanentDrawerSheet(
        modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp),
            measurePolicy = navigationMeasurePolicy(),
            content = {
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .layoutId(LayoutType.HEADER),
                    text = "GAMEFINDER",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                NavigationDrawerContent(
                    selectedRoute = selectedRoute,
                    onDrawerItemClicked = onDrawerItemClicked
                )
            }
        )
    }
}

@Composable
fun ModalNavigationDrawerContent(
    selectedRoute: String,
    onDrawerClicked: () -> Unit = {},
    onDrawerItemClicked: (NavigationElement) -> Unit
) {
    ModalDrawerSheet {
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp),
            measurePolicy = navigationMeasurePolicy(),
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .layoutId(LayoutType.HEADER),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .layoutId(LayoutType.HEADER),
                        text = "GAMEFINDER",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDrawerClicked) {
                        Icon(Icons.Default.MenuOpen, contentDescription = null)
                    }
                }

                NavigationDrawerContent(
                    selectedRoute = selectedRoute,
                    onDrawerItemClicked = onDrawerItemClicked
                )
            }
        )
    }
}

@Composable
fun NavigationDrawerContent(
    selectedRoute: String,
    onDrawerItemClicked: (NavigationElement) -> Unit
) {
    Column(
        modifier = Modifier
            .layoutId(LayoutType.CONTENT)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SCREEN_NAVIGATION.forEach {
            NavigationDrawerItem(
                selectedRoute = selectedRoute,
                navElement = it,
                onDrawerItemClicked = onDrawerItemClicked
            )
        }

        Divider()

        PLATFORM_FILTERS.forEach {
            NavigationDrawerItem(
                selectedRoute = selectedRoute,
                navElement = it,
                onDrawerItemClicked = onDrawerItemClicked
            )
        }
    }
}

@Composable
fun NavigationDrawerItem(
    selectedRoute: String,
    navElement: NavigationElement,
    onDrawerItemClicked: (NavigationElement) -> Unit
) {
    NavigationDrawerItem(
        selected = selectedRoute == navElement.name,
        label = {
            Text(
                text = navElement.name,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        icon = {
            Icon(
                //painter = painterResource("${filter.filter}-logo.svg"),
                //painter = painterResource(MR.images.Steam),
                navElement.icon,
                contentDescription = null
            )
        },
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent
        ),
        onClick = { onDrawerItemClicked(navElement) }
    )
}


fun navigationMeasurePolicy(): MeasurePolicy {
    return MeasurePolicy { measurables, constraints ->
        lateinit var headerMeasurable: Measurable
        lateinit var contentMeasurable: Measurable
        measurables.forEach {
            when (it.layoutId) {
                LayoutType.HEADER -> headerMeasurable = it
                LayoutType.CONTENT -> contentMeasurable = it
                else -> Napier.e { "Unknown layoutId encountered!" }
            }
        }

        val headerPlaceable = headerMeasurable.measure(constraints)
        val contentPlaceable = contentMeasurable.measure(
            constraints.offset(vertical = -headerPlaceable.height)
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place the header, this goes at the top
            headerPlaceable.placeRelative(0, 0)

            // Determine how much space is not taken up by the content
            val nonContentVerticalSpace = constraints.maxHeight - contentPlaceable.height

            // And finally, make sure we don't overlap with the header.
            val contentPlaceableY = 0.coerceAtLeast(headerPlaceable.height)

            contentPlaceable.placeRelative(0, contentPlaceableY)
        }
    }
}

object NavigationRoutes {
    const val LIBRARY = "Library"
    const val GROUP = "Group Play (WIP)"
}

data class NavigationElement (
    val type: DrawerItemType,
    val name: String,
    val icon: ImageVector,
    val destination: Screen
)

enum class DrawerItemType {
    NAVIGATION,
    FILTER
}

val SCREEN_NAVIGATION: List<NavigationElement> = listOf(
    NavigationElement(DrawerItemType.NAVIGATION, NavigationRoutes.LIBRARY, Icons.Filled.VideoLibrary, LibraryScreen(filter = null)),
    NavigationElement(DrawerItemType.NAVIGATION, NavigationRoutes.GROUP, Icons.Filled.Groups, GameFinderScreen())
)
val PLATFORM_FILTERS: List<NavigationElement> = Platform.entries.map { platform ->
    NavigationElement(DrawerItemType.FILTER, platform.platform, Icons.Filled.VideogameAsset, LibraryScreen(filter = platform))
}
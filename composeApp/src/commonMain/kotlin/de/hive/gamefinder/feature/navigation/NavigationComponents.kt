package de.hive.gamefinder.feature.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import cafe.adriel.voyager.core.screen.Screen
import de.hive.gamefinder.components.LayoutType
import de.hive.gamefinder.feature.game_finder.GameFinderScreen
import de.hive.gamefinder.feature.library.LibraryScreen
import de.hive.gamefinder.feature.shortlist.ShortlistScreen
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationRail(
    screenModel: NavigationScreenModel = koinInject(),
    selectedRoute: String,
    onDrawerClicked: () -> Unit = {},
    onActionButtonClicked: () -> Unit = {},
    onDrawerItemClicked: (NavigationElement) -> Unit
) {
    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.inverseOnSurface
    ) {
        Column(
            modifier = Modifier.layoutId(LayoutType.HEADER),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NavigationRailItem(
                selected = false,
                onClick = onDrawerClicked,
                icon = {
                    Icon(Icons.Default.Menu, contentDescription = null)
                }
            )
            FloatingActionButton(
                onClick = onActionButtonClicked,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(
                    Icons.Filled.PersonAdd,
                    contentDescription = "Add Friend",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
           modifier = Modifier.layoutId(LayoutType.CONTENT),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SCREEN_NAVIGATION
                .filter { it.name != NavigationRoutes.SHORTLIST }
                .forEach {
                    NavigationRailItem(
                        selected = selectedRoute == it.name,
                        onClick = { onDrawerItemClicked(it) },
                        icon = {
                            Icon(it.icon, contentDescription = null)
                        }
                    )
                }

            NavigationRailItem(
                selected = selectedRoute == NavigationRoutes.SHORTLIST,
                onClick = { onDrawerItemClicked(SCREEN_NAVIGATION[2]) },
                icon = {
                    BadgedBox(
                        badge = {
                            Badge {
                                val badgeNumber = screenModel.shortlistBadge.value.toString()
                                Text(
                                    badgeNumber,
                                    modifier = Modifier.semantics { contentDescription = "$badgeNumber games on shortlist" }
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Filled.Bookmarks,
                            contentDescription = "Shortlist"
                        )
                    }
                }
            )
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
    onDrawerItemClicked: (NavigationElement) -> Unit = {},
    onActionButtonClicked: () -> Unit = {}
) {
    ModalDrawerSheet {
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp),
            measurePolicy = navigationMeasurePolicy(),
            content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                            Icon(Icons.AutoMirrored.Filled.MenuOpen, contentDescription = null)
                        }
                    }

                    ExtendedFloatingActionButton(
                        onClick = onActionButtonClicked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 40.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Friend",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Add Friend",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
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
            // val nonContentVerticalSpace = constraints.maxHeight - contentPlaceable.height

            // And finally, make sure we don't overlap with the header.
            val contentPlaceableY = 0.coerceAtLeast(headerPlaceable.height)

            contentPlaceable.placeRelative(0, contentPlaceableY)
        }
    }
}

object NavigationRoutes {
    const val LIBRARY = "Library"
    const val GROUP = "Group Play (Alpha)"
    const val SHORTLIST = "Shortlist"
}

data class NavigationElement (
    val type: DrawerItemType,
    val name: String,
    val icon: ImageVector,
    val destination: Screen
)

enum class DrawerItemType {
    NAVIGATION
}

val SCREEN_NAVIGATION: List<NavigationElement> = listOf(
    NavigationElement(DrawerItemType.NAVIGATION, NavigationRoutes.LIBRARY, Icons.Filled.VideoLibrary, LibraryScreen(filter = null)),
    NavigationElement(DrawerItemType.NAVIGATION, NavigationRoutes.GROUP, Icons.Filled.Pageview, GameFinderScreen()),
    NavigationElement(DrawerItemType.NAVIGATION, NavigationRoutes.SHORTLIST, Icons.Filled.Bookmarks, ShortlistScreen())
)
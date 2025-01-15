package de.hive.gamefinder.feature.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import de.hive.gamefinder.components.NavigationType
import gamefinder.composeapp.generated.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject


@Composable
fun NavigationWrapper(
    navigationType: NavigationType,
    navigator: Navigator,
    screenModel: NavigationScreenModel = koinInject()
) {
    val friendName by screenModel.friendName.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var selectedRoute by remember { mutableStateOf(NavigationRoutes.LIBRARY) }
    var openDialog by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    fun validate(name: String) {
        isError = screenModel.validateFriend(name)
    }

    LaunchedEffect(Unit) {
        // Load badge values
        screenModel.getBadgeValues()
    }

    // TODO : Update the selected route when we go back!

    if (navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentNavigationDrawerContent(
                    selectedRoute = selectedRoute,
                    onDrawerItemClicked = { selectedRoute = it.name; navigator.push(it.destination) }
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
                    onDrawerItemClicked = { selectedRoute = it.name; navigator.push(it.destination) },
                    onActionButtonClicked = { openDialog = true }
                )
            },
            drawerState = drawerState
        ) {
            AppContent(
                navigationType = navigationType,
                selectedRoute = selectedRoute,
                onActionButtonClicked = { openDialog = true },
                onDrawerItemClicked = { selectedRoute = it.name; navigator.push(it.destination) }
            ) {
                scope.launch { drawerState.open() }
            }
        }
    }

    if (openDialog) {
        AddFriendDialog(
            onDismissRequest = { openDialog = false },
            onDialogClosed = {
                openDialog = false
                screenModel.setFriendName("")
                             },
            onSave = {
                     // Check whether friend already exists
                     validate(friendName)

                if (!isError) {
                    screenModel.saveFriend()
                    openDialog = false
                    screenModel.setFriendName("")
                }
            },
            onUpdateName = { screenModel.setFriendName(it) },
            friendName = friendName,
            isError = isError
        )
    }
}

@Composable
fun AppContent(
    navigationType: NavigationType,
    selectedRoute: String,
    onActionButtonClicked: () -> Unit = {},
    onDrawerItemClicked: (NavigationElement) -> Unit = {},
    onDrawerClicked: () -> Unit = {}
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
            AppNavigationRail(
                selectedRoute = selectedRoute,
                onDrawerClicked = onDrawerClicked,
                onActionButtonClicked = onActionButtonClicked,
                onDrawerItemClicked = onDrawerItemClicked
            )
        }
        CurrentScreen()
    }
}

@Composable
private fun AddFriendDialog(
    onDismissRequest: () -> Unit,
    onDialogClosed: () -> Unit,
    onSave: () -> Unit,
    onUpdateName: (friendName: String) -> Unit,
    friendName: String,
    isError: Boolean
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.PersonAdd,
                    contentDescription = "Create friend dialog header icon"
                )

                Text(
                    stringResource(Res.string.friend_dialog_header),
                    style = MaterialTheme.typography.headlineSmall
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = friendName,
                    onValueChange = { onUpdateName(it) },
                    label = {
                        Text(text = "Friend name")
                    },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onSave()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    ),
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(Res.string.friend_dialog_error_text, friendName),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = { onDialogClosed() }) {
                        Text(stringResource(Res.string.const_cancel))
                    }

                    TextButton(onClick = { onSave() }) {
                        Text(stringResource(Res.string.const_save))
                    }
                }
            }
        }
    }
}
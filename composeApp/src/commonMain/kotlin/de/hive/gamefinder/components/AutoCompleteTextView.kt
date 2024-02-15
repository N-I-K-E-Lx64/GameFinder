package de.hive.gamefinder.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> AutoCompleteTextView(
    modifier: Modifier = Modifier,
    query: String,
    queryLabel: String,
    queryPlaceholder: String,
    onQueryChanged: (String) -> Unit = {},
    predictions: List<T>,
    onDoneAction: () -> Unit = {},
    onItemClick: (T) -> Unit = {},
    itemContent: @Composable (T) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier.heightIn(max = TextFieldDefaults.MinHeight * 4)
    ) {
        stickyHeader {
            Surface {
                QuerySearch(
                    query = query,
                    label = queryLabel,
                    placeholder = queryPlaceholder,
                    onDoneAction = onDoneAction,
                    onQueryChanged = onQueryChanged
                )
            }
        }

        if (predictions.isNotEmpty()) {
            items(predictions) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = { onItemClick(it) })
                ) {
                    itemContent(it)
                }
            }
        }
    }
}

@Composable
fun QuerySearch(
    modifier: Modifier = Modifier,
    query: String,
    label: String,
    placeholder: String,
    onDoneAction: () -> Unit = {},
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = { onDoneAction() }
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        )
    )
}
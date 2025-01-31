package cz.tlaskal.inventurapp.ui.item

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.TopAppBar
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import cz.tlaskal.inventurapp.R
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.ui.components.ScannerTextField
import cz.tlaskal.inventurapp.util.timestampToString


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItemScreen(
    viewModel: NewItemViewModel = viewModel(factory = NewItemViewModel.Factory),
    onNavigateBack: () -> Unit
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val idNotUniqueError = uiState.value.idNotUnique
    val errorMessage = uiState.value.error

    val id = remember { mutableStateOf("") }
    var name = remember { mutableStateOf("") }
    var description = remember { mutableStateOf("") }

    var showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())

    val nameSupportText = {
        if (uiState.value.nameIsBlank) {
            @Composable { Text(stringResource(R.string.name_is_blank)) }
        } else null
    }

    val focusRequester = FocusRequester()

    if (uiState.value.inserted) {
        onNavigateBack()
    }

    InventurAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    stringResource(R.string.new_item_screen_title),
                    showBack = true,
                    onBackClicked = onNavigateBack
                )
            }
        ) {
            Column(
                Modifier
                    .padding(
                        top = it
                            .calculateTopPadding()
                            .plus(40.dp)
                    )
                    .padding(horizontal = 50.dp)
            ) {
                val isError = idNotUniqueError && id.value.isNotBlank()
                val supportingText = if (isError) stringResource(R.string.id_not_unique) else null
                ScannerTextField(
                    text = id.value,
                    labelText = stringResource(R.string.new_item_code),
                    isError = isError,
                    supportingText = supportingText,
                    onValueChange = { id.value = it.text; viewModel.validateId(it.text) },
                )

                OutlinedTextField(
                    value = name.value,
                    label = @Composable { Text(stringResource(R.string.name)) },
                    onValueChange = {
                        name.value = it
                        viewModel.nameChanged(it)
                    },
                    isError = uiState.value.nameIsBlank,
                    supportingText = nameSupportText(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusTarget()
                        .focusRequester(focusRequester)
                        .focusable(uiState.value.nameIsBlank)
                )
                OutlinedTextField(
                    value = description.value,
                    label = @Composable { Text(stringResource(R.string.description)) },
                    onValueChange = { description.value = it },
                    minLines = 3,
                    maxLines = 7,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusTarget()
                )
                OutlinedTextField(
                    value = timestampToString(datePickerState.selectedDateMillis!!),
                    onValueChange = { },
                    label = @Composable { Text(stringResource(R.string.created_at)) },
                    readOnly = true,
                    trailingIcon = @Composable {
                        IconButton(onClick = { showDatePicker.value = !showDatePicker.value }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .focusTarget()
                )
                Spacer(Modifier.height(50.dp))
                Button(
                    onClick = {
                        viewModel
                            .createNewItem(
                                Item(
                                    id.value,
                                    name.value,
                                    description.value,
                                    datePickerState.selectedDateMillis!!,
                                    false
                                )
                            )
                        focusRequester.requestFocus()
                    },
                    enabled = !uiState.value.idNotUnique,
                    modifier = Modifier.fillMaxWidth(),
                    shape = OutlinedTextFieldDefaults.shape
                )
                {
                    Text(stringResource(R.string.create_item))
                }


                if (showDatePicker.value) {
                    DatePickerModal(
                        state = datePickerState,
                        onDateSelected = { datePickerState.selectedDateMillis = it },
                        onDismiss = { showDatePicker.value = false }
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    state: DatePickerState,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(state.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = state)
    }
}
package cz.tlaskal.inventurapp.ui.item

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.TopAppBar
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import cz.tlaskal.inventurapp.R
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.ui.components.ScannerTextView
import cz.tlaskal.inventurapp.ui.components.ScannerTextViewModel
import cz.tlaskal.inventurapp.util.timestampToString


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItemScreen(
    viewModel: NewItemViewModel = viewModel(factory = NewItemViewModel.Factory),
    onNavigateBack: () -> Unit
) {
    val scannerViewModel: ScannerTextViewModel = viewModel()
    val scannerUiState = scannerViewModel.uiState.collectAsStateWithLifecycle()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val idNotUniqueError = uiState.value.idNotUnique
    val errorMessage = uiState.value.error

    val id = scannerUiState.value.barcode
    var name = rememberSaveable { mutableStateOf("") }
    var description = remember { mutableStateOf("") }
    viewModel.checkId(scannerUiState)

    var showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())
    var selectedDate = datePickerState.selectedDateMillis!!

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
            },
            floatingActionButton = @Composable {
                var enabled = !idNotUniqueError && id.text.isNotBlank()
                if (enabled) {
                    FloatingActionButton(
                        onClick = {
                            viewModel
                                .createNewItem(
                                    Item(
                                        id.text,
                                        name.value,
                                        description.value,
                                        selectedDate,
                                        false
                                    )
                                )
                        },
                    ) {
                        Icon(Icons.Default.Check, stringResource(R.string.save))
                    }
                }
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
                val isError = idNotUniqueError
                val supportingText = if (isError) stringResource(R.string.id_not_unique) else null
                ScannerTextView(
                    viewModel = scannerViewModel,
                    labelText = stringResource(R.string.new_item_code),
                    isError = isError,
                    supportingText = supportingText
                )
                OutlinedTextField(
                    value = name.value,
                    label = @Composable { Text(stringResource(R.string.name)) },
                    onValueChange = { name.value = it },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description.value,
                    label = @Composable { Text(stringResource(R.string.description)) },
                    onValueChange = { description.value = it },
                    minLines = 3
                )
                OutlinedTextField(
                    value = timestampToString(selectedDate),
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
                )

                if (showDatePicker.value) {
                    DatePickerModal(
                        onDateSelected = { selectedDate = it!! },
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
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
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
        DatePicker(state = datePickerState)
    }
}
package cz.tlaskal.inventurapp.ui.item

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.R
import cz.tlaskal.inventurapp.TopAppBar
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.ui.components.ScannerTextView
import cz.tlaskal.inventurapp.ui.components.ScannerTextViewModel
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import cz.tlaskal.inventurapp.util.timestampToString


@Composable
fun ItemDetailScreen(id: String, onBackClicked: () -> Unit) {

    val viewModel: ItemDetailViewModel = viewModel(factory = ItemDetailViewModel.Factory(id))
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isEnabled: Boolean = uiState.value.editable
    val formData: Item? =
        if(uiState.value.editable){
            uiState.value.editedItemData
        } else {
            uiState.value.itemData
        }

    val scannerViewModel: ScannerTextViewModel = viewModel()
    val scannerUiState: State<ScannerTextViewModel.ScannerUiState> =
        scannerViewModel.uiState.collectAsStateWithLifecycle()

    val focusManager: FocusManager = LocalFocusManager.current

    BackHandler(enabled = uiState.value.editable) {
        viewModel.cancelEditClicked()
    }

    InventurAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = stringResource(R.string.item_detail),
                    showBack = true,
                    onBackClicked = { onBackClicked() },
                    onActionEditClicked = { viewModel.editClicked() },
                    barAction = uiState.value.appBarAction
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .padding(horizontal = 50.dp)
            ) {
                Text("Item no: " + uiState.value.itemData?.id)

                ScannerTextView(
                    viewModel = scannerViewModel,
                    labelText = stringResource(R.string.item_code),
                    enabled = isEnabled,
//                isError = isError,
//                supportingText = supportingText
                )
                OutlinedTextField(
                    modifier = Modifier.focusTarget(),
                    enabled = isEnabled,
                    value = uiState.value.itemData?.nazev.toString(),
                    label = @Composable { Text(stringResource(R.string.name)) },
                    onValueChange = { /**name.value = it**/ },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                OutlinedTextField(
                    modifier = Modifier.focusTarget(),
                    enabled = isEnabled,
                    value = uiState.value.itemData?.popis.toString(),
                    label = @Composable { Text(stringResource(R.string.description)) },
                    onValueChange = { /**description.value = it **/ },
                    minLines = 3,
                )
                OutlinedTextField(
                    enabled = isEnabled,
                    value = timestampToString(uiState.value.itemData?.vytvoreno ?: 0),
                    onValueChange = { },
                    label = @Composable { Text(stringResource(R.string.created_at)) },
                    readOnly = true,
                    trailingIcon = @Composable {
                        if (isEnabled) {
                            IconButton(onClick = {viewModel.showCreatedDatePicker() }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select date"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .focusTarget(),
                )
                Spacer(Modifier.height(50.dp))
                if (isEnabled) {
                    Row {
                        Button(
                            onClick = { viewModel.cancelEditClicked() },
                            colors = ButtonDefaults.buttonColors()
                                .copy(containerColor = Color.LightGray),
                            modifier = Modifier.weight(50f),
                            shape = OutlinedTextFieldDefaults.shape
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                        Spacer(Modifier.width(10.dp))
                        Button(
                            {
//                    viewModel
//                        .createNewItem(
//                            Item(
//                                id.text,
//                                name.value,
//                                description.value,
//                                selectedDate,
//                                false
//                            )
//                        )
                            },
                            modifier = Modifier.weight(50f),
                            shape = OutlinedTextFieldDefaults.shape
                        )
                        {
                            Text(stringResource(R.string.save))
                        }
                    }
                }


            if (uiState.value.showDatePicker) {
                DatePickerModal(
                    onDateSelected = { viewModel.createDatePicked(it!!)},
                    onDismiss = { viewModel.showCreatedDatePicker(false) }
                )
            }
            }
        }
    }
}
package cz.tlaskal.inventurapp.ui.item

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import cz.tlaskal.inventurapp.ui.components.ScannerTextField
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import cz.tlaskal.inventurapp.util.timestampToString


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ItemDetailScreen(
    id: String,
    onBackClicked: () -> Unit,
    viewModel: ItemDetailViewModel = viewModel(factory = ItemDetailViewModel.Factory(id))
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isEnabled: Boolean = uiState.value.editable
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())

    val focusManager: FocusManager = LocalFocusManager.current

    val nameSupportText = {
        if (uiState.value.nameIsBlank) {
            @Composable { Text(stringResource(R.string.name_is_blank)) }
        } else null
    }

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
                Modifier
                    .padding(
                        top = it
                            .calculateTopPadding()
                            .plus(40.dp)
                    )
                    .padding(horizontal = 50.dp)
            ) {
                if (uiState.value.isLoading) {
                    LinearProgressIndicator()
                }

                ScannerTextField(
                    labelText = stringResource(R.string.item_code),
                    readOnly = !isEnabled,
                    onValueChange = { viewModel.idChanged(it.text) },
                    text = uiState.value.itemData?.id,
                    isError = viewModel.uiState.value.idNotUnique,
                    supportingText = if (viewModel.uiState.value.idNotUnique) stringResource(R.string.id_not_unique) else null,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusTarget(),
                    readOnly = !isEnabled,
                    value = uiState.value.itemData?.nazev ?: "",
                    label = @Composable { Text(stringResource(R.string.name)) },
                    onValueChange = { viewModel.nameChanged(it) },
                    isError = uiState.value.nameIsBlank,
                    supportingText = nameSupportText(),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusTarget(),
                    readOnly = !isEnabled,
                    value = uiState.value.itemData?.popis ?: "",
                    label = @Composable { Text(stringResource(R.string.description)) },
                    onValueChange = { viewModel.descriptionChanged(it) },
                    minLines = 3,
                    maxLines = 7
                )
                OutlinedTextField(
                    value = timestampToString(uiState.value.itemData?.vytvoreno ?: 0),
                    onValueChange = { },
                    label = @Composable { Text(stringResource(R.string.created_at)) },
                    readOnly = true,
                    trailingIcon = @Composable {
                        if (isEnabled) {
                            IconButton(onClick = { viewModel.showCreatedDatePicker() }) {
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
                                val itemData = uiState.value.itemData!!
                                viewModel
                                    .updateItem(
                                        Item(
                                            itemData.id,
                                            itemData.nazev,
                                            itemData.popis,
                                            itemData.vytvoreno,
                                            itemData.zkontrolovano
                                        )
                                    )
                                onBackClicked()
                            },
                            modifier = Modifier.weight(50f),
                            shape = OutlinedTextFieldDefaults.shape,
                            enabled = !uiState.value.idNotUnique
                        )
                        {
                            Text(stringResource(R.string.save))
                        }
                    }
                }


                if (uiState.value.showDatePicker) {
                    DatePickerModal(
                        state = datePickerState,
                        onDateSelected = { viewModel.createdDateChanged(it ?: 0) },
                        onDismiss = { viewModel.showCreatedDatePicker(false) }
                    )
                }
            }
        }
    }

}
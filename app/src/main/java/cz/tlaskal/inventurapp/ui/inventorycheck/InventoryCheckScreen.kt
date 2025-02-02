package cz.tlaskal.inventurapp.ui.inventorycheck

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.AppBarActionState
import cz.tlaskal.inventurapp.R
import cz.tlaskal.inventurapp.TopAppBar
import cz.tlaskal.inventurapp.ui.components.Message
import cz.tlaskal.inventurapp.ui.components.ScannerTextField
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import cz.tlaskal.inventurapp.ui.inventorycheck.ItemCheckResults.CHECKED as CHECKED

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryCheckScreen(
    onBackClicked: () -> Unit,
    viewModel: InventoryCheckViewModel = viewModel(factory = InventoryCheckViewModel.Factory)
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val showResetDialog = remember { mutableStateOf(false) }
    val id = remember { mutableStateOf("") }

    val localHaptic = LocalHapticFeedback.current
    val hapticOnScanned = { localHaptic.performHapticFeedback(HapticFeedbackType.LongPress) }

    val topBarAction =
        if (uiState.value.inventoryCheckEnabled)
            AppBarActionState.CHECK
        else
            AppBarActionState.NONE

    InventurAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = stringResource(R.string.inventory_check),
                    showBack = true,
                    barAction = topBarAction,
                    onBackClicked = onBackClicked,
                    onClearClicked = { showResetDialog.value = true }
                )
            }
        ) {

            if (showResetDialog.value) {
                ConfirmResetInventoryCheck(
                    onDismiss = {
                        showResetDialog.value = false
                    }, onConfirm = {
                        viewModel.clearCheckedItems()
                        showResetDialog.value = false
                    }
                )
            }

            CheckMessage(uiState.value.checkResult, it) {
                viewModel.resetCheckResult()
            }

            Column(
                Modifier
                    .padding(top = it.calculateTopPadding())
                    .padding(horizontal = 50.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(150.dp))
                if (uiState.value.inventoryCheckEnabled) {
                    ScannerTextField(
                        text = id.value,
                        labelText = stringResource(R.string.id_to_check),
                        onValueChange = { id.value = it.text },
                        clearFocusOnDone = false,
                        onDone = {
                            viewModel.itemChecked(
                                id.value
                            )
                        },
                        onBarcodeScanned = { hapticOnScanned() })

                    Text(
                        stringResource(R.string.checked) + " ${uiState.value.checkedItemCount}/${uiState.value.itemCount}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 30.dp, bottom = 10.dp)
                    )
                    LinearProgressIndicator(
                        progress = { (1f / uiState.value.itemCount) * uiState.value.checkedItemCount },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        stringResource(R.string.no_inventory_check_started),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(50.dp))
                    Button({ viewModel.startInventoryCheck() }) { Text(stringResource(R.string.begin_item_check)) }
                }
            }
        }
    }
}

@Composable
fun ConfirmResetInventoryCheck(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val safetyCheckboxState = remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = @Composable {
            Button(
                onClick = onConfirm,
                enabled = safetyCheckboxState.value
            ) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = @Composable {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors()
                    .copy(containerColor = Color.LightGray)
            ) {
                Text(stringResource(R.string.no))
            }
        },
        title = @Composable { Text(text = stringResource(R.string.clear_inventory_check)) },
        text = @Preview
        @Composable {
            Column {
                Text(stringResource(R.string.confirm_clear_all_checked_items))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(R.string.delete_safety_checkbox),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Checkbox(
                        checked = safetyCheckboxState.value,
                        onCheckedChange = { safetyCheckboxState.value = it })
                }
            }
        }
    )
}

@Composable
fun CheckMessage(
    checkResult: ItemCheckResults?,
    paddingValues: PaddingValues,
    onDismiss: () -> Unit
) {
    if (checkResult != null) {
        var message: String =
            if (checkResult == CHECKED)
                stringResource(R.string.item_checked)
            else
                stringResource(R.string.item_not_checked)

        var background: Color =
            if (checkResult == CHECKED)
                MaterialTheme.colorScheme.tertiaryContainer
            else
                MaterialTheme.colorScheme.errorContainer

        Box(Modifier.padding(top = paddingValues.calculateTopPadding())) {
            Message(message, background) {
                onDismiss()
            }
        }
    }
}
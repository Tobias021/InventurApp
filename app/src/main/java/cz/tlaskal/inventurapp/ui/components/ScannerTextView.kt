package cz.tlaskal.inventurapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.R

@Composable
fun ScannerTextView(
    viewModel: ScannerTextViewModel = viewModel(),
    labelText: String? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    onValueChange: (TextFieldValue) -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.value.barcode) {
        onValueChange(uiState.value.barcode)
    }


    val supportingText: @Composable (() -> Unit)? = {
        if (supportingText != null) {
            Text(supportingText)
        }
    }

    if (uiState.value.showScanner) {
        BarcodeScanner {
            viewModel.barcodeChanged(
                TextFieldValue(
                    text = it.rawValue.toString(),
                    selection = TextRange(it.rawValue.toString().length)
                )
            )
        }
        viewModel.showScanner(false)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = uiState.value.barcode,
            onValueChange = { it: TextFieldValue ->
                viewModel.barcodeChanged(it)
            },
            modifier = Modifier
                .weight(7f)
                .fillMaxWidth()
                .focusTarget(),
            label = { Label(labelText) },
            trailingIcon = {
                TrailingIcon(viewModel)
            },
            singleLine = true,
            isError = isError,
            supportingText = supportingText,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
    }
}

@Composable
private fun Label(label: String?) {
    val label = if (label == null) {
        stringResource(R.string.barcodeTextFieldLabel)
    } else label
    Text(label)
}

@Composable
private fun TrailingIcon(viewModel: ScannerTextViewModel) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Row {
        if (uiState.value.barcode.text.isNotEmpty()) {

            IconButton(onClick = { viewModel.barcodeChanged(TextFieldValue("")) }) {
                Icon(Icons.Sharp.Clear, stringResource(R.string.clear))
            }
        }
        IconButton(onClick = { viewModel.showScanner() }) {
            Icon(
                painterResource(R.drawable.baseline_qr_code_scanner_24),
                stringResource(R.string.scan_button),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
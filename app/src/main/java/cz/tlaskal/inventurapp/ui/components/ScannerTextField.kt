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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import cz.tlaskal.inventurapp.R

@Composable
fun ScannerTextField(
    text: String?,
    labelText: String? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    onValueChange: (TextFieldValue) -> Unit = {},
    clearFocusOnDone: Boolean = true,
    onDone: () -> Unit = {},
    onBarcodeScanned: () -> Unit = {}
) {
    val showScannerState: MutableState<Boolean> = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val supportingText = {
        if (supportingText?.isNotBlank() == true) {
            @Composable { Text(supportingText) }
        } else {
            null
        }
    }

    if (showScannerState.value && enabled) {
        BarcodeScanner {
            val result = TextFieldValue(
                text = it.rawValue.toString(),
                selection = TextRange(it.rawValue.toString().length)
            )
            onValueChange(result)
            onBarcodeScanned()
            onDone()
        }
        showScannerState.value = false
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = TextFieldValue(text ?: "", TextRange(text?.count()?: 0)),
            onValueChange = { it: TextFieldValue ->
                onValueChange(it)
            },
            modifier = Modifier
                .weight(7f)
                .fillMaxWidth()
                .focusTarget(),
            label = { Label(labelText) },
            trailingIcon = {
                if (enabled && !readOnly) {
                    TrailingIcon(text, showScannerState, onValueChange)
                }
            },
            singleLine = true,
            isError = isError,
            supportingText = supportingText(),
            keyboardActions = KeyboardActions(
                onDone = {
                    if(clearFocusOnDone){
                        focusManager.clearFocus()
                    }
                    onDone()
                }
            ),
            enabled = enabled,
            readOnly = readOnly
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
private fun TrailingIcon(
    text: String?,
    showScannerState: MutableState<Boolean>,
    onValueChange: (TextFieldValue) -> Unit
) {

    Row {
        if (text?.isNotEmpty() == true) {
            IconButton(onClick = { onValueChange(TextFieldValue("")) }) {
                Icon(Icons.Sharp.Clear, stringResource(R.string.clear))
            }
        }
        IconButton(onClick = { showScannerState.value = true }) {
            Icon(
                painterResource(R.drawable.baseline_qr_code_scanner_24),
                stringResource(R.string.scan_button),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
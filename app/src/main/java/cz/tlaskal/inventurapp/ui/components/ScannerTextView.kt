package cz.tlaskal.inventurapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.R

@Composable
fun ScannerTextView(
    viewModel: ScannerTextViewModel = viewModel(),
    label: String? = null,
) {
    val uiState = viewModel.uiState.collectAsState()
    val label = if (label == null) {
        stringResource(R.string.barcodeTextFieldLabel)
    } else label

    if(uiState.value.showScanner){
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
            onValueChange = { it: TextFieldValue -> viewModel.barcodeChanged(it) },
            modifier = Modifier
                .weight(7f)
                .fillMaxWidth(),
            label = @Composable{ Text(label) },
            trailingIcon = @Composable {
                IconButton(onClick = { viewModel.showScanner() }) {
                    Icon(
                        painterResource(R.drawable.baseline_qr_code_scanner_24),
                        stringResource(R.string.scan_button),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            singleLine = true,
        )
    }
}
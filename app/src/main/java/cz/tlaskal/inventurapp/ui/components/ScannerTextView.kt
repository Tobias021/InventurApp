package cz.tlaskal.inventurapp.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.R

@Composable
fun ScannerTextView(viewModel:ScannerTextViewModel = viewModel(), context: Context, label: String? = null){
    val uiState = viewModel.uiState.collectAsState()
    val label = if (label == null) {stringResource(R.string.barcodeTextFieldLabel)} else label
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            modifier = Modifier.weight(7f).fillMaxWidth(),
            value = TextFieldValue (text = uiState.value.barcode , selection = TextRange(uiState.value.barcode.length)),
            onValueChange = { it -> viewModel.barcodeChanged(it.text)},
            label = { Text(label) }
        )
        Spacer(modifier = Modifier.weight(0.8f))
        Button(
            modifier = Modifier.sizeIn(maxWidth = 1.dp).weight(2.5f),
            onClick = { viewModel.scanBarcode(context) },
        ) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Scan button",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
}
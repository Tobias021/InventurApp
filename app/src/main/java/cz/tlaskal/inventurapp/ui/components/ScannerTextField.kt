package cz.tlaskal.inventurapp.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cz.tlaskal.inventurapp.R
import cz.tlaskal.inventurapp.ui.ScannerViewModel

@Composable
fun ScannerTextInput(viewModel: ScannerViewModel, context: Context, label: String? = null){
    val barcode = viewModel.barcode.value
    val label = if (label == null) {stringResource(R.string.barcodeTextFieldLabel)} else label
    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = TextFieldValue (text = barcode, selection = TextRange(barcode.length)),
            onValueChange = { it -> viewModel.barcodeChanged(it.text)},
            label = { Text(label) }
        )
        Button(
            onClick = { viewModel.scanBarcode(context) },
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Scan button",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }
    }
}
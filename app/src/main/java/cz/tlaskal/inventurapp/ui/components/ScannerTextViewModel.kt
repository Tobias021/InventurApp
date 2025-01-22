package cz.tlaskal.inventurapp.ui.components

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class ScannerTextViewModel: ViewModel() {
    
    data class ScannerUiState(
        val barcode: TextFieldValue = TextFieldValue(),
        val showScanned: Boolean = false,
        val showScanner: Boolean = false
    )
    
    val _uiState = MutableStateFlow(ScannerUiState())
    val uiState = _uiState.asStateFlow()

    fun showScanner(show: Boolean = true) {
        _uiState.update { it.copy(showScanner = show) }
    }

    fun barcodeChanged(barcode: TextFieldValue){
        _uiState.update{it.copy(barcode = barcode)}
        updateShowScanned(barcode.text)
        if(uiState.value.showScanner){
        _uiState.update{it.copy(showScanner  = false)}
        }
    }

    private fun updateShowScanned(barcode: String){
        var show = false
        if (barcode.isNotEmpty()) {
            show = true
        }
        _uiState.update{it.copy(showScanned = show)}

    }
}
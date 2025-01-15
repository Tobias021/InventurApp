package cz.tlaskal.inventurapp.ui.components

import android.content.Context
import androidx.lifecycle.ViewModel
import cz.tlaskal.inventurapp.util.BarcodeProcessor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet


class ScannerTextViewModel: ViewModel() {
    
    data class ScannerUiState(
        val barcode: String = "",
        val showScanned: Boolean = false,
    )
    
    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    fun scanBarcode(context: Context) {
        // Spustí skenování čárového kódu a uloží výsledek do _barcode
        val processor = BarcodeProcessor()
        processor.scan(context) {
            val newBarcode = it.rawValue.toString();
            barcodeChanged(newBarcode)
        }
    }
;
    fun barcodeChanged(barcode: String){
        _uiState.updateAndGet { it.copy(barcode = barcode) }
            .barcode.let {
                updateShowScanned(it)
            }
    }

    private fun updateShowScanned(barcode: String){
        if (barcode.isNotEmpty()) {
            _uiState.update { it.copy(showScanned = true) }
        }else{
            _uiState.update { it.copy(showScanned = false) }
        }
    }
}
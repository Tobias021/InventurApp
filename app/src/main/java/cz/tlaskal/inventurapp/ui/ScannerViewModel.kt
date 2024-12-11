package cz.tlaskal.inventurapp.ui

import android.R
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cz.tlaskal.inventurapp.barcodehandler.BarcodeProcessor

class ScannerViewModel: ViewModel() {
    private val _barcode = mutableStateOf("")
    val barcode: State<String> = _barcode

    fun scanBarcode(context: Context) {
        // Spusťte skenování čárového kódu a uložte výsledek do _barcode
        val processor = BarcodeProcessor()
        processor.scan(context) { _barcode.value = it.rawValue.toString() }
    }
;
    fun barcodeChanged(barcode: String){
        _barcode.value = barcode
    }
}
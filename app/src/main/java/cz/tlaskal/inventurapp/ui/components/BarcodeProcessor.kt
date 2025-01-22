package cz.tlaskal.inventurapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

@Composable
fun BarcodeScanner(onSuccessListener: OnSuccessListener<Barcode>) {
    val options = GmsBarcodeScannerOptions.Builder()
        .enableAutoZoom()
        .allowManualInput()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        )
        .build()

    val scanner = GmsBarcodeScanning.getClient(LocalContext.current, options)
    scanner.startScan()
        .addOnSuccessListener(onSuccessListener)

}
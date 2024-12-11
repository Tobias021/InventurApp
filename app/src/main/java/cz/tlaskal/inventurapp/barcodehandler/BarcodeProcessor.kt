package cz.tlaskal.inventurapp.barcodehandler

import android.content.Context
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class BarcodeProcessor {
    fun scan(context: Context, onSuccessListener: OnSuccessListener<Barcode>) {
        val options = GmsBarcodeScannerOptions.Builder()
            .enableAutoZoom()
            .allowManualInput()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()

        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener(onSuccessListener)
    }


}
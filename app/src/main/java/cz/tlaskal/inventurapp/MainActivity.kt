package cz.tlaskal.inventurapp

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import cz.tlaskal.inventurapp.barcodehandler.BarcodeProcessor
import cz.tlaskal.inventurapp.ui.ScannerViewModel
import cz.tlaskal.inventurapp.ui.components.ScannerTextInput
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this).get(ScannerViewModel::class)
        enableEdgeToEdge()
        setContent {
            InventurAppTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                ) { innerPadding ->
                    Column {
                        Greeting(
                                name = "Tobias",
                                modifier = Modifier.padding(innerPadding)
                        )
                        if (viewModel.barcode.value != "") {
                                Text("Naskenovaný kód: ${viewModel.barcode.value}")
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ScannerTextInput(viewModel = viewModel, this@MainActivity,"kodik objektu")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! Scan the code below.",
        modifier = modifier
    )
}


@Composable
fun GreetingPreview() {
    InventurAppTheme {
        Greeting("Android")
    }
}
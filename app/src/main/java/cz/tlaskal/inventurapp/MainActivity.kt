package cz.tlaskal.inventurapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.ui.components.ScannerTextViewModel
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InventurAppTheme {
                InventurApp()
            }
            //MainView(viewModel, this)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(viewModel: ScannerTextViewModel, context: Context){

}

@Preview
@Composable
fun MainViewPreview(viewModel: ScannerTextViewModel = viewModel()){
    MainView(viewModel, LocalContext.current)
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
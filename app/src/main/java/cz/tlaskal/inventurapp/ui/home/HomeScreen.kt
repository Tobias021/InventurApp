package cz.tlaskal.inventurapp.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.AppBarActionState
import cz.tlaskal.inventurapp.Greeting
import cz.tlaskal.inventurapp.TopAppBar
import cz.tlaskal.inventurapp.ui.nav.NavDestination
import cz.tlaskal.inventurapp.R
import cz.tlaskal.inventurapp.ui.components.ItemView
import cz.tlaskal.inventurapp.ui.components.ScannerTextView
import cz.tlaskal.inventurapp.ui.components.ScannerTextViewModel
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme

object HomeDestination : NavDestination {
    override val route: String
        get() = "home"
    override val titleResource: Int
        get() = R.string.home_title
}

@Preview
@Composable
fun HomeScreen(){
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val actionState = viewModel.actionState.collectAsStateWithLifecycle()
    val items = viewModel.items.collectAsStateWithLifecycle()
    val scannerViewModel: ScannerTextViewModel = viewModel()
    val scannerUiState = scannerViewModel.uiState.collectAsState()
    val context = LocalContext.current
    InventurAppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = stringResource(HomeDestination.titleResource),
                    barAction = actionState.value,
                    onActionSelectClicked = { viewModel.switchActionState(AppBarActionState.SELECT) },
                    onActionDeleteClicked = { viewModel.switchActionState(AppBarActionState.DEFAULT); Toast.makeText(context, "Deleted, hehe", Toast.LENGTH_SHORT).show()}
                )
            }

        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Greeting(
                    name = "Tobias",
                    modifier = Modifier.padding(innerPadding)
                )
                if (scannerUiState.value.showScanned) {
                    Text("Naskenovaný kód: ${scannerUiState.value.barcode}")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScannerTextView(viewModel = scannerViewModel, LocalContext.current,"kodik objektu")
                }

                LazyColumn() {
                    items(items = items.value, key = {it.id}){
                        ItemView(it)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}

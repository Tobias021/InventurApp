package cz.tlaskal.inventurapp.ui.home

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
import kotlin.time.Duration

object HomeDestination : NavDestination {
    override val route: String
        get() = "home"
    override val titleResource: Int
        get() = R.string.home_title
}

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val scannerViewModel: ScannerTextViewModel = viewModel()
    val scannerUiState = scannerViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    val showSnackbarDeleted: suspend (count: Int) -> SnackbarResult = {
        val stringResource: (Int) -> String = { context.resources.getString(it) }
        HomeViewModel.SnackbarHostState
            .showSnackbar(
                message = viewModel.getSnackDeletedMessage(
                    it,
                    SnackbarItemsDeletdStrings(
                        stringResource(R.string.item_deleted),
                        stringResource(R.string.few_items_deleted),
                        stringResource(R.string.many_items_deleted),
                        stringResource(R.string.no_items_deleted)
                    )
                ),
                actionLabel = stringResource(R.string.revert_deleted),
                withDismissAction = true
            )
    }

    InventurAppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = stringResource(HomeDestination.titleResource),
                    barAction = uiState.value.actionState,
                    onActionSelectClicked = { viewModel.switchActionState(AppBarActionState.SELECT) },
                    onActionCloseSelectClicked = { viewModel.seed() },
                    onActionDeleteClicked = {
                        viewModel.deleteSelectedItems()
                        { count ->
                            showSnackbarDeleted(count)
                        }
                    },
                    onActionDeleteHeld = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.showDeleteDialog()
                    }
                )
            },
            snackbarHost = { SnackbarHost(HomeViewModel.SnackbarHostState) }

        ) { innerPadding ->

            if (uiState.value.deleteDialogVisible) {
                val safetyCheckboxState = remember { mutableStateOf(false) }
                AlertDialog(
                    onDismissRequest = { viewModel.showDeleteDialog(false) },
                    confirmButton = @Composable {
                        Button(
                            onClick = {
                                viewModel.deleteAllItems(showSnackbarDeleted)
                                viewModel.switchActionState(AppBarActionState.DEFAULT)
                                viewModel.showDeleteDialog(false)
                            },
                            enabled = safetyCheckboxState.value
                        ) {
                            Text(stringResource(R.string.yes))
                        }
                    },
                    dismissButton = @Composable {
                        Button(
                            onClick = { viewModel.showDeleteDialog(false) },
                            colors = ButtonDefaults.buttonColors()
                                .copy(containerColor = Color.LightGray)
                        ) {
                            Text(stringResource(R.string.no))
                        }
                    },
                    title = @Composable { Text(text = stringResource(R.string.delete_all)) },
                    text = @Preview
                    @Composable {
                        Column {
                            Text(stringResource(R.string.confirm_delete_all_items))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.delete_safety_checkbox), textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 5.dp), fontWeight = FontWeight.Bold)
                                Checkbox(
                                    checked = safetyCheckboxState.value,
                                    onCheckedChange = { safetyCheckboxState.value = it })
                            }
                        }
                    }
                )
            }



            Column(modifier = Modifier.padding(innerPadding)) {

                if (uiState.value.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(10.dp, 50.dp)
                            .background(MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = uiState.value.error!!,
                            modifier = Modifier.padding(5.dp),
                            color = Color.Black
                        )
                    }
                }

                Greeting(
                    name = "Tobias",
                    modifier = Modifier.padding(innerPadding)
                )

                if (scannerUiState.value.showScanned) {
                    Text("Naskenovaný kód: ${scannerUiState.value.barcode}")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScannerTextView(viewModel = scannerViewModel, "kodik objektu")
                }

                LazyColumn() {
                    items(items = uiState.value.items, key = { it.id }) {
                        ItemView(
                            item = it,
                            isSelectable = uiState.value.isItemSelectable,
                            isSelected = viewModel.isItemSelected(it),
                            onClick = { viewModel.itemClicked(it) }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

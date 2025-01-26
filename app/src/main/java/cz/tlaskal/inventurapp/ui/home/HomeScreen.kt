package cz.tlaskal.inventurapp.ui.home

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.tlaskal.inventurapp.AppBarActionState
import cz.tlaskal.inventurapp.R
import cz.tlaskal.inventurapp.TopAppBar
import cz.tlaskal.inventurapp.ui.components.Error
import cz.tlaskal.inventurapp.ui.components.Item
import cz.tlaskal.inventurapp.ui.components.ScannerTextView
import cz.tlaskal.inventurapp.ui.components.ScannerTextViewModel
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import cz.tlaskal.inventurapp.util.drawFadingEdgesBasic

@Composable
fun HomeScreen(onAddItem: () -> Unit, onEditItem: (String) -> Unit) {
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val scannerViewModel: ScannerTextViewModel = viewModel()
    val scannerUiState = scannerViewModel.uiState.collectAsStateWithLifecycle()

    val hapticFeedback = LocalHapticFeedback.current
    val snackbarOnDeleted = snackbarOnDeleted(viewModel)

    BackHandler(scannerUiState.value.barcode.text.isNotEmpty()) {
        scannerViewModel.barcodeChanged(TextFieldValue())
    }

    InventurAppTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = stringResource(R.string.home_title),
                    barAction = uiState.value.actionState,
                    onActionSelectClicked = { viewModel.switchActionState(AppBarActionState.SELECT) },
                    onActionCloseSelectClicked = {
//                        viewModel.switchActionState(AppBarActionState.DEFAULT),
                        viewModel.seed()
                    },
                    onActionDeleteClicked = {
                        viewModel.deleteSelectedItems(snackbarOnDeleted)
                    },
                    onActionDeleteHeld = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.showDeleteDialog()
                    }
                )
            },
            floatingActionButton = @Composable {
                FloatingActionButton(onClick = onAddItem) {
                    Icon(Icons.Default.Add, stringResource(R.string.add_item))
                }

            },
            snackbarHost = { SnackbarHost(HomeViewModel.SnackbarHostState) }

        ) { innerPadding ->

            if (uiState.value.deleteDialogVisible) {
                ConfirmDeleteAllDialog(viewModel)
            }


            Column(modifier = Modifier.padding(innerPadding)) {

                Error(
                    message = uiState.value.error,
                    onClick = { viewModel.showError(null) }
                )


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScannerTextView(
                        viewModel = scannerViewModel,
                        labelText = "Filtr ID",
                        onValueChange = {
                            viewModel.onFilterChanged(it.text)
                        },
                    )
                }

                if(uiState.value.items.isNotEmpty()) {
                    LazyColumn{
                        items(items = uiState.value.items, key = { it.id }) {
                            Box(modifier = Modifier.animateItem()){
                                Item(
                                    item = it,
                                    isSelectable = uiState.value.isItemSelectable,
                                    isSelected = viewModel.isItemSelected(it),
                                    onClick = {
                                        viewModel.itemClicked(it)
                                    if(!uiState.value.isItemSelectable){
                                        onEditItem(it.id.toString())
                                    }                                    }
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(200.dp))
                        }

                    }
                }else{
                    NoItemsMessage { onAddItem() }
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteAllDialog(viewModel: HomeViewModel) {
    val safetyCheckboxState = remember { mutableStateOf(false) }
    val snackbarOnDeleted = snackbarOnDeleted(viewModel)
    AlertDialog(
        onDismissRequest = { viewModel.showDeleteDialog(false) },
        confirmButton = @Composable {
            Button(
                onClick = {
                    viewModel.deleteAllItems(snackbarOnDeleted)
                    viewModel.switchActionState(AppBarActionState.HOME)
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
                    Text(
                        stringResource(R.string.delete_safety_checkbox),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Checkbox(
                        checked = safetyCheckboxState.value,
                        onCheckedChange = { safetyCheckboxState.value = it })
                }
            }
        }
    )
}

@Composable
fun snackbarOnDeleted(viewModel: HomeViewModel): suspend (count: Int) -> SnackbarResult {
    val snackStrings = SnackbarItemsDeletdStrings(
        stringResource(R.string.item_deleted),
        stringResource(R.string.few_items_deleted),
        stringResource(R.string.many_items_deleted),
        stringResource(R.string.no_items_deleted)
    )
    val actionLabel = stringResource(R.string.revert_deleted)

    return { count: Int ->
        HomeViewModel.SnackbarHostState
            .showSnackbar(
                message = viewModel.getSnackDeletedMessage(
                    count,
                    snackStrings
                ),
                actionLabel = actionLabel,
                withDismissAction = true,
                duration = SnackbarDuration.Long
            )
    }
}

@Composable
fun NoItemsMessage(onAddItem: () -> Unit) {
    Column(modifier = Modifier.padding(top= 50.dp).padding(horizontal = 35.dp)) {
        Text(stringResource(R.string.no_items_message))
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(stringResource(R.string.you_can))
            Text(
                modifier = Modifier.clickable{
                    onAddItem()
                },
                text = stringResource(R.string.create_new),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}



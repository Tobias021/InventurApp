package cz.tlaskal.inventurapp.ui.inventorycheck

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.tlaskal.inventurapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryCheckScreen(onBack: () -> Unit) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.inventory_check))
                }
            )
        }
    ) {
        Column(Modifier.padding(top = it.calculateTopPadding())) {
            Spacer(Modifier.height(150.dp))
        }
    }
}
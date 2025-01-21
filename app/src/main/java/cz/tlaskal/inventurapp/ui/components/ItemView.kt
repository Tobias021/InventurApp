package cz.tlaskal.inventurapp.ui.components

import android.content.res.Configuration
import cz.tlaskal.inventurapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import cz.tlaskal.inventurapp.util.timestampToDate
import java.util.Date

@Composable
fun ItemView(item: Item, isSelectable: Boolean = false, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    Card(
        colors = CardDefaults
            .cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 60.dp,
                vertical = 5.dp
            )
            .heightIn(50.dp, 200.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Column {
                Text(item.nazev)
                Text(text = "popis: " + item.popis,
                    modifier = Modifier.padding(start = 20.dp))
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End) {
                if(isSelectable){
                    Checkbox(checked = isSelected, onCheckedChange = null)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
            Text(
                text = stringResource(R.string.created_at)+timestampToDate(item.vytvoreno).toLocaleString(),
                textAlign = TextAlign.Right,
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 15.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
fun ItemViewPreview() {
    InventurAppTheme {
    ItemView(Item("125455", "Nazvik", "Popis", Date.UTC(2022,2,11,8,20, 36), false), true)
    }
}
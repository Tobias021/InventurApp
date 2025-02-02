package cz.tlaskal.inventurapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import cz.tlaskal.inventurapp.R
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import cz.tlaskal.inventurapp.util.endOnLineBreak
import cz.tlaskal.inventurapp.util.limitLength
import cz.tlaskal.inventurapp.util.timestampToString
import java.util.Date

@Composable
fun Item(
    item: Item,
    isSelectable: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    val itemName = item.nazev.limitLength(20)
    val itemDescription = item.popis.limitLength(80).endOnLineBreak()
    val containerColor =
        if (item.zkontrolovano)
            MaterialTheme.colorScheme.tertiaryContainer
        else
            MaterialTheme.colorScheme.surfaceContainer

    Card(
        colors = CardDefaults
            .cardColors(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.secondary
            ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 40.dp,
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
                val titleStyle = MaterialTheme.typography.titleMedium
                Text(
                    text = itemName,
                    fontSize = titleStyle.fontSize,
                    fontStyle = titleStyle.fontStyle,
                    fontWeight = titleStyle.fontWeight,
                    fontFamily = titleStyle.fontFamily
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = itemDescription,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                if (isSelectable) {
                    Checkbox(checked = isSelected, onCheckedChange = null)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                text = stringResource(R.string.created_at) + timestampToString(item.vytvoreno),
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
        Item(Item("125455", "Nazvik", "Popis", Date.UTC(2022, 2, 11, 8, 20, 36), false), true)
    }
}
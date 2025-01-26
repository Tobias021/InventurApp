package cz.tlaskal.inventurapp.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme

/**
 * Simple Message panel showing a message when passed a nan-null string [message].
 *
 * @param message: Message to be shown.
 * @param onClick: Lambda to be called when the message is clicked.
 * @author Tobiáš Tláskal
 */

@Composable
fun Message(){

}


@Composable
fun Error(message: String?, onClick: () -> Unit){
    if (message != null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(10.dp, 50.dp)
                .background(MaterialTheme.colorScheme.error)
                .clickable { onClick.invoke() }

        ) {
            Text(
                text = message,
                modifier = Modifier.padding(5.dp),
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@PreviewLightDark
@Composable
fun ErrorPreview(){
    InventurAppTheme {
        Scaffold(Modifier.heightIn(30.dp, 60.dp)) {
            Error("Test") {}
        }
    }
}
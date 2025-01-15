package cz.tlaskal.inventurapp

import android.R.style.Theme
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cz.tlaskal.inventurapp.ui.nav.AppNavHost
import cz.tlaskal.inventurapp.ui.theme.InventurAppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun InventurApp(navController: NavHostController = rememberNavController()){
    AppNavHost(navController)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(title: String? = null, showBack: StateFlow<Boolean>? = null) {
    InventurAppTheme {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    title ?: stringResource(R.string.top_bar_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor =   MaterialTheme.colorScheme.primary,
                navigationIconContentColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.background,
                actionIconContentColor = MaterialTheme.colorScheme.background
            ),
            navigationIcon = {
                if (showBack != null)
                    AnimatedVisibility(
                        visible = showBack.collectAsState().value,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally()
                    ) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
            },
            modifier = Modifier
                .background(Color.LightGray)
                .shadow(12.dp),
        )
    }
}

@Preview
@Composable
fun TopAppBarPreview(){
    var showVisible = MutableStateFlow(true);
    Scaffold(topBar = {TopAppBar(showBack = showVisible.asStateFlow())}) {
        innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Button(onClick = {showVisible.update { !showVisible.value }}, ) {Text("Ukaž!") }
        }
    }
}
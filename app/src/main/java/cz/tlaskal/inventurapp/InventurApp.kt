package cz.tlaskal.inventurapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

enum class AppBarActionState{
    DEFAULT,
    SEARCH,
    SELECT,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(title: String? = null,
              showBack: Boolean? = null,
              barAction: AppBarActionState? = null,
              onBackClicked: (() -> Unit) = {},
              onActionSearchClicked: (() -> Unit) = {},
              onActionSelectClicked: (() -> Unit) = {},
              onActionCloseSelectClicked: (() -> Unit) = {},
              onActionDeleteClicked: (() -> Unit) ={}
) {
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
                        visible = showBack,
                        enter = expandHorizontally(),
                        exit = shrinkHorizontally()
                    ) {
                        IconButton(onClick = {onBackClicked}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
            },
            actions = {
                when(barAction){
                    AppBarActionState.SEARCH -> {
                        onActionSearchClicked
                    }

                    AppBarActionState.SELECT -> {
                        AppBarSelectAction(
                            onActionCloseSelectClicked,
                            onActionDeleteClicked
                        )
                    }
                    AppBarActionState.DEFAULT -> {
                        AppBarDefaultAction(
                            onActionSelectClicked,
                            onActionSearchClicked
                        )
                    }
                    null -> {}
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
    var showVisible = true;
    Scaffold(topBar = {TopAppBar(showBack = showVisible, barAction = AppBarActionState.DEFAULT)}) {
        innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Button(onClick = {showVisible = !showVisible}, ) {Text("Ukaž!") }
        }
    }
}


@Composable
fun AppBarDefaultAction(onActionSelectClicked: (() -> Unit), onActionSearchClicked: (() -> Unit)) {
    Row {
        IconButton(onClick = onActionSelectClicked ) {
            Icon(Icons.Filled.Edit, stringResource(R.string.select_items))
        }
        IconButton(onActionSearchClicked) {
            Icon(Icons.Filled.Search, stringResource(R.string.search_items))
        }
    }
}

@Composable
fun AppBarSelectAction(onActionCloseSelectClicked: (() -> Unit), onActionDeleteClicked: (() -> Unit)){
    Row {
        IconButton(onClick = onActionDeleteClicked) {
            Icon(Icons.Filled.Delete, stringResource(R.string.delete_selected))
        }
        IconButton(onClick = onActionCloseSelectClicked) {
            Icon(Icons.Filled.Close, stringResource(R.string.close))
        }
    }
}
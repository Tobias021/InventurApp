package cz.tlaskal.inventurapp.ui.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.tlaskal.inventurapp.ui.home.HomeScreen
import cz.tlaskal.inventurapp.ui.item.NewItemScreen
import kotlinx.serialization.Serializable


@Serializable
object Home

@Serializable
data class ItemDetail(val itemId: String)

@Serializable
object NewItem



@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier)
{
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier,
    ){
        composable<Home>{
            HomeScreen({navController.navigate(NewItem)})
        }
        composable<NewItem>{
            NewItemScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
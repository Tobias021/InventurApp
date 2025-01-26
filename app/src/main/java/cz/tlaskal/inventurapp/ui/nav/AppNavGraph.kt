package cz.tlaskal.inventurapp.ui.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import cz.tlaskal.inventurapp.ui.home.HomeScreen
import cz.tlaskal.inventurapp.ui.item.ItemDetailScreen
import cz.tlaskal.inventurapp.ui.item.ItemDetailUiState
import cz.tlaskal.inventurapp.ui.item.NewItemScreen
import kotlinx.serialization.Serializable


@Serializable
object Home

@Serializable
data class ItemDetail(val id: String)

@Serializable
object NewItem



@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier)
{
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier,
    ){
        composable<Home>{
            HomeScreen(
                onAddItem = {navController.navigate(NewItem)},
                onEditItem = {
                    navController.navigate(ItemDetail(it))
                }
            )
        }
        composable<NewItem>{
            NewItemScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<ItemDetail>{
            val itemDetail: ItemDetail = it.toRoute()
            ItemDetailScreen(id = itemDetail.id, onBackClicked = { navController.navigate(Home) })
        }
    }
}
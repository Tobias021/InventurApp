package cz.tlaskal.inventurapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import cz.tlaskal.inventurapp.ui.home.HomeScreen
import cz.tlaskal.inventurapp.ui.inventorycheck.InventoryCheckScreen
import cz.tlaskal.inventurapp.ui.item.ItemDetailScreen
import cz.tlaskal.inventurapp.ui.item.NewItemScreen
import kotlinx.serialization.Serializable


@Serializable
object Home

@Serializable
data class ItemDetail(val id: String)

@Serializable
object NewItem

@Serializable
object InventoryCheck


@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier,
    ) {
        composable<Home> {
            HomeScreen(
                onAddItem = { navController.navigate(NewItem) },
                onEditItem = { navController.navigate(ItemDetail(it)) },
                onCheckItems = { navController.navigate(InventoryCheck) }
            )
        }
        composable<NewItem> {
            NewItemScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<ItemDetail> {
            val itemDetail: ItemDetail = it.toRoute()
            ItemDetailScreen(id = itemDetail.id, onBackClicked = { navController.popBackStack() })
        }
        composable<InventoryCheck> {
            InventoryCheckScreen(onBackClicked = { navController.popBackStack() })
        }

    }
}
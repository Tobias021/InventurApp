package cz.tlaskal.inventurapp.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.tlaskal.inventurapp.ui.home.HomeDestination
import cz.tlaskal.inventurapp.ui.home.HomeScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier)
{
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ){
        composable(route = HomeDestination.route){
            HomeScreen()
        }
    }
}
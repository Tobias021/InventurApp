package cz.tlaskal.inventurapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cz.tlaskal.inventurapp.util.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val itemRepo = lazy { (application as InventurApplication).container.itemsRepository }
    val seeder = lazy { DatabaseSeeder(itemRepo.value) }


    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch{
            if(itemRepo.value.getAllItemsStream().count() < 1)
                seeder.value.seedDatabase()
        }
        enableEdgeToEdge()
        setContent {
                InventurApp()
        }
    }
}
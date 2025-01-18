package cz.tlaskal.inventurapp.util

import android.content.Context
import cz.tlaskal.inventurapp.data.InventoryDatabase
import cz.tlaskal.inventurapp.data.ItemsRepository
import cz.tlaskal.inventurapp.data.LocalItemsRepository

//manuální DI - AppContainer je dostupný ve všech třídách,
// musí být instancován v InventurApp Application třídě

interface AppContainer {
    val itemsRepository: ItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer{

    override val itemsRepository: ItemsRepository by lazy {
        LocalItemsRepository(InventoryDatabase.Companion.getDatabase(context).itemDao())
    }

}
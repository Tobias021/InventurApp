package cz.tlaskal.inventurapp.data

import android.content.Context

//manuální DI - AppContainer je dostupný ve všech třídách,
// musí být instancován v InventurApp Application třídě

interface AppContainer {
    val itemsRepository: ItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer{

    override val itemsRepository: ItemsRepository by lazy {
        LocalItemsRepository(InventoryDatabase.getDatabase(context).itemDao())
    }

}
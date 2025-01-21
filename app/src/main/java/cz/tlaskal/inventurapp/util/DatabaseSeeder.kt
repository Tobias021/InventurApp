package cz.tlaskal.inventurapp.util

import android.app.Activity
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import cz.tlaskal.inventurapp.InventurApplication
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.data.ItemsRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Date

class DatabaseSeeder(private val itemsRepository: ItemsRepository) {
    suspend fun seedDatabase(){
        itemsRepository.insertItem(Item("5", "Pocitac", "Hezky novy pocitac", Date.UTC(2022,2,11,8,20, 36), false))
        itemsRepository.insertItem(Item("6", "Penalek", "Hezky stary penal", Date.UTC(2018,5,20,8,20, 36), false))
        itemsRepository.insertItem(Item("7", "Penalek", "Hezky stary penal", Date.UTC(2018,5,20,8,20, 36), false))
        itemsRepository.insertItem(Item("8", "Penalek", "Hezky stary penal", Date.UTC(2018,5,20,8,20, 36), false))
        itemsRepository.insertItem(Item("9", "Penalek", "Hezky stary penal", Date.UTC(2018,5,20,8,20, 36), false))
        itemsRepository.insertItem(Item("10", "Penalek", "Hezky stary penal", Date.UTC(2018,5,20,8,20, 36), false))
        itemsRepository.insertItem(Item("11", "Penalek", "Hezky stary penal", Date.UTC(2018,5,20,8,20, 36), false))
    }

    suspend fun nukeItems(){
        itemsRepository.nukeItems()
    }
}
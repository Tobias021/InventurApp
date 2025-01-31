package cz.tlaskal.inventurapp.ui.inventorycheck

import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.tlaskal.inventurapp.InventurApplication
import cz.tlaskal.inventurapp.data.ItemsRepository
import kotlinx.coroutines.flow.Flow

data class InventoryCheckUiState(
    val itemCount: Flow<Int>,
    val inventoryCheckEnabled: Boolean = false,

)

class InventoryCheckViewModel(itemsRepository: ItemsRepository): ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory{
            initializer {
                val application = (this[APPLICATION_KEY] as InventurApplication)
                InventoryCheckViewModel(application.container.itemsRepository)
            }
        }
    }
}
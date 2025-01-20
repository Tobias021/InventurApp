package cz.tlaskal.inventurapp.ui.home

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.tlaskal.inventurapp.AppBarActionState
import cz.tlaskal.inventurapp.InventurApplication
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.data.ItemsRepository
import cz.tlaskal.inventurapp.data.LocalItemsRepository
import cz.tlaskal.inventurapp.util.DatabaseSeeder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val itemsRepository: ItemsRepository): ViewModel() {

    private val _items: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _actionState = MutableStateFlow(AppBarActionState.DEFAULT)
    val actionState: StateFlow<AppBarActionState> = _actionState.asStateFlow()

    companion object
    {
        val Factory: ViewModelProvider.Factory = viewModelFactory{
            initializer {
                val application = (this[APPLICATION_KEY] as InventurApplication)
                val itemsRepository = application.container.itemsRepository
                HomeViewModel(itemsRepository = itemsRepository)
            }
        }
    }

    init {
        viewModelScope.launch{
            itemsRepository.getAllItemsStream().collect {
                _items.value = it
            }
        }
    }

    fun switchActionState(state: AppBarActionState){
        _actionState.value = state
    }
}
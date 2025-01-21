package cz.tlaskal.inventurapp.ui.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.tlaskal.inventurapp.AppBarActionState
import cz.tlaskal.inventurapp.ERROR_VISIBILITY_DURATION
import cz.tlaskal.inventurapp.InventurApplication
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.data.ItemsRepository
import cz.tlaskal.inventurapp.util.DatabaseSeeder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer

data class HomeUiState(
    val items: List<Item> = emptyList(),
    val actionState: AppBarActionState = AppBarActionState.DEFAULT,
    val isItemSelectable: Boolean = false,
    val selectedItems: List<Item> = emptyList(),
    val error: String? = null,
    val deleteDialogVisible: Boolean = false
)

data class SnackbarItemsDeletdStrings(
    val one_deleted: String,
    val few_deleted: String,
    val many_deleted: String,
    val none_deleted: String

)

class HomeViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {

//    private val _items: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())
//    val items: StateFlow<List<Item>> = _items.asStateFlow()
//
//    private val _actionState = MutableStateFlow(AppBarActionState.DEFAULT)
//    val actionState: StateFlow<AppBarActionState> = _actionState.asStateFlow()

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private val deletedItemsCache: MutableList<Item> = mutableListOf()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as InventurApplication)
                val itemsRepository = application.container.itemsRepository
                HomeViewModel(itemsRepository = itemsRepository)
            }
        }
        val SnackbarHostState = SnackbarHostState()
    }

    init {
        viewModelScope.launch {
            itemsRepository.getAllItemsStream().collect {
                val items = it
                _uiState.update { it.copy(items = items) }
            }
        }
    }

    fun switchActionState(state: AppBarActionState) {
        _uiState.update { it.copy(actionState = state) }

        when (state) {
            AppBarActionState.SELECT -> _uiState.update { it.copy(isItemSelectable = true) }
            else -> {
                if (uiState.value.isItemSelectable) {
                    _uiState.update { it.copy(isItemSelectable = false) }
                }
            }
        }
    }

    fun itemClicked(item: Item) {
        if (uiState.value.isItemSelectable) {
            if (item in uiState.value.selectedItems) {
                _uiState.update { it.copy(selectedItems = uiState.value.selectedItems - item) }
            } else {
                _uiState.update { it.copy(selectedItems = uiState.value.selectedItems + item) }
            }
        }
    }

    fun isItemSelected(item: Item): Boolean {
        if (item in uiState.value.selectedItems) {
            return true
        }
        return false
    }

    fun deleteSelectedItems(showSnackDeleted: suspend (count: Int) -> SnackbarResult) {
        viewModelScope.launch {
            clearDeletedItemsCache()
            val selectedCount = uiState.value.selectedItems.count().toString()
            for (item in uiState.value.selectedItems) {
                deletedItemsCache.add(item)
                itemsRepository.deleteItem(item)
                _uiState.update { it.copy(selectedItems = it.selectedItems - item) }
            }
            showSnackDeleted(selectedCount.toInt()).also {
                if (it == SnackbarResult.ActionPerformed) {
                    revertLastDelete()
                }
            }
        }
        switchActionState(AppBarActionState.DEFAULT)
    }

    fun deleteAllItems(showSnackDeleted: suspend (count: Int) -> SnackbarResult){
        viewModelScope.launch(Dispatchers.IO){
            clearDeletedItemsCache()
            val count = uiState.value.items.count()
                deletedItemsCache.addAll(uiState.value.items)
            itemsRepository.nukeItems()
            showSnackDeleted(count).also{
                if (it == SnackbarResult.ActionPerformed){
                    revertLastDelete()
                }
            }
        }
    }

    fun seed() {
        viewModelScope.launch {
            try {
                DatabaseSeeder(itemsRepository).seedDatabase()
            } catch (exception: Exception) {
                showError("Chyba při seedování DB")
            }
        }
    }

    fun showDeleteDialog(show: Boolean = true){
        _uiState.update { it.copy(deleteDialogVisible = show) }
    }

    fun showError(mesg: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = mesg) }
            delay(ERROR_VISIBILITY_DURATION)
            _uiState.update { it.copy(error = null) }
        }
    }

    private fun clearDeletedItemsCache() {
        deletedItemsCache.removeAll(deletedItemsCache)
    }

    private fun revertLastDelete() {
        viewModelScope.launch {
            for (item in deletedItemsCache) {
                itemsRepository.insertItem(item)
            }
            deletedItemsCache.removeAll(deletedItemsCache)
        }
    }

    fun getSnackDeletedMessage(count: Int, strings: SnackbarItemsDeletdStrings): String {
        val countString = count.toString()

        return when (count) {
            0 -> strings.none_deleted
            1 -> countString + strings.one_deleted
            in 2..4 -> countString + strings.few_deleted
            else -> countString + strings.many_deleted
        }
    }
}
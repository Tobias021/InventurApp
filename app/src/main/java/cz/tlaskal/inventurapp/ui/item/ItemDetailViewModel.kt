package cz.tlaskal.inventurapp.ui.item

import android.text.Editable
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItemDetailUiState(
    val editable: Boolean = false,
    val isLoading: Boolean = true,
    val itemData: Item? = null,
    val editedItemData: Item? = null,
    val appBarAction: AppBarActionState = AppBarActionState.DETAIL,
    val showDatePicker: Boolean = false,
)

class ItemDetailViewModel(itemRepository: ItemsRepository, itemId: String) : ViewModel() {
    private val _uiState = MutableStateFlow<ItemDetailUiState>(ItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        val Factory: (itemId: String) -> ViewModelProvider.Factory = { itemId ->
            viewModelFactory {
                initializer {
                    val application = (this[APPLICATION_KEY] as InventurApplication)
                    val container = application.container
                    val itemRepository = container.itemsRepository
                    ItemDetailViewModel(itemRepository, itemId.toString())
                }
            }
        }
    }

    init {
        val itemFlow = itemRepository.getItemStream(itemId)
        registerItemCollector(itemFlow)
    }

    private fun registerItemCollector(itemFlow: Flow<Item?>) {
        viewModelScope.launch {
            itemFlow.collect {
                val item = it
                _uiState.update { it.copy(itemData = item) }
            }
        }.invokeOnCompletion {
            _uiState.update { it.copy(isLoading = false) }
        }

    }

    fun editClicked() {
        _uiState.update {
            it.copy(
                editable = true,
                appBarAction = AppBarActionState.NONE,
                editedItemData = uiState.value.itemData
            )
        }
    }

    fun cancelEditClicked() {

        _uiState.update { it.copy(editable = false, appBarAction = AppBarActionState.DETAIL) }
    }

    fun showCreatedDatePicker(show: Boolean = true) {
        _uiState.update { it.copy(showDatePicker = show) }
    }

    fun createDatePicked(timestamp: Long) {
        _uiState.update { it.copy(itemData = it.itemData?.copy(vytvoreno = timestamp)) }
    }
}
package cz.tlaskal.inventurapp.ui.item

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItemDetailUiState(
    val editable: Boolean = false,
    val isLoading: Boolean = true,
    val itemData: Item? = null,
    val idNotUnique: Boolean = false,
    val nameIsBlank: Boolean = false,
    val appBarAction: AppBarActionState = AppBarActionState.DETAIL,
    val showDatePicker: Boolean = false,
)

enum class ItemDetailFormFields() {
    ID,
    NAME,
    DESCRIPTION,
    CREATED_AT
}

class ItemDetailViewModel(val itemRepository: ItemsRepository, val itemId: String) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemDetailUiState>(ItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _editStates: MutableStateFlow<MutableMap<ItemDetailFormFields, Boolean>> =
        MutableStateFlow(mutableMapOf<ItemDetailFormFields, Boolean>())
    val editStates = _editStates.asStateFlow()

    private var registeredIds: List<String> = listOf()

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
        val idsFlow = itemRepository.getAllItemsIdsStream()
        registerItemCollector(itemFlow)
        registerIdsCollector(idsFlow)

        ItemDetailFormFields.entries.forEach {
            _editStates.value[it] = false
        }
    }

    private fun registerItemCollector(itemFlow: Flow<Item?>) {
        viewModelScope.launch {
            itemFlow.collect {
                val item = it
                _uiState.update { it.copy(itemData = item) }

                if (uiState.value.isLoading) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun registerIdsCollector(idsFlow: Flow<List<String>>) {
        viewModelScope.launch {
            idsFlow.collect {
                val ids = it
                registeredIds = ids.minus(itemId)
            }
        }
    }

    fun editClicked() {
        _uiState.update {
            it.copy(
                editable = true,
                appBarAction = AppBarActionState.NONE,
            )
        }
    }

    fun cancelEditClicked() {

        _uiState.update { it.copy(editable = false, appBarAction = AppBarActionState.DETAIL) }
    }

    fun showCreatedDatePicker(show: Boolean = true) {
        _uiState.update { it.copy(showDatePicker = show) }
    }

    fun idExists(id: String): Boolean {
        if (registeredIds.contains(id)) {
            return true
        } else {
            return false
        }
    }

    fun idChanged(id: String) {
        _uiState.update { it.copy(itemData = it.itemData?.copy(id = id)) }
        if (idExists(id) != uiState.value.idNotUnique) {
            _uiState.update { it.copy(idNotUnique = !uiState.value.idNotUnique) }
        }
    }

    fun nameChanged(name: String) {
        _uiState.update { it.copy(itemData = it.itemData?.copy(nazev = name)) }
        if (uiState.value.nameIsBlank && name.isNotBlank()) {
            _uiState.update { it.copy(nameIsBlank = false) }
        } else if (name.isBlank()) {
            _uiState.update { it.copy(nameIsBlank = true) }
        }
    }

    fun descriptionChanged(description: String) {
        _uiState.update { it.copy(itemData = it.itemData?.copy(popis = description)) }
    }

    fun createdDateChanged(created: Long) {
        _uiState.update { it.copy(itemData = it.itemData?.copy(vytvoreno = created)) }
    }

    fun updateItem(item: Item) {
        if (itemId == uiState.value.itemData?.id) {
            viewModelScope.launch {
                itemRepository.updateItem(item)
            }
        } else {
            viewModelScope.launch {
                itemRepository.getItemStream(itemId).collect {
                    if (it != null) {
                        itemRepository.deleteItem(it)
                        itemRepository.insertItem(item)
                    }
                    return@collect
                }
            }
        }
    }
}
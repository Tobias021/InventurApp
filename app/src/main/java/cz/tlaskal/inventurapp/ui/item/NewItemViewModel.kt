package cz.tlaskal.inventurapp.ui.item

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.tlaskal.inventurapp.InventurApplication
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.data.ItemsRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewItemUiState(
    val inserted: Boolean = false,
    val idNotUnique: Boolean = true,
    val nameIsBlank: Boolean = false,
    val error: String? = null
)

class NewItemViewModel(val itemsRepository: ItemsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(NewItemUiState())
    val uiState: StateFlow<NewItemUiState> = _uiState.asStateFlow()
    var registeredIds: List<String> = emptyList()
    private val idForValidation = MutableSharedFlow<String>()


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as InventurApplication)
                val repository = application.container.itemsRepository
                NewItemViewModel(repository)
            }
        }
    }

    init {
        registerItemIdsCollector()
        registerIdValidator()
    }

    private fun registerItemIdsCollector() {
        viewModelScope.launch {
            itemsRepository.getAllItemsIdsStream().collect {
                registeredIds = it
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun registerIdValidator() {
        viewModelScope.launch {
            idForValidation.collect {
                if (
                    it in registeredIds || it.isBlank()
                ) {
                    _uiState.update { it.copy(idNotUnique = true) }
                } else if (uiState.value.idNotUnique == true) {
                    _uiState.update { it.copy(idNotUnique = false) }
                }
            }
        }
    }

    fun createNewItem(item: Item) {
        if (item.nazev.isBlank()) {
            _uiState.update { it.copy(nameIsBlank = true) }
            return
        }
        if (!uiState.value.idNotUnique) {
            viewModelScope.launch {
                try {
                    itemsRepository.insertItem(item)
                    _uiState.update { it.copy(inserted = true) }
                } catch (exception: SQLiteException) {
                    _uiState.update { it.copy(error = exception.message) }
                }
            }
        }
    }

    fun validateId(id: String) {
        viewModelScope.launch {
            idForValidation.emit(id)
        }
    }

    fun canCreateNewItem(): Boolean {
        if (!uiState.value.idNotUnique) return true else return false
    }

    fun nameChanged(name: String) {
        if (uiState.value.nameIsBlank && name.isNotBlank()) {
            _uiState.update { it.copy(nameIsBlank = false) }
        } else if (name.isBlank()) {
            _uiState.update { it.copy(nameIsBlank = true) }
        }
    }
}
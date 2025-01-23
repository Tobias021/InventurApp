package cz.tlaskal.inventurapp.ui.item

import android.database.sqlite.SQLiteException
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.tlaskal.inventurapp.InventurApplication
import cz.tlaskal.inventurapp.data.Item
import cz.tlaskal.inventurapp.data.ItemsRepository
import cz.tlaskal.inventurapp.ui.components.ScannerTextViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewItemUiState(
    val inserted: Boolean = false,
    val idNotUnique: Boolean = false,
    val registeredIds: List<String> = emptyList(),
    val error: String? = null
)

class NewItemViewModel(val itemsRepository: ItemsRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(NewItemUiState())
    val uiState: StateFlow<NewItemUiState> = _uiState.asStateFlow()


    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as InventurApplication)
                val repository = application.container.itemsRepository
                NewItemViewModel(repository)
            }
        }
    }

    init {
        viewModelScope.launch {
            itemsRepository.getAllItemsIdsStream().collect {
                val registeredIdsList = it
                _uiState.update { it.copy(registeredIds = registeredIdsList) }
            }
        }
    }

    fun createNewItem(item: Item){
        if(!uiState.value.idNotUnique){
            viewModelScope.launch{
                try {
                    itemsRepository.insertItem(item)
                    _uiState.update { it.copy(inserted = true) }
                }catch (exception: SQLiteException){
                    _uiState.update { it.copy(error = exception.message) }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun checkId(scannerState: State<ScannerTextViewModel.ScannerUiState>){
        var scannerInputFlow = flowOf(scannerState)
        viewModelScope.launch{
            scannerInputFlow.debounce(500).collectLatest {
                if(it.value.barcode.text in uiState.value.registeredIds){
                    _uiState.update { it.copy(idNotUnique = true) }
                }else if (uiState.value.idNotUnique == true){
                    _uiState.update { it.copy(idNotUnique = false) }
                }
            }
        }
    }
}
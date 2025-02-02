package cz.tlaskal.inventurapp.ui.inventorycheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.tlaskal.inventurapp.InventurApplication
import cz.tlaskal.inventurapp.data.ItemsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class InventoryCheckUiState(
    val itemCount: Int = -1,
    val checkedItemCount: Int = -1,
    val inventoryCheckEnabled: Boolean = false,
    val checkResult: ItemCheckResults? = null
)

enum class ItemCheckResults {
    CHECKED,
    UNCHANGED
}

class InventoryCheckViewModel(val itemsRepository: ItemsRepository) : ViewModel() {

    private val _uiState: MutableStateFlow<InventoryCheckUiState> =
        MutableStateFlow(InventoryCheckUiState())
    val uiState = _uiState.asStateFlow()

    var checkResultJob: Job? = null

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as InventurApplication)
                InventoryCheckViewModel(application.container.itemsRepository)
            }
        }
    }

    init {
        registerItemCountCollector()
        registerCheckedItemCountCollector()
        runInitObserver()
    }

    private fun registerItemCountCollector() {
        viewModelScope.launch {
            itemsRepository.getItemsCount().collect {
                val itemCount = it
                _uiState.update { it.copy(itemCount = itemCount) }
            }
        }
    }

    private fun registerCheckedItemCountCollector() {
        viewModelScope.launch {
            itemsRepository.getCheckedItemsCount().collect {
                val checkedItemCount = it
                _uiState.update { it.copy(checkedItemCount = checkedItemCount) }
            }
        }
    }

    private fun runInitObserver() {
        viewModelScope.launch {
            while (uiState.value.checkedItemCount == -1 && uiState.value.itemCount == -1) {
                delay(5)
            }
            if (uiState.value.checkedItemCount > 0) {
                _uiState.update { it.copy(inventoryCheckEnabled = true) }
            }
        }
    }

    fun itemChecked(id: String) {
        viewModelScope.launch {
            val changed = itemsRepository.checkItem(id)

            var result = if (changed > 0) ItemCheckResults.CHECKED else ItemCheckResults.UNCHANGED
            resetCheckResult()
            checkResultJob = launch {
                _uiState.update { it.copy(checkResult = result) }
                delay(7000)
                _uiState.update { it.copy(checkResult = null) }
            }
        }
    }

    fun startInventoryCheck() {
        _uiState.update { it.copy(inventoryCheckEnabled = true) }
    }

    fun clearCheckedItems() {
        viewModelScope.launch {
            itemsRepository.uncheckAllItems()
            _uiState.update { it.copy(inventoryCheckEnabled = false) }
        }
    }

    fun resetCheckResult() {
        checkResultJob?.cancel(CancellationException("Dismissed"))
        _uiState.update { it.copy(checkResult = null) }
    }
}
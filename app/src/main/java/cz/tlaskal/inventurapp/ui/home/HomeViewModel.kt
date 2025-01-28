package cz.tlaskal.inventurapp.ui.home

import android.database.sqlite.SQLiteException
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import cz.tlaskal.inventurapp.util.DatabaseSeeder
import cz.tlaskal.inventurapp.util.ERROR_VISIBILITY_DURATION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

data class HomeUiState(
    val items: List<Item> = emptyList(),
    val actionState: AppBarActionState = AppBarActionState.HOME,
    val isItemSelectable: Boolean = false,
    val selectedItems: List<Item> = emptyList(),
    val error: String? = null,
    val deleteDialogVisible: Boolean = false,
    val isLoading: Boolean = true,
    val idFilter: String = "",
)

data class SnackbarItemsDeletdStrings(
    val one_deleted: String,
    val few_deleted: String,
    val many_deleted: String,
    val none_deleted: String

)

@OptIn(FlowPreview::class)
class HomeViewModel(private val itemsRepository: ItemsRepository) : ViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _filterText: MutableSharedFlow<String> = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val filterText = _filterText.asSharedFlow()

    private var itemProviderJob: Job? = null
        set(value) {
            itemProviderJob?.cancel(CancellationException("New item provider job launched"))
            field = value
        }

    private val deletedItemsCache: MutableList<Item> = mutableListOf()
    private var runningErrorJob: Job? = null


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
        itemProviderJob = provideAllItemsJob()
        registerItemFilter()
        registerFilterColletor()
    }

    private fun registerItemFilter(){
        viewModelScope.launch {
            filterText
                .debounce(700)
                .collectLatest {
                    if (it.isNotBlank()) {
                        itemProviderJob = provideSearchedItemsJob(it)
                    } else {
                        itemProviderJob = provideAllItemsJob()
                    }
                }
            if(uiState.value.isLoading){
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun registerFilterColletor(){
        viewModelScope.launch {
            filterText.collectLatest {
                val filterString = it
                _uiState.update { it.copy(idFilter = filterString) }
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
        return item in uiState.value.selectedItems
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
        switchActionState(AppBarActionState.HOME)
    }

    fun deleteAllItems(showSnackDeleted: suspend (count: Int) -> SnackbarResult) {
        viewModelScope.launch(Dispatchers.IO) {
            clearDeletedItemsCache()
            val count = uiState.value.items.count()
            deletedItemsCache.addAll(uiState.value.items)
            itemsRepository.nukeItems()
            showSnackDeleted(count).also {
                if (it == SnackbarResult.ActionPerformed) {
                    revertLastDelete()
                }
            }
        }
    }

    fun seed() {
        viewModelScope.launch {
            try {
                DatabaseSeeder(itemsRepository).seedDatabase()
            } catch (e: SQLiteException) {
                showError("Chyba při seedování DB: " + e.message)
            }
        }
    }

    fun showDeleteDialog(show: Boolean = true) {
        _uiState.update { it.copy(deleteDialogVisible = show) }
    }

    fun showError(mesg: String?) {
        runningErrorJob?.cancel(CancellationException("New error spawned"))
        runningErrorJob = viewModelScope.launch {
            if (mesg == null) {
                _uiState.update { it.copy(error = null) }
                return@launch
            }
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

    fun filterChanged(filter: String) {
        _filterText.tryEmit(filter)
    }

    private fun provideAllItemsJob(): Job {
        return viewModelScope.launch {
            itemsRepository.getAllItemsStream().collect {
                val items = it
                _uiState.update {
                    it.copy(items = items)
                }
            }
        }
    }

    private fun provideSearchedItemsJob(id: String): Job {
        return viewModelScope.launch {
            itemsRepository.searchItemById(id).collect {
                val itemsFiltered = it
                _uiState.update {
                    it.copy(items = itemsFiltered)
                }
            }
        }
    }
}

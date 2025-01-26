package cz.tlaskal.inventurapp.data

import kotlinx.coroutines.flow.Flow

interface ItemsRepository {

    fun getAllItemsStream(asc: Boolean = true): Flow<List<Item>>

    fun getAllItemsIdsStream(): Flow<List<String>>

    fun getActiveItems(): Flow<List<Item>>

    fun getInactiveItems(): Flow<List<Item>>

    suspend fun getItemsCount(): Int

    fun getItemStream(id: String): Flow<Item?>

    suspend fun insertItem(item: Item)

    suspend fun deleteItem(item: Item)

    suspend fun updateItem(item: Item)

    suspend fun nukeItems()

    fun searchItemById(id: String): Flow<List<Item>>

}
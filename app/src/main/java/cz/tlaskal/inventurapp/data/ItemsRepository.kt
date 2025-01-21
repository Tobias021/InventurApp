package cz.tlaskal.inventurapp.data

import kotlinx.coroutines.flow.Flow

interface ItemsRepository {

    fun getAllItemsStream(): Flow<List<Item>>

    fun getActiveItems(): Flow<List<Item>>

    fun getInactiveItems(): Flow<List<Item>>

    fun getItemStream(id: String): Flow<Item?>

    suspend fun insertItem(item: Item)

    suspend fun deleteItem(item: Item)

    suspend fun updateItem(item: Item)

    suspend fun nukeItems()

    suspend fun getItemsCount(): Int
}
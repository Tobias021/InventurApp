package cz.tlaskal.inventurapp.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class LocalItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<Item>> {
        return itemDao.getAllItems()
    }

    override fun getAllItemsIdsStream(): Flow<List<String>> {
        return itemDao.getAllItemsIds()
    }

    override fun getActiveItems(): Flow<List<Item>> {
        return itemDao.getActiveItems()
    }

    override fun getInactiveItems(): Flow<List<Item>> {
        return itemDao.getInactiveItems()
    }

    override fun getItemStream(id: String): Flow<Item?> {
        return itemDao.getItem(id)
    }

    override suspend fun insertItem(item: Item) {
        return itemDao.insert(item)
    }

    override suspend fun deleteItem(item: Item) {
        return itemDao.delete(item)
    }

    override suspend fun updateItem(item: Item) {
        return itemDao.update(item)
    }

    override suspend fun nukeItems(){
        return itemDao.nukeItems()
    }

    override fun searchItemById(id: String): Flow<List<Item>> {
        return itemDao.searchItemById(id)
    }

    override suspend fun getItemsCount(): Int {
        return itemDao.getCount()
    }


}
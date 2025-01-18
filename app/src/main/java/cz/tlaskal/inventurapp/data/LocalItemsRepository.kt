package cz.tlaskal.inventurapp.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class LocalItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<Item>> {
        return itemDao.getAllItems()
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

    override fun nukeItems(){
        return itemDao.nukeItems()
    }

    companion object {
            suspend fun LocalItemsRepository.seedDatabase() {
                insertItem(Item("1", "Pocitac", "Hezky novy pocitac",Date.UTC(2022,2,11,8,20, 36), false))
                insertItem(Item("2", "Penal", "Hezky stary penal", Date.UTC(2022,2,11,8,20, 36), false))
            }

            fun LocalItemsRepository.nukeDatabase() {
                    nukeItems()
            }
    }

}
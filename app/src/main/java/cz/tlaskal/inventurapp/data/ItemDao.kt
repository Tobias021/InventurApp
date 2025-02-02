package cz.tlaskal.inventurapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItem(id: String): Flow<Item>

    @Query("SELECT * FROM items WHERE id LIKE :id||'%'")
    fun searchItemById(id: String): Flow<List<Item>>

    @Query("SELECT * FROM items ORDER BY nazev ASC")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT id FROM items")
    fun getAllItemsIds(): Flow<List<String>>

    @Query("SELECT * FROM items WHERE zkontrolovano = FALSE")
    fun getActiveItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE zkontrolovano = TRUE")
    fun getInactiveItems(): Flow<List<Item>>

    @Query("DELETE FROM items ")
    fun nukeItems()

    @Query("SELECT count(*) FROM items")
    fun getCount(): Flow<Int>

    @Query("SELECT count(*) FROM items WHERE zkontrolovano = TRUE")
    fun getCheckedCount(): Flow<Int>

    @Query("UPDATE items SET zkontrolovano = true WHERE id = :id AND zkontrolovano = false")
    suspend fun checkItem(id: String): Int

    @Query("UPDATE items SET zkontrolovano = false WHERE zkontrolovano = TRUE")
    suspend fun uncheckAllItems()

}
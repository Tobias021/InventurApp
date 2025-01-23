package cz.tlaskal.inventurapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val nazev: String,
    val popis: String,
    val vytvoreno: Long,
    val zkontrolovano: Boolean = false
)
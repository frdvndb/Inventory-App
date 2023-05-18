package com.example.inventory.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Objek akses basis data untuk mengakses basis data Inventaris
 */
@Dao
interface ItemDao {
    @Query("SELECT * from item WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    @Query("SELECT * from item ORDER BY name ASC")
    fun getItems(): Flow<List<Item>>

    // Tentukan onConflict sebagai IGNORE, saat pengguna mencoba
    // menambahkan Item yang ada ke dalam database,
    // Room mengabaikan konflik.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
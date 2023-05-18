package com.example.inventory

import androidx.lifecycle.asLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

/**
 * ViewModel untuk menyimpan referensi ke repositori
 * inventaris dan daftar terbaru semua item.
 */
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    // Cache semua item dari database menggunakan LiveData.
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    /**
     * Mengembalikan nilai true jika EditTexts tidak kosong
     */
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Meluncurkan coroutine baru untuk menyisipkan
     * item dengan cara yang tidak menghalangi
     */
    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    /**
     * Mengembalikan instance kelas entitas Item dengan info item yang dimasukkan oleh pengguna.
     * Ini akan digunakan untuk menambahkan entri baru ke database Inventaris.
     */
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    /**
     * Tambahkan Item baru ke dalam database.
     */
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

    /**
     * Ambil item dari repositori.
     */
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    /**
     * Mengurangi satu unit stock dan memperbarui basis data.
     */
    fun sellItem(item: Item) {
        if (item.quantityInStock > 0) {
            // Decrease the quantity by 1
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    /**
     * Mengembalikan nilai benar jika stok tersedia
     * untuk dijual, salah jika sebaliknya.
     */
    fun isStockAvailable(item: Item): Boolean {
        return (item.quantityInStock > 0)
    }

    /**
     * Meluncurkan coroutine baru untuk menghapus
     * item dengan cara yang tidak memblokir
     */
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    /**
     * Meluncurkan coroutine baru untuk memperbarui
     * item dengan cara yang tidak memblokir
     */
    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    /**
     * Dipanggil untuk memperbarui entri yang ada di database Inventaris.
     * Mengembalikan instance kelas entitas Item dengan informasi item yang diperbarui oleh pengguna.
     */
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    /**
     * Memperbarui Item yang ada di database.
     */
    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }
}

/**
 * Kelas untuk membuat instance ViewModel.
 */
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
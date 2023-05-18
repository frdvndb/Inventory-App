package com.example.inventory

import android.app.Application
import com.example.inventory.data.ItemRoomDatabase

/**
 * Kelas yang mendefinisikan property database
 * ItemRoomDatabase dengan lazy sehingga database hanya
 * dibuat saat dibutuhkan daripada saat aplikasi dimulai
 */
class InventoryApplication : Application() {
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}
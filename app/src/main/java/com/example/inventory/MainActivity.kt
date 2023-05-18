package com.example.inventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController

/**
 * Aktivitas utama yang menghosting semua fragmen lain di aplikasi.
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    /**
     * mengambil NavController dari NavHostFragment dan
     * menyiapkan Action Bar untuk digunakan dengan NavController.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ambil NavController dari NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        // Siapkan Action Bar untuk digunakan dengan NavController
        setupActionBarWithNavController(this, navController)
    }

    /**
     * Tangani navigasi saat pengguna memilih dari Action Bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
package com.example.uaspppb1_b1.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspppb1_b1.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set default fragment (HomeFragment) ketika aplikasi pertama kali dibuka.
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment()) // Menampilkan HomeFragment pada container
            .commit()

        // Mengambil referensi BottomNavigationView dan menangani pemilihan item pada bottom navigation.
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item -> // Listener untuk item yang dipilih
            when (item.itemId) {
                // Ketika item Home dipilih, tampilkan HomeFragment
                R.id.nav_home -> {
                    replaceFragment(HomeFragment()) // Mengganti fragment dengan HomeFragment
                    true // Menandakan item berhasil dipilih
                }
                // Ketika item Borrow dipilih, tampilkan BorrowFragment
                R.id.nav_borrow -> {
                    replaceFragment(BorrowFragment()) // Mengganti fragment dengan BorrowFragment
                    true
                }
                // Ketika item Profile dipilih, tampilkan ProfileFragment
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment()) // Mengganti fragment dengan ProfileFragment
                    true
                }
                else -> false // Jika item yang dipilih tidak dikenali
            }
        }
    }

    // Fungsi untuk mengganti fragment yang sedang ditampilkan di container.
    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction() // Memulai transaksi fragment
            .replace(R.id.fragment_container, fragment) // Mengganti fragment di container
            .commit() // Menyelesaikan transaksi fragment
    }
}

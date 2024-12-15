package com.example.uaspppb1_b1.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uaspppb1_b1.R
import com.example.uaspppb1_b1.sharedpref.PrefManager

class ProfileFragment : Fragment() {

    private lateinit var prefManager: PrefManager // Deklarasi PrefManager untuk mengambil data dari shared preferences
    private lateinit var tvUserName: TextView // Deklarasi TextView untuk menampilkan nama pengguna
    private lateinit var tvUserRole: TextView // Deklarasi TextView untuk menampilkan role pengguna

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout untuk fragment ini
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inisialisasi PrefManager untuk mengakses shared preferences
        context?.let {
            prefManager = PrefManager.getInstance(it) // Mendapatkan instance PrefManager untuk mengakses data pengguna
        }

        // Menghubungkan variabel dengan elemen layout (TextView untuk nama pengguna)
        tvUserName = view.findViewById(R.id.tv_user_name) // Menemukan dan menghubungkan tvUserName dengan view di layout

        // Memuat data pengguna dari PrefManager
        loadUserData()

        return view // Mengembalikan view yang telah di-inflate untuk ditampilkan
    }

    private fun loadUserData() {
        // Mengambil data pengguna dari PrefManager
        val username = prefManager.getUsername() // Mendapatkan username dari PrefManager
        val role = prefManager.getRole() // Mendapatkan role dari PrefManager

        // Menampilkan data pengguna (username) pada TextView tvUserName
        tvUserName.text = "Email: $username" // Mengubah teks pada tvUserName untuk menampilkan nama pengguna
    }
}

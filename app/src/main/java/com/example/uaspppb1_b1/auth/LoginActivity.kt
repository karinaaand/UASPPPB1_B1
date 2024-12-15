package com.example.uaspppb1_b1.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspppb1_b1.admin.DashboardActivity
import com.example.uaspppb1_b1.databinding.ActivityLoginBinding
import com.example.uaspppb1_b1.sharedpref.PrefManager
import com.example.uaspppb1_b1.user.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menginisialisasi binding untuk layout dan memeriksa status login pengguna
        binding = ActivityLoginBinding.inflate(layoutInflater)

        val prefManager = PrefManager.getInstance(this)
        checkLoginStatus(prefManager) // Mengecek apakah pengguna sudah login atau belum

        setContentView(binding.root)

        // Ketika tombol login ditekan
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            // Memastikan bahwa username dan password tidak kosong
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
            } else {
                // Memeriksa apakah username dan password yang dimasukkan sesuai dengan data yang tersimpan
                val savedUsername = prefManager.getUsername()
                val savedPassword = prefManager.getPassword()

                if (username == savedUsername && password == savedPassword) {
                    // Mendapatkan role pengguna dari shared preferences
                    val role = prefManager.getRole()

                    // Arahkan pengguna sesuai dengan role yang dimilikinya
                    if (role == "Admin") {
                        prefManager.setLoggedIn(true) // Menandakan bahwa pengguna sudah login
                        Toast.makeText(this, "Login berhasil sebagai Admin", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, DashboardActivity::class.java)) // Arahkan ke DashboardActivity untuk Admin
                        finish() // Menutup activity login
                    } else if (role == "User") {
                        prefManager.setLoggedIn(true) // Menandakan bahwa pengguna sudah login
                        Toast.makeText(this, "Login berhasil sebagai User", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java)) // Arahkan ke MainActivity untuk User
                        finish() // Menutup activity login
                    } else {
                        Toast.makeText(this, "Role tidak valid", Toast.LENGTH_SHORT).show() // Jika role tidak valid
                    }
                } else {
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show() // Jika username atau password salah
                }
            }
        }

        // Ketika link registrasi diklik
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java)) // Arahkan pengguna ke activity registrasi
        }
    }

    // Fungsi untuk memeriksa apakah pengguna sudah login
    private fun checkLoginStatus(prefManager: PrefManager) {
        if (prefManager.isLoggedIn()) { // Jika pengguna sudah login
            val role = prefManager.getRole() // Mendapatkan role pengguna
            if (role == "Admin") {
                startActivity(Intent(this, DashboardActivity::class.java)) // Arahkan ke DashboardActivity jika Admin
                finish() // Menutup activity login
            } else if (role == "User") {
                startActivity(Intent(this, MainActivity::class.java)) // Arahkan ke MainActivity jika User
                finish() // Menutup activity login
            }
        }
    }
}

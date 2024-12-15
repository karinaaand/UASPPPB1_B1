package com.example.uaspppb1_b1.auth

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspppb1_b1.databinding.ActivityRegisterBinding
import com.example.uaspppb1_b1.sharedpref.PrefManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)  // Menghubungkan layout dengan binding
        setContentView(binding.root)

        val prefManager = PrefManager.getInstance(this)  // Mendapatkan instance PrefManager untuk menyimpan data

        // Ketika tombol register ditekan
        binding.btnRegister.setOnClickListener {
            val username = binding.etRegisterUsername.text.toString()  // Mengambil input username
            val password = binding.etRegisterPassword.text.toString()  // Mengambil input password
            val confirmPassword = binding.etConfirmPassword.text.toString()  // Mengambil input konfirmasi password

            // Mengambil pilihan role (Admin/User) dari RadioGroup
            val selectedRoleId = binding.rgRole.checkedRadioButtonId
            val role = if (selectedRoleId != -1) {
                findViewById<RadioButton>(selectedRoleId).text.toString()  // Menentukan role berdasarkan pilihan pengguna
            } else {
                null  // Jika tidak ada pilihan role
            }

            // Mengecek kondisi input yang tidak valid
            when {
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()  // Pesan jika ada field yang kosong
                }
                role == null -> {
                    Toast.makeText(this, "Pilih peran (Admin/User)", Toast.LENGTH_SHORT).show()  // Pesan jika role tidak dipilih
                }
                password != confirmPassword -> {
                    Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()  // Pesan jika password tidak sesuai dengan konfirmasi
                }
                else -> {
                    // Jika semua input valid, simpan data ke shared preferences
                    prefManager.saveUsername(username)  // Menyimpan username
                    prefManager.savePassword(password)  // Menyimpan password
                    prefManager.saveRole(role)  // Menyimpan role
                    prefManager.setLoggedIn(false)  // Menandakan pengguna belum login

                    Toast.makeText(this, "Registrasi berhasil sebagai $role", Toast.LENGTH_SHORT).show()  // Menampilkan pesan sukses registrasi

                    // Setelah registrasi berhasil, arahkan pengguna ke halaman login
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()  // Menutup halaman registrasi setelah berpindah ke halaman login
                }
            }
        }

        // Ketika link login diklik, arahkan ke halaman login
        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))  // Arahkan ke LoginActivity
        }
    }
}

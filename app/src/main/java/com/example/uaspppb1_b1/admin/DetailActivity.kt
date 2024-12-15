package com.example.uaspppb1_b1.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uaspppb1_b1.network.ApiClient
import com.example.uaspppb1_b1.network.ApiService
import com.example.uaspppb1_b1.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    // Inisialisasi variabel yang akan digunakan untuk menyimpan data motor
    private lateinit var apiService: ApiService
    private lateinit var motorId: String
    private lateinit var motorimage: String
    private lateinit var motorname: String
    private lateinit var motortype: String
    private lateinit var motorplat: String
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)  // Menghubungkan layout dengan binding
        setContentView(binding.root)  // Mengatur tampilan layar menggunakan binding

        // Menginisialisasi ApiClient untuk mendapatkan instance dari ApiService
        apiService = ApiClient.getInstance()

        // Mendapatkan data motor yang dikirim melalui Intent
        motorId = intent.getStringExtra("MOTOR_ID") ?: ""  // Menyimpan ID motor dari Intent
        motorimage = intent.getStringExtra("GAMBAR_MOTOR") ?: ""  // Menyimpan gambar motor dari Intent
        motorname = intent.getStringExtra("NAMA_MOTOR") ?: ""  // Menyimpan nama motor dari Intent
        motortype = intent.getStringExtra("TYPE_MOTOR") ?: ""  // Menyimpan tipe motor dari Intent
        motorplat = intent.getStringExtra("PLAT_MOTOR") ?: ""  // Menyimpan plat motor dari Intent

        // Menampilkan detail motor menggunakan fungsi displayMotorDetails
        displayMotorDetails(name = motorname, type = motortype, plat = motorplat, image = motorimage)
    }

    // Fungsi untuk menampilkan detail motor pada UI
    private fun displayMotorDetails(name: String, type: String, plat: String, image: String) {
        // Mengatur teks pada TextView untuk nama, tipe, dan plat motor
        binding.tvMotorName.text = name
        binding.tvMotorType.text = type
        binding.tvMotorPlate.text = plat

        // Memuat gambar motor menggunakan Glide (untuk menggantikan Picasso)
        Glide.with(this)
            .load(image)  // Memuat gambar dari URL yang diterima
            .into(binding.imgMotor)  // Menampilkan gambar pada ImageView
    }

    // Fungsi untuk menampilkan pesan error menggunakan Toast
    private fun showError(message: String) {
        // Menampilkan pesan kesalahan jika ada masalah
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

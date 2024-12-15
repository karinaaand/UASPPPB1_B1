package com.example.uaspppb1_b1.admin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.uaspppb1_b1.databinding.ActivityUpdateBinding
import com.example.uaspppb1_b1.network.ApiClient
import com.example.uaspppb1_b1.network.ApiService
import com.example.uaspppb1_b1.network.Motor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    private lateinit var client: ApiService
    private var motorId: String? = null
    private var URLImage: String? = null
    private lateinit var motorimage: String
    private lateinit var motorname: String
    private lateinit var motortype: String
    private lateinit var motorplat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)  // Menghubungkan layout dengan binding
        setContentView(binding.root)  // Mengatur tampilan menggunakan binding

        // Menerima data motor yang dikirimkan dari aktivitas sebelumnya
        motorId = intent.getStringExtra("MOTOR_ID") ?: ""  // Menyimpan ID motor yang diterima
        motorimage = intent.getStringExtra("GAMBAR_MOTOR") ?: ""  // Menyimpan URL gambar motor
        motorname = intent.getStringExtra("NAMA_MOTOR") ?: ""  // Menyimpan nama motor
        motortype = intent.getStringExtra("TYPE_MOTOR") ?: ""  // Menyimpan tipe motor
        motorplat = intent.getStringExtra("PLAT_MOTOR") ?: ""  // Menyimpan plat motor

        // Menampilkan detail motor yang diterima dari aktivitas sebelumnya pada form input
        displayMotorDetails(motorname, motortype, motorplat, motorimage)

        // Inisialisasi API Client untuk berkomunikasi dengan server
        client = ApiClient.getInstance()

        // Mengatur aksi ketika tombol Update Motor diklik
        binding.btnAddMotor.setOnClickListener { updateMotor() }
    }

    // Fungsi untuk memperbarui data motor ke server
    private fun updateMotor() {
        // Mengambil data yang dimasukkan pengguna melalui form input
        val image = binding.etImage.text.toString()
        val name = binding.etMotorName.text.toString()
        val type = binding.etMotorType.text.toString()
        val plat = binding.etMotorPlat.text.toString()

        // Mengecek apakah semua kolom sudah diisi dengan benar
        if (name.isEmpty() || type.isEmpty() || plat.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return  // Menghentikan proses jika ada kolom yang kosong
        }

        // Menyusun data motor dalam format JSON
        val motorStatus = true
        val json = """
        {
            "type": "$type",
            "name": "$name",
            "plat": "$plat",
            "image": "$image",
            "status": "$motorStatus"
        }
        """
        // Membuat RequestBody untuk mengirim data JSON ke API
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        // Mengirim permintaan untuk memperbarui data motor ke server
        client.updateMotor(motorId, requestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Menangani respons dari server
                if (response.isSuccessful) {
                    Toast.makeText(this@UpdateActivity, "Motor updated successfully", Toast.LENGTH_SHORT).show()
                    // Mengembalikan hasil sukses ke aktivitas sebelumnya
                    setResult(RESULT_OK, Intent())
                    finish()  // Menutup aktivitas ini
                } else {
                    Toast.makeText(this@UpdateActivity, "Failed to update motor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Menangani kegagalan jika permintaan gagal
                Toast.makeText(this@UpdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi untuk menampilkan detail motor ke dalam form input
    private fun displayMotorDetails(name: String, type: String, plat: String, image: String) {
        // Menampilkan data motor pada kolom input form
        binding.etMotorName.setText(name)
        binding.etMotorType.setText(type)
        binding.etMotorPlat.setText(plat)
        binding.etImage.setText(image)
    }

    // Fungsi untuk mendapatkan path asli dari URI gambar yang dipilih
    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath  // Mengembalikan path asli gambar
    }
}

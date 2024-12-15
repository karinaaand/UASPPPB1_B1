package com.example.uaspppb1_b1.admin

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspppb1_b1.databinding.ActivityCreateBinding
import com.example.uaspppb1_b1.network.ApiClient
import com.example.uaspppb1_b1.network.ApiService
import com.example.uaspppb1_b1.network.Motor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateActivity : AppCompatActivity() {

    // Inisialisasi variabel untuk menyimpan referensi ke API service dan binding layout
    private lateinit var apiService: ApiService
    private lateinit var binding: ActivityCreateBinding

    // Di kode yang di-comment ini, biasanya digunakan untuk memilih gambar dari galeri.
    // private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding layout activity ke dalam kode program
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi API service untuk melakukan request ke server
        apiService = ApiClient.getInstance()

        // Set onClick listener untuk tombol 'Add Motor'
        binding.btnAddMotor.setOnClickListener {
            // Memanggil fungsi addMotor ketika tombol ditekan
            addMotor()
        }
    }

    // Fungsi untuk menambahkan motor baru
    private fun addMotor() {
        // Mengambil input dari user untuk nama motor, tipe motor, plat motor, dan gambar motor
        val motorImage = binding.etImage.text.toString()
        val motorName = binding.etMotorName.text.toString()
        val motorType = binding.etMotorType.text.toString()
        val motorPlat = binding.etMotorPlat.text.toString()

        // Validasi bahwa semua field harus diisi
        if (motorName.isEmpty() || motorType.isEmpty() || motorPlat.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Menyiapkan status motor (misalnya motor aktif)
        val motorStatus = true

        // Membuat string JSON untuk data motor yang akan dikirim
        val json = """
        {
            "type": "$motorType",
            "name": "$motorName",
            "plat": "$motorPlat",
            "image": "$motorImage",
            "status": "$motorStatus"
        }
        """

        // Membuat RequestBody dari string JSON agar bisa dikirim ke server
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)

        // Mengirim request untuk menambahkan motor ke server
        apiService.createMotor(requestBody).enqueue(object : Callback<Void> {
            // Callback untuk menangani response dari server
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Menampilkan Toast jika motor berhasil ditambahkan
                    Toast.makeText(this@CreateActivity, "Motor added successfully", Toast.LENGTH_SHORT).show()
                    // Mengirim hasil ke activity sebelumnya
                    val resultIntent = Intent().apply{}
                    setResult(RESULT_OK, resultIntent)
                    finish() // Menutup activity
                } else {
                    // Menampilkan Toast jika gagal menambahkan motor
                    Toast.makeText(this@CreateActivity, "Failed to add motor", Toast.LENGTH_SHORT).show()
                }
            }

            // Callback untuk menangani kegagalan request
            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Menampilkan error jika request gagal
                Toast.makeText(this@CreateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi untuk mendapatkan path file dari URI gambar
    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var filePath: String? = null
        // Menentukan kolom yang diperlukan untuk mendapatkan data file
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        // Mengambil cursor untuk mencari data berdasarkan URI
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            // Jika cursor memiliki data, ambil path file
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        // Mengembalikan path file yang ditemukan
        return filePath
    }
}

package com.example.uaspppb1_b1.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspppb1_b1.auth.LoginActivity
import com.example.uaspppb1_b1.database.DB_Instance
import com.example.uaspppb1_b1.database.PeminjamanDao
import com.example.uaspppb1_b1.databinding.FragmentHomeBinding
import com.example.uaspppb1_b1.network.ApiClient
import com.example.uaspppb1_b1.network.ApiService
import com.example.uaspppb1_b1.network.Motor
import com.example.uaspppb1_b1.recyclerview.ListMotorAdapterUser
import com.example.uaspppb1_b1.sharedpref.PrefManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // Deklarasi variabel untuk Adapter, PrefManager, DB, ApiClient, dan ExecutorService
    private lateinit var adapterMotor: ListMotorAdapterUser
    private val listMotor = ArrayList<Motor>()
    private lateinit var prefManager: PrefManager
    private lateinit var binding: FragmentHomeBinding
    private lateinit var peminjamanDao: PeminjamanDao
    private lateinit var executorService: ExecutorService
    private lateinit var client: ApiService

    // Fungsi onCreateView untuk mengatur UI dan melakukan inisialisasi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        client = ApiClient.getInstance() // Menginisialisasi ApiClient
        binding = FragmentHomeBinding.inflate(inflater, container, false) // Mengikat layout dengan binding
        prefManager = PrefManager.getInstance(binding.root.context) // Inisialisasi PrefManager untuk manajemen sesi
        val db = DB_Instance.getDatabase(binding.root.context) // Menginisialisasi database lokal
        peminjamanDao = db!!.PeminjamanDao()!! // Mengakses DAO untuk akses ke data peminjaman motor

        // Inisialisasi adapter untuk RecyclerView dan menentukan tindakan ketika motor dipinjam atau dikembalikan
        adapterMotor = ListMotorAdapterUser(
            listMotor = listMotor,
            onClickBorrow = { peminjaman  ->
                updateMotor(peminjaman) // Mengupdate status motor ketika dipinjam
                if (peminjaman != null) {
                    insertData(peminjaman, binding.root.context) // Menambahkan data peminjaman ke database lokal
                }
                fetchData() // Memuat ulang data motor dari API
            },
            onClickFinish = { peminjaman ->
                updateMotor(peminjaman) // Mengupdate status motor ketika dikembalikan
                if (peminjaman != null) {
                    DeleteData(peminjaman, binding.root.context) // Menghapus data peminjaman dari database lokal
                }
            }
        )

        // Setup RecyclerView untuk menampilkan daftar motor
        binding.rvMotorList.apply {
            adapter = adapterMotor // Menghubungkan adapter dengan RecyclerView
            layoutManager = LinearLayoutManager(binding.root.context) // Mengatur layout menjadi Linear
            setHasFixedSize(true) // Menyatakan bahwa ukuran RecyclerView tetap
        }

        fetchData() // Memuat data motor dari API

        setupLogout() // Menyiapkan fungsi logout

        return binding.root // Mengembalikan root view
    }

    // Fungsi untuk memuat data motor dari API
    private fun fetchData() {
        client.getMotors().enqueue(object : Callback<List<Motor>> {
            override fun onResponse(call: Call<List<Motor>>, response: Response<List<Motor>>) {
                if (response.isSuccessful) {
                    // Jika berhasil, update list motor dengan data baru
                    response.body()?.let { motorList ->
                        val filteredList = motorList.filter { motor ->
                            motor.status // Filter motor yang aktif sesuai status
                        }
                        adapterMotor.updateListMotor(filteredList) // Update list motor pada adapter
                    }
                } else {
                    // Tampilkan error jika response tidak berhasil
                    Log.e("API Error", "Error fetching data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Motor>>, t: Throwable) {
                // Tampilkan error jika request gagal
                Log.e("API Error", "Failed to fetch data: ${t.message}")
            }
        })
    }

    // Fungsi untuk menambahkan data peminjaman motor ke database lokal
    fun insertData(peminjaman: Motor, context: Context) {
        executorService = Executors.newSingleThreadExecutor() // Membuat executor untuk thread terpisah
        val peminjamanbaru = Motor(
            id = peminjaman.id,
            name = peminjaman.name,
            plat = peminjaman.plat,
            type = peminjaman.type,
            image = peminjaman.image,
            status = !peminjaman.status // Mengubah status motor
        )
        executorService.execute {
            val isExists = peminjamanDao.isDataExists(peminjamanbaru.id) // Mengecek apakah data sudah ada
            if (!isExists) { // Jika belum ada, tambahkan data ke database
                peminjamanDao.insert(peminjamanbaru)
            }
        }
    }

    // Fungsi untuk menghapus data peminjaman motor dari database lokal
    fun DeleteData(peminjaman: Motor, context: Context) {
        executorService = Executors.newSingleThreadExecutor() // Membuat executor untuk thread terpisah
        val peminjamanbaru = Motor(
            id = peminjaman.id,
            name = peminjaman.name,
            plat = peminjaman.plat,
            type = peminjaman.type,
            image = peminjaman.image,
            status = !peminjaman.status // Mengubah status motor
        )
        executorService.execute {
            val isExists = peminjamanDao.isDataExists(peminjamanbaru.id) // Mengecek apakah data ada
            if (isExists) { // Jika ada, hapus data dari database
                peminjamanDao.delete(peminjamanbaru)
            }
        }
    }

    // Fungsi untuk menampilkan toast
    private fun showToast(context: Context, message: String) {
        // Menampilkan Toast di UI thread
        (context as Activity).runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mengupdate data motor di server API
    private fun updateMotor(peminjaman: Motor) {
        val json = """{
            "type": "${peminjaman.type}",
            "name": "${peminjaman.name}",
            "plat": "${peminjaman.plat}",
            "image": "${peminjaman.image}",
            "status": ${!peminjaman.status}
        }"""

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)
        client.updateMotor(peminjaman.id, requestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(binding.root.context, "Motor successfully updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context, "Failed to update motor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(binding.root.context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi logout untuk menghapus sesi dan kembali ke layar login
    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            prefManager.clear() // Menghapus data sesi
            val intent = Intent(binding.root.context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK // Menghapus stack aktivitas sebelumnya
            startActivity(intent) // Memulai aktivitas login
            Toast.makeText(binding.root.context, "Logout berhasil", Toast.LENGTH_SHORT).show() // Menampilkan toast logout berhasil
        }
    }
}

package com.example.uaspppb1_b1.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspppb1_b1.R
import com.example.uaspppb1_b1.database.DB_Instance
import com.example.uaspppb1_b1.database.PeminjamanDao
import com.example.uaspppb1_b1.databinding.FragmentBorrowBinding
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
 * Use the [BorrowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BorrowFragment : Fragment() {

    // Variabel-variabel untuk adapter, daftar motor, binding, database, dan executorService
    private lateinit var adapterMotor: ListMotorAdapterUser
    private val listMotor = ArrayList<Motor>()
    private lateinit var binding: FragmentBorrowBinding
    private lateinit var peminjamanDao: PeminjamanDao
    private lateinit var executorService: ExecutorService
    private lateinit var client: ApiService

    // onCreateView untuk menginisialisasi binding, mengambil referensi dari database, dan memanggil fungsi getallMotor untuk mendapatkan data motor
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        client = ApiClient.getInstance() // Mendapatkan instance ApiClient
        binding = FragmentBorrowBinding.inflate(inflater, container, false)
        val db = DB_Instance.getDatabase(binding.root.context) // Mengakses database
        peminjamanDao = db!!.PeminjamanDao()!! // Mendapatkan DAO untuk Peminjaman

        getallMotor() // Memanggil fungsi untuk mendapatkan daftar motor yang tersedia

        return (binding.root) // Mengembalikan tampilan fragment
    }

    // Fungsi untuk menyisipkan data motor ke dalam database jika motor belum ada
    fun insertData(peminjaman: Motor, context: Context) {
        executorService = Executors.newSingleThreadExecutor() // Menjalankan operasi di thread terpisah
        val peminjamanbaru = Motor(
            id = peminjaman.id,
            name = peminjaman.name,
            plat = peminjaman.plat,
            type = peminjaman.type,
            image = peminjaman.image,
            status = !peminjaman.status // Mengubah status motor
        )
        executorService.execute {
            val isExists = peminjamanDao.isDataExists(peminjamanbaru.id) // Mengecek apakah motor sudah ada
            if (!isExists) { // Jika motor belum ada, maka insert ke dalam database
                peminjamanDao.insert(peminjamanbaru)
            }
        }
    }

    // Fungsi untuk menghapus data motor dari database jika sudah ada
    fun DeleteData(peminjaman: Motor, context: Context) {
        executorService = Executors.newSingleThreadExecutor() // Menjalankan operasi di thread terpisah
        val peminjamanbaru = Motor(
            id = peminjaman.id,
            name = peminjaman.name,
            plat = peminjaman.plat,
            type = peminjaman.type,
            image = peminjaman.image,
            status = !peminjaman.status // Mengubah status motor
        )
        executorService.execute {
            val isExists = peminjamanDao.isDataExists(peminjamanbaru.id) // Mengecek apakah motor sudah ada
            if (isExists) { // Jika motor ada, maka delete dari database
                peminjamanDao.delete(peminjamanbaru)
            }
        }
    }

    // Fungsi untuk mendapatkan daftar motor dan menampilkan motor yang statusnya "tersedia"
    private fun getallMotor() {
        peminjamanDao.allPeminjaman.observe(requireActivity()) { listMotor ->
            val filteredList = listMotor.filter { motor -> motor.status == false } // Menyaring motor yang statusnya tidak tersedia
            val motorArrayList = ArrayList(filteredList) // Menyimpan motor yang difilter ke dalam list baru
            adapterMotor = ListMotorAdapterUser(
                listMotor = motorArrayList, // Menyediakan daftar motor untuk adapter
                onClickBorrow = { peminjaman -> // Menangani event klik untuk meminjam motor
                    updateMotor(peminjaman)
                    if (peminjaman != null) {
                        insertData(peminjaman, binding.root.context)
                    }
                },
                onClickFinish = { peminjaman -> // Menangani event klik untuk mengembalikan motor
                    updateMotor(peminjaman)
                    if (peminjaman != null) {
                        DeleteData(peminjaman, binding.root.context)
                    }
                }
            )
            // Mengatur RecyclerView untuk menampilkan daftar motor
            binding.rvMotorList.apply {
                adapter = adapterMotor
                layoutManager = LinearLayoutManager(binding.root.context)
                setHasFixedSize(true)
            }
        }
    }

    // Fungsi untuk memperbarui status motor ke API
    private fun updateMotor(peminjaman: Motor) {
        val json = """
        {
            "type": "${peminjaman.type}",
            "name": "${peminjaman.name}",
            "plat": "${peminjaman.plat}",
            "image": "${peminjaman.image}",
            "status": ${!peminjaman.status}
        }
        """ // Membuat JSON untuk mengirimkan data motor

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json) // Membuat RequestBody dari JSON
        client.updateMotor(peminjaman.id, requestBody).enqueue(object : Callback<Void> { // Memanggil API untuk memperbarui data motor
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(binding.root.context, "Motor successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context, "Failed to motor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(binding.root.context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

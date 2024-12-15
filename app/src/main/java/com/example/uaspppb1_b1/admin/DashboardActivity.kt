package com.example.uaspppb1_b1.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uaspppb1_b1.auth.LoginActivity
import com.example.uaspppb1_b1.databinding.ActivityDashboardBinding
import com.example.uaspppb1_b1.network.ApiClient
import com.example.uaspppb1_b1.network.ApiService
import com.example.uaspppb1_b1.network.Motor
import com.example.uaspppb1_b1.recyclerview.ListMotorAdapterAdmin
import com.example.uaspppb1_b1.sharedpref.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var client: ApiService
    private lateinit var adapterMotor: ListMotorAdapterAdmin
    private val listMotor = ArrayList<Motor>()
    private lateinit var prefManager: PrefManager
    private lateinit var createMotorLauncher: ActivityResultLauncher<Intent>
    private lateinit var updateMotorLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Registrasi launcher untuk Activity hasil create dan update motor
        // Untuk menangani hasil activity create motor
        createMotorLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    fetchData() // Memuat ulang data motor setelah aksi berhasil
                }
            }

        // Untuk menangani hasil activity update motor
        updateMotorLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    fetchData() // Memuat ulang data motor setelah update berhasil
                }
            }

        // Menginisialisasi PrefManager untuk pengelolaan data preferensi pengguna
        prefManager = PrefManager.getInstance(this)

        // Menyiapkan adapter untuk RecyclerView yang akan menampilkan daftar motor
        adapterMotor = ListMotorAdapterAdmin(
            listMotor = listMotor,
            onClickMotor = { motor ->
                // Arahkan pengguna ke DetailActivity untuk melihat detail motor
                val intent = Intent(this@DashboardActivity, DetailActivity::class.java).apply {
                    putExtra("MOTOR_ID", motor.id)
                    putExtra("GAMBAR_MOTOR", motor.image)
                    putExtra("NAMA_MOTOR", motor.name)
                    putExtra("TYPE_MOTOR", motor.type)
                    putExtra("PLAT_MOTOR", motor.plat)
                }
                startActivity(intent)
            },
            onClickEdit = { motor ->
                // Arahkan pengguna ke UpdateActivity untuk memperbarui data motor
                val intent = Intent(this@DashboardActivity, UpdateActivity::class.java).apply {
                    putExtra("MOTOR_ID", motor.id)
                    putExtra("GAMBAR_MOTOR", motor.image)
                    putExtra("NAMA_MOTOR", motor.name)
                    putExtra("TYPE_MOTOR", motor.type)
                    putExtra("PLAT_MOTOR", motor.plat)
                }
                updateMotorLauncher.launch(intent) // Meluncurkan activity untuk update motor
            }
        )

        // Setup UI dengan tombol dan RecyclerView
        setupUI()

        // Memuat data motor dari API saat activity dimulai
        fetchData()
    }

    private fun setupUI() {
        with(binding) {
            // Tombol untuk menambahkan motor baru, meluncurkan activity CreateActivity
            btnAddMotor.setOnClickListener {
                val intent = Intent(this@DashboardActivity, CreateActivity::class.java)
                createMotorLauncher.launch(intent) // Meluncurkan activity untuk menambahkan motor baru
            }

            // Tombol untuk logout, menghapus data login dari SharedPreferences dan mengarahkan ke LoginActivity
            btnLogout.setOnClickListener {
                prefManager.clear() // Hapus data preferensi login
                val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
                startActivity(intent) // Arahkan ke LoginActivity
                finish() // Tutup DashboardActivity
            }

            // Menyiapkan RecyclerView untuk menampilkan daftar motor
            rvMotorList.apply {
                adapter = adapterMotor
                layoutManager = LinearLayoutManager(this@DashboardActivity) // Mengatur layout linear
                setHasFixedSize(true) // Optimalkan performa RecyclerView
            }
        }
    }

    private fun fetchData() {
        // Menginisialisasi klien API untuk melakukan request data
        client = ApiClient.getInstance()

        // Memanggil endpoint getMotors untuk mendapatkan daftar motor dari server
        client.getMotors().enqueue(object : Callback<List<Motor>> {
            override fun onResponse(call: Call<List<Motor>>, response: Response<List<Motor>>) {
                if (response.isSuccessful) {
                    // Jika data berhasil diterima, update adapter dengan daftar motor yang baru
                    response.body()?.let { motorList ->
                        adapterMotor.updateListMotor(motorList)
                    }
                } else {
                    // Tampilkan error jika request gagal
                    showError("Error fetching data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Motor>>, t: Throwable) {
                // Tampilkan error jika ada kegagalan dalam request
                showError("Failed to fetch data: ${t.message}")
            }
        })
    }

    private fun showError(message: String) {
        // Menampilkan pesan error menggunakan Toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

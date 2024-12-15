package com.example.uaspppb1_b1.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.example.uaspppb1_b1.admin.CreateActivity
import com.example.uaspppb1_b1.admin.DetailActivity
import com.example.uaspppb1_b1.network.Motor
import com.example.uaspppb1_b1.databinding.ItemMotorBinding
import com.example.uaspppb1_b1.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Type alias untuk mendefinisikan fungsi klik pada item
typealias OnClickMotor = (Motor) -> Unit
typealias OnClickEdit = (Motor) -> Unit

// Adapter untuk menampilkan daftar objek Motor dalam RecyclerView
class ListMotorAdapterAdmin(

    // Menyimpan data motor yang akan ditampilkan dan callback fungsi untuk menangani klik item dan edit
    private val listMotor: ArrayList<Motor>,
    private val onClickMotor: OnClickMotor,
    private val onClickEdit: OnClickEdit

) : RecyclerView.Adapter<ListMotorAdapterAdmin.ItemMotorViewHolder>() {

    // ViewHolder untuk setiap item motor yang ditampilkan dalam RecyclerView
    inner class ItemMotorViewHolder(private val binding: ItemMotorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Fungsi untuk mengikat data motor ke tampilan dalam ViewHolder
        fun bind(data: Motor) {
            with(binding) {
                // Menampilkan nama dan plat nomor motor di TextView
                tvMotorName.text = data.name
                tvMotorPlat.text = data.plat

                // Mengatur aksi saat tombol edit ditekan
                btnEditMotor.setOnClickListener {
                    onClickEdit(data)  // Menjalankan aksi edit motor
                }

                // Mengatur aksi saat tombol delete ditekan
                btnDeleteMotor.setOnClickListener {
                    deleteMotor(id = data.id, itemView, adapterPosition)  // Memanggil fungsi delete motor
                }

                // Mengatur aksi saat item motor di klik
                itemView.setOnClickListener {
                    onClickMotor(data)  // Memanggil fungsi callback untuk menampilkan detail motor
                }
            }
        }
    }

    // Fungsi untuk membuat ViewHolder baru saat diperlukan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMotorViewHolder {
        // Menggunakan binding untuk meng-inflate layout item_motor.xml sebagai item tampilan
        val binding = ItemMotorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemMotorViewHolder(binding)
    }

    // Mendapatkan ukuran daftar data untuk mengetahui jumlah item dalam RecyclerView
    override fun getItemCount(): Int {
        return listMotor.size
    }

    // Mengikat data motor pada posisi tertentu ke ViewHolder
    override fun onBindViewHolder(holder: ItemMotorViewHolder, position: Int) {
        holder.bind(listMotor[position])
    }

    // Fungsi untuk memperbarui daftar motor di adapter
    fun updateListMotor(newList: List<Motor>) {
        listMotor.clear()  // Hapus semua data yang ada
        listMotor.addAll(newList)  // Menambahkan data baru
        notifyDataSetChanged()  // Memberitahukan adapter bahwa data telah diperbarui
    }

    // Fungsi untuk menghapus motor dari daftar berdasarkan posisi
    fun removeMotor(position: Int) {
        if (position >= 0 && position < listMotor.size) {
            listMotor.removeAt(position)  // Menghapus item dari daftar
            notifyItemRemoved(position)  // Memberitahukan adapter bahwa item telah dihapus
            notifyItemRangeChanged(position, listMotor.size)  // Mengupdate item setelah penghapusan
        }
    }

    // Fungsi untuk menghapus motor dari server menggunakan API
    private fun deleteMotor(id: String?, itemView: View, position: Int) {
        val ApiService = ApiClient.getInstance()

        // Memanggil API untuk menghapus motor berdasarkan ID
        ApiService.deleteMotor(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Menampilkan pesan sukses jika penghapusan berhasil
                    Toast.makeText(
                        itemView.context,
                        "Motor deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    removeMotor(position)  // Menghapus motor dari tampilan RecyclerView
                } else {
                    // Menampilkan pesan error jika penghapusan gagal
                    Toast.makeText(
                        itemView.context,
                        "Failed to delete motor: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Menangani kesalahan jaringan atau kesalahan lainnya
                Toast.makeText(itemView.context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

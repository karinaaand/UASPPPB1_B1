package com.example.uaspppb1_b1.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uaspppb1_b1.databinding.ItemBorrowBinding
import com.example.uaspppb1_b1.network.Motor

// Type alias untuk mendefinisikan fungsi klik pada item
typealias OnClickBorrow = (Motor) -> Unit
typealias OnClickFinish = (Motor) -> Unit

// Adapter untuk menampilkan daftar objek Motor dalam RecyclerView untuk User
class ListMotorAdapterUser(
    private val listMotor: ArrayList<Motor>,  // Daftar motor yang akan ditampilkan
    private val onClickBorrow: OnClickBorrow, // Fungsi callback saat motor dipinjam
    private val onClickFinish: OnClickFinish  // Fungsi callback saat motor selesai dipinjam
) : RecyclerView.Adapter<ListMotorAdapterUser.ItemBorrowViewHolder>() {

    // ViewHolder untuk setiap item yang ditampilkan dalam RecyclerView
    inner class ItemBorrowViewHolder(private val binding: ItemBorrowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Fungsi untuk mengikat data Motor ke tampilan di dalam ViewHolder
        fun bind(data: Motor) {
            with(binding) {
                // Kondisi pengecekan status motor apakah tersedia atau sedang dipinjam
                if (data.status == true) { // Motor tersedia untuk dipinjam
                    Glide.with(root.context)
                        .load(data.image) // Menampilkan gambar motor
                        .into(imgMotor)

                    // Menampilkan tombol pinjam dan menyembunyikan tombol selesai pinjam
                    btnBorrowMotor.visibility = android.view.View.VISIBLE
                    btnFinishMotor.visibility = android.view.View.GONE
                } else if (data.status == false) { // Motor sudah dipinjam
                    imgMotor.visibility = android.view.View.GONE // Menyembunyikan gambar motor
                    btnBorrowMotor.visibility = android.view.View.GONE // Menyembunyikan tombol pinjam
                    btnFinishMotor.visibility = android.view.View.VISIBLE // Menampilkan tombol selesai pinjam
                }

                // Menampilkan nama dan plat motor
                tvMotorName.text = data.name
                tvMotorPlat.text = data.plat

                // Tombol pinjam diklik
                btnBorrowMotor.setOnClickListener {
                    onClickBorrow(data)  // Memanggil fungsi callback untuk memproses pinjaman motor
                }

                // Tombol selesai pinjam diklik
                btnFinishMotor.setOnClickListener {
                    onClickFinish(data)  // Memanggil fungsi callback untuk menyelesaikan peminjaman motor
                }
            }
        }
    }

    // Fungsi untuk membuat ViewHolder baru saat diperlukan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBorrowViewHolder {
        // Menggunakan binding untuk meng-inflate layout item_motor_user.xml sebagai item tampilan
        val binding = ItemBorrowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemBorrowViewHolder(binding)
    }

    // Mendapatkan ukuran daftar data untuk mengetahui jumlah item dalam RecyclerView
    override fun getItemCount(): Int {
        return listMotor.size
    }

    // Mengikat data Motor pada posisi tertentu ke ViewHolder
    override fun onBindViewHolder(holder: ItemBorrowViewHolder, position: Int) {
        listMotor[position]?.let { holder.bind(it) }  // Mengikat data motor pada posisi yang sesuai
    }

    // Fungsi untuk memperbarui daftar motor di adapter
    fun updateListMotor(newList: List<Motor>) {
        listMotor.clear()  // Menghapus semua data motor lama
        listMotor.addAll(newList)  // Menambahkan data motor baru
        notifyDataSetChanged()  // Memberitahukan adapter bahwa data telah diperbarui
    }
}

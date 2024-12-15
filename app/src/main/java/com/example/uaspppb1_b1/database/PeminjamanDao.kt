package com.example.uaspppb1_b1.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.uaspppb1_b1.network.Motor

@Dao
interface PeminjamanDao {

    // Fungsi untuk menyisipkan data Motor ke dalam tabel peminjaman, jika data sudah ada, maka akan diabaikan
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data: Motor)

    // Fungsi untuk memperbarui data Motor yang ada di dalam tabel peminjaman
    @Update
    fun update(data: Motor)

    // Fungsi untuk menghapus data Motor dari tabel peminjaman
    @Delete
    fun delete(data: Motor)

    // Fungsi untuk mengambil semua data Motor dari tabel peminjaman, diurutkan berdasarkan id secara ascending
    @get:Query("SELECT * from peminjaman ORDER BY id ASC")
    val allPeminjaman: LiveData<List<Motor>>

    // Fungsi untuk mengecek apakah data dengan id tertentu sudah ada di tabel peminjaman
    @Query("SELECT EXISTS(SELECT 1 FROM peminjaman WHERE id = :id)")
    fun isDataExists(id: String): Boolean
}

package com.example.uaspppb1_b1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.example.uaspppb1_b1.network.Motor

// Menandakan bahwa kelas ini adalah kelas database yang menggunakan Room, dengan entitas Motor
@Database(entities = [Motor::class], version = 1, exportSchema = false)
abstract class DB_Instance : RoomDatabase() {

    // Abstract function yang mengembalikan DAO untuk mengakses data peminjaman
    abstract fun PeminjamanDao(): PeminjamanDao?

    companion object {
        @Volatile
        private var INSTANCE: DB_Instance? = null // Variabel instance yang akan menyimpan objek DB_Instance

        // Fungsi untuk mendapatkan instance dari database, jika belum ada, maka akan dibuat
        fun getDatabase(context: Context): DB_Instance? {
            // Mengecek apakah INSTANCE sudah ada, jika belum maka membuatnya
            if (INSTANCE == null) {
                synchronized(DB_Instance::class.java) {
                    // Membuat instance baru dari database menggunakan Room databaseBuilder
                    INSTANCE = databaseBuilder(
                        context.applicationContext, // Menggunakan context aplikasi
                        DB_Instance::class.java,    // Menentukan kelas database
                        "database"                  // Nama file database
                    )
                        .build() // Membangun dan menyimpan instance database
                }
            }
            // Mengembalikan instance yang sudah ada atau yang baru dibuat
            return INSTANCE
        }
    }
}

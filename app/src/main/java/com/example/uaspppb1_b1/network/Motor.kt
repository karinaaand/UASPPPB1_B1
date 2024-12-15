package com.example.uaspppb1_b1.network

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// Entity untuk data Motor, yang akan disimpan di dalam tabel 'peminjaman' pada database lokal
@Entity(tableName = "peminjaman")
data class Motor(
    // ID motor sebagai primary key, diserialisasi dari field '_id' pada JSON
    @PrimaryKey
    @SerializedName("_id") val id: String,

    // Nama motor, diserialisasi dari field 'name' pada JSON
    @SerializedName("name") val name: String,

    // Plat nomor motor, diserialisasi dari field 'plat' pada JSON
    @SerializedName("plat") val plat: String,

    // Tipe motor, diserialisasi dari field 'type' pada JSON
    @SerializedName("type") val type: String,

    // URL atau path gambar motor, diserialisasi dari field 'image' pada JSON
    @SerializedName("image") val image: String,

    // Status motor (tersedia atau tidak), diserialisasi dari field 'status' pada JSON
    @SerializedName("status") val status: Boolean
)

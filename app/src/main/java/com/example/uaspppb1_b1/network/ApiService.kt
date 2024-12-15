package com.example.uaspppb1_b1.network

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Fungsi untuk mendapatkan daftar motor dari API
    @GET("motor")
    fun getMotors(): Call<List<Motor>>  // Mengembalikan Call dengan tipe List<Motor> untuk mendapatkan data motor

    // Fungsi untuk membuat data motor baru melalui request POST
    @POST("motor")
    fun createMotor(@Body rawJson: RequestBody): Call<Void>  // Menerima body JSON sebagai RequestBody, mengirim data untuk membuat motor baru

    // Fungsi untuk memperbarui data motor berdasarkan id tertentu
    @POST("motor/{id}")
    fun updateMotor(@Path("id") id: String?, @Body rawJson: RequestBody): Call<Void>  // Menerima id motor dan body JSON untuk memperbarui motor

    // Fungsi untuk menghapus data motor berdasarkan id
    @DELETE("motor/{id}")
    fun deleteMotor(@Path("id") id: String?): Call<Void>  // Menerima id motor yang akan dihapus
}

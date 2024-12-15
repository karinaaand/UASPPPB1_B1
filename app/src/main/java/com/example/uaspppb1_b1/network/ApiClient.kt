package com.example.uaspppb1_b1.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Fungsi untuk mendapatkan instance dari ApiService
    fun getInstance(): ApiService {
        // Membuat interceptor untuk log request dan response HTTP
        val mHttpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY) // Menampilkan body dari request dan response

        // Membuat OkHttpClient dan menambahkan interceptor
        val mOkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(mHttpLoggingInterceptor) // Menambahkan interceptor ke client
            .build()

        // Membangun objek Retrofit dengan konfigurasi
        val builder = Retrofit.Builder()
            .baseUrl("https://ppbo-api.vercel.app/MQAX0/") // Menentukan URL dasar API
            .addConverterFactory(GsonConverterFactory.create()) // Menambahkan converter untuk parsing JSON ke objek
            .client(mOkHttpClient) // Menyertakan OkHttpClient yang telah dikonfigurasi
            .build()

        // Mengembalikan instance ApiService untuk digunakan dalam panggilan API
        return builder.create(ApiService::class.java)
    }
}

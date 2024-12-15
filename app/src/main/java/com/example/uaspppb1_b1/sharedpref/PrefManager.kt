package com.example.uaspppb1_b1.sharedpref

import android.content.Context
import android.content.SharedPreferences

class PrefManager private constructor(context : Context){

    private val sharedPreferences: SharedPreferences

    companion object {
        // Nama file untuk menyimpan SharedPreferences
        private const val PREFS_FILENAME = "AuthAppPrefs"
        // Kunci untuk menyimpan data status login, username, password, dan role
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "user"
        private const val KEY_PASSWORD = "password"

        // Instance tunggal PrefManager dengan penguncian untuk thread-safety
        @Volatile
        private var instance: PrefManager? = null
        fun getInstance(context: Context): PrefManager {
            // Menggunakan double-check locking untuk memastikan hanya ada satu instance PrefManager
            return instance ?: synchronized(this) {
                instance ?: PrefManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    // Inisialisasi SharedPreferences untuk menyimpan data secara lokal
    init {
        sharedPreferences = context.getSharedPreferences(PREFS_FILENAME,
            Context.MODE_PRIVATE)
    }

    // Fungsi untuk menyimpan status login (true/false) ke SharedPreferences
    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn) // Menyimpan status login
        editor.apply() // Menyimpan perubahan
    }

    // Fungsi untuk memeriksa apakah pengguna sudah login atau belum
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) // Mengembalikan status login
    }

    // Fungsi untuk menyimpan username pengguna ke SharedPreferences
    fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USERNAME, username) // Menyimpan username
        editor.apply() // Menyimpan perubahan
    }

    // Fungsi untuk menyimpan password pengguna ke SharedPreferences
    fun savePassword(password: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_PASSWORD, password) // Menyimpan password
        editor.apply() // Menyimpan perubahan
    }

    // Fungsi untuk menyimpan peran (role) pengguna ke SharedPreferences
    fun saveRole(role: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_ROLE, role) // Menyimpan role
        editor.apply() // Menyimpan perubahan
    }

    // Fungsi untuk mendapatkan username yang tersimpan di SharedPreferences
    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: "" // Mengembalikan username atau string kosong
    }

    // Fungsi untuk mendapatkan password yang tersimpan di SharedPreferences
    fun getPassword(): String {
        return sharedPreferences.getString(KEY_PASSWORD, "") ?: "" // Mengembalikan password atau string kosong
    }

    // Fungsi untuk mendapatkan peran (role) pengguna yang tersimpan di SharedPreferences
    fun getRole(): String {
        return sharedPreferences.getString(KEY_ROLE, "") ?: "" // Mengembalikan role atau string kosong
    }

    // Fungsi untuk menghapus semua data yang tersimpan di SharedPreferences
    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear() // Menghapus semua data
        editor.apply() // Menyimpan perubahan
    }
}

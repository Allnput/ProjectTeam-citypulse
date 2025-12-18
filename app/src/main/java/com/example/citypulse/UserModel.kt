package com.example.citypulse

data class UserModel(
    val username: String = "",
    val nama: String = "",
    val email: String = "",
    val password: String = "" // Simpan password di database untuk verifikasi manual
)
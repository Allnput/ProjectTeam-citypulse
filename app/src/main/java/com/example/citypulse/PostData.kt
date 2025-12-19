package com.example.citypulse

data class PostData(
    val status: String = "",
    val category: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val imageUrl: String? = null,  // URL gambar opsional
    val userId: String = ""  // ID pengguna yang membuat postingan
)



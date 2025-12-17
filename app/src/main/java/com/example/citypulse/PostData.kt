package com.example.citypulse

data class PostData(
    val status: String,
    val category: String,
    val imageUrl: String? = null // URL gambar (opsional jika gambar dipilih)
)


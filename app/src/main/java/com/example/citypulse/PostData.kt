package com.example.citypulse

import android.net.Uri // Pastikan URI sudah diimpor

data class PostData(
    var title: String = "",
    var message: String = "",
    var status: String = "",
    var category: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var imageUrl: String? = null,
    var userId: String = "",
    var timestamp: Long = 0L,
    var categoryColor: Int = 0,
    var postImageUri: Uri? = null // Tipe data Uri
)

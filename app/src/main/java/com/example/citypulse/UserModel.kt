package com.example.citypulse

data class UserModel(
    val uid: String = "", //ID UNIK DARI FIREBASE AUTH
    val nama: String = "", // NAMA USER
    val email: String = "",
)
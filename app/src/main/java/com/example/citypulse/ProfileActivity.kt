package com.example.citypulse

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserLocation: TextView
    private lateinit var imgProfile: ImageView
    private lateinit var btnBack: ImageView

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inisialisasi komponen UI
        tvUserName = findViewById(R.id.txtName)
        tvUserEmail = findViewById(R.id.txtUsername)
        tvUserLocation = findViewById(R.id.txtLocation)
        imgProfile = findViewById(R.id.imgProfile)
        btnBack = findViewById(R.id.btnBack)

        // Menyambung ke Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Ambil user ID dari Firebase Auth
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Ambil data pengguna dari Firebase Realtime Database
            database.child("users").child(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Ambil data pengguna dari snapshot
                        val user = snapshot.getValue(User::class.java)

                        // Menampilkan data pengguna di UI
                        tvUserName.text = user?.name ?: "Nama Pengguna"
                        tvUserEmail.text = user?.email ?: "Email tidak tersedia"
                        tvUserLocation.text = user?.location ?: "Lokasi tidak tersedia"

                        // Menampilkan foto profil
                        Glide.with(this@ProfileActivity)
                            .load(user?.profilePictureUrl)
                            .circleCrop()  // Membuat gambar berbentuk bulat
                            .into(imgProfile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Tangani error jika ada
                }
            })
        }

        // Tombol Kembali
        btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }

    // Data model untuk User
    data class User(
        var name: String = "",
        var email: String = "",
        var location: String = "",
        var profilePictureUrl: String = ""
    )
}

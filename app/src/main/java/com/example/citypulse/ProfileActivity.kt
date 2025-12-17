package com.example.citypulse

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Set layout yang sesuai

        // Inisialisasi UI components
        val btnBack: ImageView = findViewById(R.id.btnBack) // Pastikan ID sesuai
        val btnProfile: Button = findViewById(R.id.btnProfile)
        val txtName: TextView = findViewById(R.id.txtName)
        val txtUsername: TextView = findViewById(R.id.txtUsername)
        val txtLocation: TextView = findViewById(R.id.txtLoc4ation)
        val txtFollowing: TextView = findViewById(R.id.txtFollowing)
        val txtFollowers: TextView = findViewById(R.id.txtFollowers)
        val imgProfile: ImageView = findViewById(R.id.imgProfile)

        // Data pengguna, bisa diganti dengan data dinamis
        val name = "John Doe"
        val username = "@john_doe"
        val location = "New York"
        val following = 289
        val followers = 45

        // Set data pada TextView
        txtName.text = name
        txtUsername.text = username
        txtLocation.text = location
        txtFollowing.text = "$following Following"
        txtFollowers.text = "$followers Followers"

        // Logika untuk tombol profile
        btnProfile.setOnClickListener {
            // Misalnya, mengarahkan ke halaman Profile detail
            Toast.makeText(this, "Profile Button Clicked", Toast.LENGTH_SHORT).show()
        }

        // Aksi tombol back
        btnBack.setOnClickListener {
            onBackPressed() // Menutup activity atau kembali ke aktivitas sebelumnya
        }

        // Anda bisa menambahkan fungsionalitas lain seperti mengubah foto profil atau lainnya
    }
}

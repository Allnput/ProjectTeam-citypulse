package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.citypulse.databinding.ActivityProfileBinding
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Inisialisasi UI & Adapter
        setupRecyclerView()
        setupBottomNavigation() // Fungsi navigasi footer

        // 2. Load Data
        loadUserDataAndPosts()
    }

    private fun setupRecyclerView() {
        postsAdapter = PostsAdapter()
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPosts.adapter = postsAdapter
    }

    private fun loadUserDataAndPosts() {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getString("CURRENT_USER_ID", "")

        if (!userId.isNullOrEmpty()) {
            // Ambil Data Profil
            FirebaseDatabase.getInstance().getReference("users").child(userId)
                .get().addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(UserModel::class.java)
                    user?.let {
                        binding.txtName.text = it.nama
                        binding.txtUsername.text = it.email
                        binding.txtLocation.text = "Yogyakarta, Indonesia"
                    }
                }

            // Ambil Data Postingan (Filtered by User ID)
            FirebaseDatabase.getInstance().getReference("posts")
                .get().addOnSuccessListener { snapshot ->
                    val posts = snapshot.children.mapNotNull {
                        it.getValue(PostData::class.java)
                    }.filter { it.userId == userId }

                    postsAdapter.submitList(posts)
                }
        }
    }

    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            // Bersihkan tumpukan activity agar kembali ke home terasa bersih
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.navSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
            // Kita tidak memanggil finish() agar user bisa 'back' ke profile jika mau
        }

        binding.navProfile.setOnClickListener {
            // Sudah di halaman profile, tidak perlu aksi
        }
    }
}
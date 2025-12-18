package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.citypulse.databinding.ActivityRegisterBinding
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    // Inisialisasi Database Reference ke node "users"
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Sign Up (sesuaikan ID binding dengan XML Anda)
        binding.btnLogin.setOnClickListener {
            performRegister()
        }

        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        val nama = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val isChecked = binding.cbAgree.isChecked

        // 1. Validasi Input
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isChecked) {
            Toast.makeText(this, "Centang persetujuan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Encode Email karena Firebase Key tidak boleh mengandung karakter "."
        val userId = email.replace(".", "_")

        // 3. Cek apakah user sudah ada
        database.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "Email ini sudah terdaftar!", Toast.LENGTH_SHORT).show()
            } else {
                // 4. Buat objek user (Pastikan UserModel sudah memiliki field password)
                // Kita simpan password di DB karena tidak menggunakan Firebase Auth
                val userProfile = UserModel(userId, nama, email, password)

                database.child(userId).setValue(userProfile)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()

                        // Masukkan variabel userId (yang sudah di-replace "." nya) ke fungsi
                        saveUserSession(userId)

                        navigateToMaps()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal simpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Terjadi kesalahan koneksi", Toast.LENGTH_SHORT).show()
        }
    }

// Di dalam RegisterActivity.kt

    private fun saveUserSession(userId: String) { // Tambahkan parameter userId di sini
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean("IS_REGISTERED", true)
            putBoolean("IS_LOGGED_IN", true)

            // --- TAMBAHKAN BARIS INI ---
            // Simpan userId agar SettingActivity tahu node mana yang harus dihapus
            putString("CURRENT_USER_ID", userId)

            apply()
        }
    }

    private fun navigateToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        // Menghapus stack activity sebelumnya agar user tidak bisa 'back' ke Register
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
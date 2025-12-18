package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.citypulse.databinding.ActivityLoginBinding
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    // Ganti Firestore ke Realtime Database
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Logika Tombol Login (Sign In)
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // Navigasi ke RegisterActivity
        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val name = binding.etName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nama dan Password tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }

        database.orderByChild("nama").equalTo(name).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    var userFound = false

                    for (userSnapshot in snapshot.children) {
                        val userProfile = userSnapshot.getValue(UserModel::class.java)

                        if (userProfile != null && userProfile.password == password) {
                            // --- PERUBAHAN DI SINI ---
                            // Ambil KEY/ID dari database (misal: user_gmail_com)
                            val userIdFromDb = userSnapshot.key ?: ""

                            Toast.makeText(this, "Selamat datang, ${userProfile.nama}", Toast.LENGTH_SHORT).show()

                            // Kirim userId ke fungsi saveLoginStatus
                            saveLoginStatus(true, userIdFromDb)

                            navigateToMain()
                            userFound = true
                            break
                        }
                    }

                    if (!userFound) {
                        Toast.makeText(this, "Password salah!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User dengan nama '$name' tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Tambahkan parameter userId
    private fun saveLoginStatus(isLoggedIn: Boolean, userId: String) {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putBoolean("IS_LOGGED_IN", isLoggedIn)
            putBoolean("IS_REGISTERED", true)

            // --- PENTING: Simpan ID agar bisa dihapus di SettingActivity ---
            putString("CURRENT_USER_ID", userId)

            apply()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
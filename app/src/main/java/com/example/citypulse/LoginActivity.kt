package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.citypulse.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inisialisasi Firebase & View Binding
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. HAPUS checkExistingSession() dari sini.
        // Kita ingin user selalu melihat layar login.

        // 3. Logika Tombol Login (Sign In)
        binding.btnLogin.setOnClickListener {
            performLogin()
        }

        // 4. Navigasi ke RegisterActivity (Jika belum punya akun)
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

        // 5. Cari Email di Firestore berdasarkan Nama
        db.collection("users")
            .whereEqualTo("nama", name)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "User dengan nama '$name' tidak ditemukan.", Toast.LENGTH_SHORT).show()
                } else {
                    val email = documents.documents[0].getString("email")

                    if (email != null) {
                        // 6. Login menggunakan Email + Password
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    // Mengubah dokumen Firestore langsung kembali menjadi objek UserModel
                                    val userProfile = documents.documents[0].toObject(UserModel::class.java)
                                    Toast.makeText(this, "Selamat datang, ${userProfile?.nama}", Toast.LENGTH_SHORT).show()

                                    saveLoginStatus(true)
                                    navigateToMain()
                                } else {
                                    Toast.makeText(this, "Password salah atau terjadi gangguan.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("IS_LOGGED_IN", isLoggedIn).apply()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
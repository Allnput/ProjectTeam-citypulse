package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.citypulse.databinding.ActivityRegisterBinding // Pastikan nama binding sesuai XML Anda
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // PENTING: Panggil inflate sebelum setContentView
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Sesuaikan dengan ID di XML Anda (tadi di gambar tulisannya SIGN UP)
        binding.btnLogin.setOnClickListener {
            performRegister()
        }

        binding.tvToLogin.setOnClickListener {
            // Jika Anda ingin ke LoginActivity:
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        val nama = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val isChecked = binding.cbAgree.isChecked

        // Validasi Dasar
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            binding.etName.error = "Nama wajib diisi" // Menggunakan .error lebih user-friendly
            return
        }

        if (!isChecked) {
            Toast.makeText(this, "Centang persetujuan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan Loading (Opsional tapi disarankan agar user tidak tekan tombol berkali-kali)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: ""
                    val userProfile = UserModel(uid, nama, email)

                    // Simpan ke Firestore
                    db.collection("users").document(uid)
                        .set(userProfile)
                        .addOnSuccessListener {
                            saveLoginStatus(true)
                            navigateToMaps() // Langsung ke Maps
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Database Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Cek jika error karena email sudah ada atau format salah
                    Toast.makeText(this, "Auth Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            // Gunakan key yang konsisten dengan MainActivity (IS_LOGGED_IN)
            putBoolean("IS_LOGGED_IN", isLoggedIn)
            apply()
        }
    }

    private fun navigateToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        // Clear task agar user tidak bisa balik ke halaman Register setelah sukses
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
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
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isChecked) {
            Toast.makeText(this, "Centang persetujuan terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = email.replace(".", "_")

        database.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "Email ini sudah terdaftar!", Toast.LENGTH_SHORT).show()
            } else {
                val userProfile = UserModel(userId, nama, email, password)

                database.child(userId).setValue(userProfile)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
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

    private fun saveUserSession(userId: String) { // Tambahkan parameter userId di sini
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean("IS_REGISTERED", true)
            putBoolean("IS_LOGGED_IN", true)
            putString("CURRENT_USER_ID", userId)

            apply()
        }
    }

    private fun navigateToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
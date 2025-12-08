package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.citypulse.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Atur listener untuk tombol login
        binding.btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            return
        }

        if (email == "user@city.com" && password == "123456") {

            // Login Berhasil
            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

            // 3. Simpan Status Login
            saveLoginStatus(true)

            // 4. Navigasi ke MainActivity
            navigateToMain()

        } else {
            // Login Gagal
            Toast.makeText(this, "Email atau Password salah.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        // Harus sama dengan nama yang Anda gunakan di MainActivity
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putBoolean("IS_LOGGED_IN", isLoggedIn)
            apply() // Menyimpan perubahan secara asynchronous
        }
    }
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
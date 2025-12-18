package com.example.citypulse

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Splash Screen (Harus sebelum super.onCreate)
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Ambil data dari SharedPreferences
        val sharedPrefs = getSharedPreferences("USER_PREFS", MODE_PRIVATE)
        val isRegistered = sharedPrefs.getBoolean("IS_REGISTERED", false)
        val isLoggedIn = sharedPrefs.getBoolean("IS_LOGGED_IN", false)

        // 3. Logika Routing
        when {
            !isRegistered -> navigateToRegister()
            isRegistered && !isLoggedIn -> navigateToLogin()
            else -> navigateToMaps()
        }

        // 4. Selesaikan MainActivity agar tidak bisa kembali ke layar kosong
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        // Tambahkan flag agar backstack bersih
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.citypulse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (checkLoginStatus()) {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

        } else {
            navigateToLogin()
        }
    }
    private fun checkLoginStatus(): Boolean {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)

        return sharedPrefs.getBoolean("IS_LOGGED_IN", false)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
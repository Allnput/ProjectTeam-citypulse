package com.example.citypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (isUserRegistered()) {
            navigateToMaps()
        } else {
            navigateToRegister()
        }
        finish()
    }

    private fun isUserRegistered(): Boolean {
        val sharedPrefs = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("IS_REGISTERED", false)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMaps() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}